package dao;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import model.EraseSettings;
import model.User;
import model.UserSettings;

import org.joda.time.DateTime;

import dao.settings.ObjectFactory;
import dao.settings.XMLEraseSettings;
import dao.settings.XMLIPBlacklist;
import dao.settings.XMLSettings;
import dao.settings.XMLUser;

public class XMLSettingsDAO extends XMLAbstractDAO<XMLSettings> {

	private XMLSettingsDAO() {
		super("resources/settings.xml", "resources/settings.xsd", "settings.xml",
				"settings.schema");
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
		return objFact.createXMLSettings();
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

		out.getSize().setFrom(xmlErase.getSizeBytes().getFrom());
		out.getSize().setTo(xmlErase.getSizeBytes().getTo());
		out.getDate().setFrom(convertToDateTime(xmlErase.getDate().getFrom()));
		out.getDate().setTo(convertToDateTime(xmlErase.getDate().getTo()));
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
		out.getSchedule().setFrom(xmlUser.getSchedule().getFrom());
		out.getSchedule().setTo(xmlUser.getSchedule().getTo());
		out.setRotate(xmlUser.getTransformSettings().isRotate());
		out.setLeet(xmlUser.getTransformSettings().isLeet());

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
		xmlErase.getSizeBytes().setFrom(erase.getSize().getFrom());
		xmlErase.getSizeBytes().setTo(erase.getSize().getTo());

		// Java, making your life easier.
		xmlErase.getDate().setFrom(
				convertToXMLGregorianCalendar(erase.getDate().getFrom()));
		xmlErase.getDate().setTo(
				convertToXMLGregorianCalendar(erase.getDate().getTo()));
	}

	public void saveUser(User user) {
		XMLUser xmlUser = getXMLUser(user);
		XMLEraseSettings xmlErase = null;
		UserSettings userSettings = user.getSettings();

		// If updating,
		if (xmlUser != null) {
			xmlErase = xmlUser.getEraseSettings();
		} else {

			// If creating, construct proper structure, prevent null pointers...
			ObjectFactory objFact = new ObjectFactory();
			xmlErase = objFact.createXMLEraseSettings();
			xmlErase.setDate(objFact.createXMLDateRestriction());
			xmlErase.setSizeBytes(objFact.createXMLSizeRestriction());

			xmlUser = objFact.createXMLUser();
			xmlUser.setName(user.getName());
			xmlUser.setEraseSettings(xmlErase);
			xmlUser.setSchedule(objFact.createXMLScheduleRestriction());
			xmlUser.setTransformSettings(objFact.createXMLTransformation());

			// And add the newly constructed user to root
			rootElement.getUser().add(xmlUser);
		}

		if (user.getSettings() != null) {
			xmlUser.setMaxLogins(userSettings.getMaxLogins());
			xmlUser.setServer(userSettings.getServer());
			xmlUser.getTransformSettings().setLeet(userSettings.isLeet());
			xmlUser.getTransformSettings().setRotate(userSettings.isRotate());
			xmlUser.getSchedule().setFrom(userSettings.getSchedule().getFrom());
			xmlUser.getSchedule().setTo(userSettings.getSchedule().getTo());

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
		return empty; // default, on memory, empty user
	}

}
