package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import model.User;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import dao.XMLEraseSettings;
import dao.XMLSettings;
import dao.XMLUser;

public class XMLSettingsLoader {

	String filename;
	XMLSettings settings;

	public static void main(String args[]) {
		XMLSettingsLoader loader = new XMLSettingsLoader("settings.xml");
		try {
			loader.load();
		} catch (Exception e) {
			return;
		}

		List<User> users = loader.getUserList();
		
		System.out.println("IP Blacklist:");
		System.out.println(loader.getBlacklistIP());

		// DEBUG
		for (User u : users) {
			System.out.println("Usuario: " + u);

			System.out.println("User settings ------------------");
			System.out.println(u.getSettings());

			System.out.println("Erase settings ------------------");
			System.out.println(u.getSettings().getEraseSettings());
			System.out
					.println("--------------------------------------------------");
		}
	}

	public XMLSettingsLoader(String filename) {
		this.filename = filename;
		this.settings = null;
	}

	public void load() throws FileNotFoundException, JAXBException {
		InputStream input = null;
		input = new FileInputStream("settings.xml");
		settings = unmarshal(XMLSettings.class, input, new File(
				"src/settings.xsd"));

		if (settings == null)
			throw new IllegalAccessError("Error reading XML file.");
	}

	// TODO esto puede pinchar, falta guardar algo y levantarlo para ver que
	// onda
	private DateTime convertToDateTime(XMLGregorianCalendar calendar) {
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

	// Unmarshal and validate XML file
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream,
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
					System.err.println("Line: " + line + ", Column: " + column + ": "
							+ msg);
				}
			}
		}
		return null;
	}
}
