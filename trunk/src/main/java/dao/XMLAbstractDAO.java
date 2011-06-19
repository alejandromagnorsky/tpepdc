package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public abstract class XMLAbstractDAO<T> {

	protected static Logger logger = Logger.getLogger("logger");
	private String dataFilename, schemaFilename;
	protected T rootElement;

	protected XMLAbstractDAO(String data, String schema, String xmlProperty,
			String schemaProperty) {

		try {
			Properties prop = new Properties();
			// TODO Modificar cuando este bien el pom.xml
			// prop.load(POP3Proxy.class.getResourceAsStream("connection.properties"));
			prop.load(new FileInputStream("resources/proxy.properties"));

			dataFilename = prop.getProperty(xmlProperty);
			if (dataFilename == null) {
				logger.warn("Could not read property " + xmlProperty
						+ ". Setting default values for XML (" + data
						+ ") files...");
				dataFilename = data;
			}

			schemaFilename = prop.getProperty(schemaProperty);
			if (schemaFilename == null) {
				logger.warn("Could not read property " + schemaProperty
						+ ". Setting default values for schema (" + schema
						+ ") files...");
				schemaFilename = schema;
			}

		} catch (Exception e) {
			logger.fatal("Could not read properties file. Setting default values for XML("
					+ dataFilename
					+ ") and schema("
					+ schemaFilename
					+ ") files...");
			this.dataFilename = data;
			this.schemaFilename = schema;
		}
		rootElement = createRoot();
	}

	// This is the final commit to save the data
	public synchronized boolean commit() {
		return marshal(dataFilename);
	}

	private void persistRoot() {
		rootElement = createRoot();
		if (rootElement != null) {
			if (commit()) {
				logger.warn("XML empty root persisted.");
				return;
			} else
				logger.warn("XML empty root could not be persisted. Temporary root created.");
		} else
			logger.fatal("XML empty root could not be created nor persisted. Prepare for unforeseen consequences.");
	}

	public synchronized void load() {
		InputStream input = null;
		try {
			input = new FileInputStream(dataFilename);
		} catch (FileNotFoundException e) {
			logger.warn("XML file " + dataFilename
					+ " not found. Creating new empty file with base root...");
			File f = new File(dataFilename);
			try {
				f.createNewFile();
				input = new FileInputStream(dataFilename);
				persistRoot();
			} catch (IOException e1) {
				logger.fatal("Load FAILED: Could not create new empty file. Maybe privileges are needed?");
				return;
			}
		}
		rootElement = unmarshal(input, new File(schemaFilename));

		if (rootElement == null) {
			logger.warn("Creating valid empty XML file with root...");
			dataFilename = "." + dataFilename + ".valid";
			persistRoot();
			logger.info("XML File exists but is invalid and could not be loaded: an empty valid root was created to prevent future errors. Temporary file: "
					+ dataFilename);
			return;
		}

		logger.info("Loaded XML " + dataFilename + " with schema "
				+ schemaFilename + " ...");
	}

	protected abstract T createRoot();

	private boolean marshal(String filename) {
		try {
			JAXBContext context = JAXBContext.newInstance(rootElement
					.getClass().getPackage().getName());

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(filename);
			} catch (FileNotFoundException e) {
				logger.info("XML file not found. Creating new empty file...");
				File f = new File(dataFilename);
				try {
					f.createNewFile();
					output = new FileOutputStream(filename);
				} catch (IOException e1) {
					logger.fatal("Commit FAILED: Could not create new empty file. Maybe privileges are needed?");
					return false;
				}
			}
			m.marshal(rootElement, output);
		} catch (JAXBException e) {
			logger.fatal("Commit FAILED: JAXBException.");
			return false;
		}
		return true;
	}

	// Unmarshal and validate XML file
	private T unmarshal(InputStream inputStream, File schemaFile) {

		ValidationEventCollector vec = new ValidationEventCollector();
		String packageName = rootElement.getClass().getPackage().getName();
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();

			Schema schema = null;
			SchemaFactory sf = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				schema = sf.newSchema(schemaFile);
			} catch (SAXException saxe) {
				logger.fatal("Error loading schema file " + schemaFilename);
				return null;
			}

			u.setSchema(schema);
			u.setEventHandler(vec);

			@SuppressWarnings("unchecked")
			JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
			return doc.getValue();
		} catch (JAXBException e) {
		} finally {
			if (vec != null && vec.hasEvents()) {

				logger.fatal("Error validating XML file " + dataFilename
						+ " with schema " + schemaFilename
						+ ". XML is either invalid or empty.");
				for (ValidationEvent ve : vec.getEvents()) {
					String msg = ve.getMessage();
					ValidationEventLocator vel = ve.getLocator();
					int line = vel.getLineNumber();
					int column = vel.getColumnNumber();
					logger.fatal("Line: " + line + ", Column: " + column + ": "
							+ msg);
				}
			}
		}
		return null;
	}

}
