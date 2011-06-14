import junit.framework.TestCase;
import model.EraseSettings;
import model.User;
import model.UserSettings;

import org.joda.time.LocalDate;
import org.junit.Test;

import dao.XMLLoginLogDAO;
import dao.XMLSettingsDAO;

public class TestXMLDAO extends TestCase {

	@Test
	public void testLoginSaveLoad() {

		XMLLoginLogDAO dao = new XMLLoginLogDAO("test_loginLog.xml",
				"src/loginLog.xsd");

		User user = new User("Krillin");

		User otherUser = new User("Piccoro");

		dao.saveLogin(user, new LocalDate(2010, 10, 23), 0);
		dao.saveLogin(user, new LocalDate(2010, 10, 23), 3);
		dao.saveLogin(user, new LocalDate(2010, 10, 23), 5);
		dao.saveLogin(user, new LocalDate(2010, 10, 23), 6);

		dao.saveLogin(otherUser, new LocalDate(), 5);

		dao.commit();

		XMLLoginLogDAO otherDao = new XMLLoginLogDAO("test_loginLog.xml",
				"src/loginLog.xsd");
		try {
			otherDao.load();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		assertEquals(
				otherDao.getUserLogins(user, new LocalDate(2010, 10, 23)) == 6,
				true);

		assertEquals(otherDao.getUserLogins(user, new LocalDate()) == 0, true);

		assertEquals(otherDao.getUserLogins(otherUser, new LocalDate()) == 5,
				true);

		dao.saveLogin(user, new LocalDate(), 6);
		dao.commit();

		try {
			otherDao.load();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		assertEquals(otherDao.getUserLogins(user, new LocalDate()) == 6, true);

	}

	@Test
	public void testSettingsLoad() {
		XMLSettingsDAO loader = new XMLSettingsDAO("test_settings.xml",
				"src/settings.xsd");
		try {
			loader.load();
		} catch (Exception e) {
			return;
		}

		// List<User> users = loader.getUserList();
		//
		// System.out.println("IP Blacklist:");
		// System.out.println(loader.getBlacklistIP());
		//
		// // DEBUG
		// for (User u : users) {
		// System.out.println("Usuario: " + u);
		//
		// System.out.println("User settings ------------------");
		// System.out.println(u.getSettings());
		//
		// System.out.println("Erase settings ------------------");
		// System.out.println(u.getSettings().getEraseSettings());
		// System.out
		// .println("--------------------------------------------------");
		// }
	}

	@Test
	public void testSettingsSave() {
		XMLSettingsDAO dao = new XMLSettingsDAO("test_settings.xml",
				"src/settings.xsd");

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
