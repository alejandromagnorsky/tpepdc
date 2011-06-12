import java.util.List;

import junit.framework.TestCase;
import model.User;

import org.junit.Test;

import settings.EraseSettings;
import settings.UserSettings;
import settings.XMLSettingsDAO;

public class TestXMLDAO extends TestCase {

	@Test
	public void testLoad() {
		XMLSettingsDAO loader = new XMLSettingsDAO("settings.xml");
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

		// loader.marshal("testeando.xml");
	}

	@Test
	public void testSave() {
		XMLSettingsDAO dao = new XMLSettingsDAO("test.xml");

		User user = new User();
		user.setSettings(new UserSettings());
		user.getSettings().setEraseSettings(new EraseSettings());
		user.getSettings().setMaxLogins(10);
		user.getSettings().setLeet(true);

		user.setName("Vegeta");

		dao.saveUser(user);
		dao.commit();

		// Reload all data
		try {
			dao.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(dao.getUserList().contains(user), true);
		assertEquals(dao.getUserList().get(0).getSettings().isLeet().booleanValue(), true);

	}
}
