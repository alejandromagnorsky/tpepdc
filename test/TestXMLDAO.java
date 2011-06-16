import java.util.List;

import junit.framework.TestCase;
import model.EraseSettings;
import model.User;
import model.UserSettings;

import org.junit.Test;

import dao.XMLSettingsDAO;

public class TestXMLDAO extends TestCase {
	
	@Test
	public void testSettingsLoad() {
		XMLSettingsDAO loader = XMLSettingsDAO.getInstance();

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

	@Test
	public void testSettingsSave() {
		XMLSettingsDAO dao = XMLSettingsDAO.getInstance();

		User user = new User("Vegeta");
		user.setSettings(new UserSettings());
		user.getSettings().setEraseSettings(new EraseSettings());
		user.getSettings().setMaxLogins(10);
		user.getSettings().setLeet(true);

		dao.saveUser(user);
		dao.commit();

		// Reload all data
		try {
			dao.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(dao.getUserList().contains(user), true);
		assertEquals(dao.getUserList().get(0).getSettings().isLeet()
				.booleanValue(), true);

	}
}
