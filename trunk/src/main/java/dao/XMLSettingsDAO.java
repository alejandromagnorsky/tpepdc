package dao;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import model.EraseSettings;
import model.Range;
import model.User;
import model.UserSettings;

import org.joda.time.DateTime;

import dao.settings.ObjectFactory;
import dao.settings.XMLDateRestriction;
import dao.settings.XMLEraseSettings;
import dao.settings.XMLIPBlacklist;
import dao.settings.XMLScheduleRestriction;
import dao.settings.XMLSettings;
import dao.settings.XMLSizeRestriction;
import dao.settings.XMLUser;

public class XMLSettingsDAO extends XMLAbstractDAO<XMLSettings> {

	private XMLSettingsDAO() {
		super("resources/settings.xml", "resources/settings.xsd",
				"settings.xml", "settings.schema");
		load();
	}

	private static XMLSettingsDAO instance = null;

	public static XMLSettingsDAO getInstance() {
		if (instance == null)
			instance = new XMLSettingsDAO();
		return instance;
	}

	@Override
	protected XMLSettings createRoot() {
		ObjectFactory objFact = new ObjectFactory();
		XMLSettings out = objFact.createXMLSettings();
		out.setBlacklist(objFact.createXMLIPBlacklist());
		return out;
	}

	private DateTime convertToDateTime(XMLGregorianCalendar calendar) {
		if (calendar == null)
			return null;
		return new DateTime(calendar.toGregorianCalendar().getTimeInMillis());
	}

	private EraseSettings constructEraseSettings(int index) {
		EraseSettings out = new EraseSettings();

		XMLEraseSettings xmlErase = rootElement.getUser().get(index)
				.getEraseSettings();

		for (XMLSizeRestriction r : xmlErase.getSizeBytes()) {
			Range<Integer> range = new Range<Integer>();
			range.setFrom(r.getFrom());
			range.setTo(r.getTo());
			out.addSizeRestriction(range);
		}

		for (XMLDateRestriction r : xmlErase.getDate()) {
			Range<DateTime> range = new Range<DateTime>();
			range.setFrom(convertToDateTime(r.getFrom()));
			range.setTo(convertToDateTime(r.getTo()));
			out.addDateRestriction(range);
		}

		out.setStructure(xmlErase.getStructure());

		for (String pattern : xmlErase.getHeaderPattern())
			out.addHeaderPattern(pattern);

		for (String content : xmlErase.getContentType())
			out.addContentHeader(content);

		for (String sender : xmlErase.getSender())
			out.addSender(sender);

		return out;
	}

	private UserSettings constructUserSettings(int index) {
		UserSettings out = new UserSettings();
		XMLUser xmlUser = rootElement.getUser().get(index);

		out.setServer(xmlUser.getServer());
		out.setMaxLogins(xmlUser.getMaxLogins());

		for (XMLScheduleRestriction r : xmlUser.getSchedule()) {
			Range<Integer> range = new Range<Integer>();
			range.setFrom(r.getFrom());
			range.setTo(r.getTo());
			out.addScheduleRestriction(range);
		}

		out.setRotate(xmlUser.getTransformSettings().isRotate());
		out.setLeet(xmlUser.getTransformSettings().isLeet());
		out.setExternal(xmlUser.getTransformSettings().getExternal());

		EraseSettings erase = constructEraseSettings(index);
		out.setEraseSettings(erase);

		return out;
	}

	public List<User> getUserList() {
		List<User> out = new ArrayList<User>();
		for (int i = 0; i < rootElement.getUser().size(); i++) {
			User user = new User(rootElement.getUser().get(i).getName());
			UserSettings userSettings = constructUserSettings(i);
			user.setSettings(userSettings);
			out.add(user);
		}
		return out;
	}

	public List<String> getBlacklistIP() {
		if (rootElement != null && rootElement.getBlacklist() != null)
			return rootElement.getBlacklist().getIp();
		return new ArrayList<String>();
	}

	private XMLUser getXMLUser(User user) {
		for (XMLUser u : rootElement.getUser())
			if (u.getName().equals(user.getName()))
				return u;
		return null;
	}

	private XMLGregorianCalendar convertToXMLGregorianCalendar(DateTime date) {
		if (date == null)
			return null;
		DatatypeFactory dtf;
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			return null;
		}
		XMLGregorianCalendar xgc = dtf.newXMLGregorianCalendar(date
				.toGregorianCalendar());
		return xgc;
	}

	private void saveEraseSettings(XMLEraseSettings xmlErase,
			EraseSettings erase) {
		xmlErase.setStructure(erase.getStructure());

		xmlErase.getContentType().clear();
		xmlErase.getContentType().addAll(erase.getContentTypes());
		xmlErase.getHeaderPattern().clear();
		xmlErase.getHeaderPattern().addAll(erase.getHeaderPattern());
		xmlErase.getSender().clear();
		xmlErase.getSender().addAll(erase.getSenders());

		xmlErase.getSizeBytes().clear();
		for (Range<Integer> range : erase.getSizeRestrictions()) {
			ObjectFactory objFact = new ObjectFactory();
			XMLSizeRestriction r = objFact.createXMLSizeRestriction();
			r.setFrom(range.getFrom());
			r.setTo(range.getTo());
			xmlErase.getSizeBytes().add(r);
		}

		xmlErase.getDate().clear();
		for (Range<DateTime> range : erase.getDateRestrictions()) {
			ObjectFactory objFact = new ObjectFactory();
			XMLDateRestriction r = objFact.createXMLDateRestriction();
			r.setFrom(convertToXMLGregorianCalendar(range.getFrom()));
			r.setTo(convertToXMLGregorianCalendar(range.getTo()));
			xmlErase.getDate().add(r);
		}
	}

	public void saveUser(User user) {
		XMLUser xmlUser = getXMLUser(user);
		XMLEraseSettings xmlErase = null;
		UserSettings userSettings = user.getSettings();

		// If updating,
		if (xmlUser != null) {
			xmlErase = xmlUser.getEraseSettings();
		} else {
			// If creating a user, construct proper structure, prevent null
			// pointers...
			ObjectFactory objFact = new ObjectFactory();
			xmlErase = objFact.createXMLEraseSettings();
			xmlUser = objFact.createXMLUser();
			xmlUser.setName(user.getName());
			xmlUser.setEraseSettings(xmlErase);
			xmlUser.setTransformSettings(objFact.createXMLTransformation());

			// And add the newly constructed user to root
			rootElement.getUser().add(xmlUser);
		}

		if (user.getSettings() != null) {
			xmlUser.setMaxLogins(userSettings.getMaxLogins());
			xmlUser.setServer(userSettings.getServer());
			xmlUser.getTransformSettings().setLeet(userSettings.isLeet());
			xmlUser.getTransformSettings().setRotate(userSettings.isRotate());
			xmlUser.getTransformSettings().setExternal(
					userSettings.getExternal());

			xmlUser.getSchedule().clear();
			for (Range<Integer> range : userSettings.getScheduleList()) {
				ObjectFactory objFact = new ObjectFactory();
				XMLScheduleRestriction r = objFact
						.createXMLScheduleRestriction();
				r.setFrom(range.getFrom());
				r.setTo(range.getTo());
				xmlUser.getSchedule().add(r);
			}

			saveEraseSettings(xmlErase, userSettings.getEraseSettings());
		}
	}

	public void saveBlacklistedIP(String ip) {
		XMLIPBlacklist blacklist = rootElement.getBlacklist();

		if (blacklist != null) {
			List<String> list = blacklist.getIp();
			if (!list.contains(ip))
				list.add(ip);
		} else {
			ObjectFactory objFact = new ObjectFactory();
			blacklist = objFact.createXMLIPBlacklist();
			blacklist.getIp().add(ip);
		}
	}

	public void clear() {
		rootElement = createRoot();
	}

	public void saveBlacklistIP(List<String> ipList) {
		for (String ip : ipList)
			saveBlacklistedIP(ip);
	}

	public void saveUserList(List<User> modifiedUsers) {
		for (User user : modifiedUsers)
			saveUser(user);
	}

	public User getUser(String username) {
		for (User user : getUserList())
			if (username.toUpperCase().equals(user.getName().toUpperCase()))
				return user;

		User empty = new User(username);
		empty.setSettings(new UserSettings());
		empty.getSettings().setEraseSettings(new EraseSettings());

		// Persist new users
		saveUser(empty);
		commit();

		return empty; // default, on memory, empty user
	}

}
