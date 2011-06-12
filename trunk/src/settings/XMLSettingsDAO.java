package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import model.User;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import dao.ObjectFactory;
import dao.XMLEraseSettings;
import dao.XMLIPBlacklist;
import dao.XMLSettings;
import dao.XMLUser;

public class XMLSettingsDAO {

	String filename;
	XMLSettings settings;

	public XMLSettingsDAO(String filename) {
		this.filename = filename;

		// Create root by default
		ObjectFactory objFact = new ObjectFactory();
		settings = objFact.createXMLSettings();
	}

	public void load() throws FileNotFoundException, JAXBException {
		InputStream input = null;
		input = new FileInputStream(filename);
		settings = unmarshal(XMLSettings.class, input, new File(
				"src/settings.xsd"));

		if (settings == null)
			throw new IllegalAccessError("Error reading XML file.");
	}

	private DateTime convertToDateTime(XMLGregorianCalendar calendar) {
		if (calendar == null)
			return null;
		return new DateTime(calendar.toGregorianCalendar().getTimeInMillis());
	}

	private EraseSettings constructEraseSettings(int index) {
		EraseSettings out = new EraseSettings();

		XMLEraseSettings xmlErase = settings.getUser().get(index)
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
		XMLUser xmlUser = settings.getUser().get(index);

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
		for (int i = 0; i < settings.getUser().size(); i++) {
			User user = new User();
			user.setName(settings.getUser().get(i).getName());
			UserSettings userSettings = constructUserSettings(i);
			user.setSettings(userSettings);
			out.add(user);
		}
		return out;
	}

	public List<String> getBlacklistIP() {
		return settings.getBlacklist().getIp();
	}

	private XMLUser getXMLUser(User user) {
		for (XMLUser u : settings.getUser())
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
			settings.getUser().add(xmlUser);
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
		XMLIPBlacklist blacklist = settings.getBlacklist();

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
		for(String ip: ipList)
			saveBlacklistedIP(ip);
	}

	public void saveUserList(List<User> modifiedUsers) {
		for (User user : modifiedUsers)
			saveUser(user);
	}

	// This is the final commit to save the data
	public void commit() {
		marshal(filename);
	}

	private void marshal(String filename) {
		try {
			JAXBContext context = JAXBContext.newInstance(XMLSettings.class
					.getPackage().getName());

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(settings, new FileOutputStream(filename));
		} catch (Exception e) {
			System.err.println("Error saving configuration file.");
			System.out.println("--------------------------------");
			e.printStackTrace();

		}
	}

	// Unmarshal and validate XML file
	private <T> T unmarshal(Class<T> docClass, InputStream inputStream,
			File schemaFile) {

		ValidationEventCollector vec = new ValidationEventCollector();
		String packageName = docClass.getPackage().getName();
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();

			Schema schema = null;
			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				schema = sf.newSchema(schemaFile);
			} catch (SAXException saxe) {
				System.err.println("Error loading schema file");
			}

			u.setSchema(schema);
			u.setEventHandler(vec);

			@SuppressWarnings("unchecked")
			JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
			return doc.getValue();
		} catch (JAXBException e) {
		} finally {
			if (vec != null && vec.hasEvents()) {

				System.out.println("Error validating XML file.");
				System.out.println("--------------------------");

				for (ValidationEvent ve : vec.getEvents()) {
					String msg = ve.getMessage();
					ValidationEventLocator vel = ve.getLocator();
					int line = vel.getLineNumber();
					int column = vel.getColumnNumber();
					System.err.println("Line: " + line + ", Column: " + column
							+ ": " + msg);
				}
			}
		}
		return null;
	}
}
