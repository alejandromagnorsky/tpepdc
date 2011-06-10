package settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import dao.Settings;

public class XMLTest {

	public void main(String args[]) {
		System.out.println("hola");

		InputStream input = null;

		try {
			input = new FileInputStream("settings.xml");
		} catch (FileNotFoundException e1) {
			System.out.println("Error cargando archivo de configuracion.");
			e1.printStackTrace();
		}

		if (input == null)
			return;

		Settings s = null;
		try {
			s = unmarshal(Settings.class, input);
		} catch (JAXBException e) {
			System.out
					.println("Error levantando datos del archivo de configuracion.");
			e.printStackTrace();
		}

		System.out.println(s.getUser().get(0).getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream)
			throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		javax.xml.bind.Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
		return doc.getValue();
	}

}
