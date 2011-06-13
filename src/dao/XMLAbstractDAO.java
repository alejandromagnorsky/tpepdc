package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

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

import org.xml.sax.SAXException;

import proxy.POP3Proxy;

public abstract class XMLAbstractDAO<T> {

	private String dataFilename, schemaFilename;
	protected T rootElement;

	public XMLAbstractDAO(String dataFilename, String schemaFilename) {
		this.dataFilename = dataFilename;
		this.schemaFilename = schemaFilename;
		rootElement = createRoot();
	}

	// This is the final commit to save the data
	public void commit() {
		try {
			marshal(dataFilename);
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error saving configuration file");
		}
	}

	public void load() throws FileNotFoundException, JAXBException {
		InputStream input = null;
		input = new FileInputStream(dataFilename);
		rootElement = unmarshal(input, new File(schemaFilename));

		if (rootElement == null)
			throw new IllegalAccessError("Error reading XML file "
					+ dataFilename + "with schema " + schemaFilename + ".");
	}

	protected abstract T createRoot();

	private void marshal(String filename) throws JAXBException,
			FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(rootElement.getClass()
				.getPackage().getName());

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(rootElement, new FileOutputStream(filename));
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
				POP3Proxy.logger.fatal("Error loading schema file");
			}

			u.setSchema(schema);
			u.setEventHandler(vec);

			@SuppressWarnings("unchecked")
			JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
			return doc.getValue();
		} catch (JAXBException e) {
		} finally {
			if (vec != null && vec.hasEvents()) {

				POP3Proxy.logger.fatal("Error validating XML file");
				
				for (ValidationEvent ve : vec.getEvents()) {
					String msg = ve.getMessage();
					ValidationEventLocator vel = ve.getLocator();
					int line = vel.getLineNumber();
					int column = vel.getColumnNumber();
					POP3Proxy.logger.fatal("Line: " + line + ", Column: " + column
							+ ": " + msg);
				}
			}
		}
		return null;
	}

}
