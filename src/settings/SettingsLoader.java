package settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import model.User;
import dao.Settings;

public class SettingsLoader {

	String filename;
	Settings settings;

	public SettingsLoader(String filename) {
		this.filename = filename;
		this.settings = null;
	}

	public void load() throws FileNotFoundException, JAXBException {
		InputStream input = null;
		input = new FileInputStream("settings.xml");
		settings = unmarshal(Settings.class, input);
	}

	public List<User> getUserList() {
		List<User> out = new ArrayList<User>();

		for (int i = 0; i < settings.getUser().size(); i++) {
			model.User user = new model.User();
			user.setName(settings.getUser().get(i).getName());

			// UserSettings userSettings;
		}

		return out;
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
