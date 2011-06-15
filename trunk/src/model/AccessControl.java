package model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import proxy.POP3Proxy;
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

		XMLLoginLogDAO dao = XMLLoginLogDAO.getInstance();

		try {
			dao.load();
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error loading Logins");
		}
		int qty = dao.getUserLogins(user, today);

		if (qty < maxLogins) {
			dao.saveLogin(user, today, qty + 1);
			dao.commit(); // TODO OJO CON ESTOOO
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
