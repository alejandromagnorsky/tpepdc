import java.util.List;
import java.util.Map;

import model.Range;
import model.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.XMLLoginLogDAO;

public final class AccessControl {

	public static boolean hourIsOutOfRange(User user) {
		Range<Integer> range = user.getSettings().getSchedule();
		int from = range.getFrom();
		int to = range.getTo();
		int now = new DateTime().getMinuteOfDay();

		// TODO ver esta validacion
		// if(to < from || to > 24 || from < 0) {
		// throw new IllegalArgumentException();
		// }

		if (from > now || to < now) {
			return true;
		}

		return false;
	}

	public static boolean exceedsMaxLogins(User user) {
		int maxLogins = user.getSettings().getMaxLogins();
		LocalDate today = new LocalDate();

		XMLLoginLogDAO dao = new XMLLoginLogDAO("test_loginLog.xml",
				"src/loginLog.xsd");
		Map<LocalDate, Integer> logins = dao.getUserLogins(user);
		Integer quantity = logins.get(today);

		// First login of day
		if (quantity == null) {
			dao.addLoginLog(user, today);
			return false;
		} else if (quantity < maxLogins) {
			// Update login
			dao.addLoginLog(user, today);
			return false;
		}
		return true;
	}

	public static boolean ipIsDenied(List<String> ipBlackList, String ip) {
		if (ipBlackList != null && ip != null && !ip.equals("")) {
			return ipBlackList.contains(ip);
		}
		return false;
	}

}
