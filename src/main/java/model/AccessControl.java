package model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.XMLLoginLogDAO;

public final class AccessControl {

	public static boolean hourIsOutOfRange(User user) {
		if (user != null && user.getSettings() != null) {
			Range<Integer> range = user.getSettings().getSchedule();

			if (range == null || range.getFrom() == null
					|| range.getTo() == null)
				return false;

			int from = range.getFrom();
			int to = range.getTo();
			int now = new DateTime().getMinuteOfDay();

			return from > now || to < now;
		}
		return false;
	}

	public static boolean exceedsMaxLogins(User user) {
		if (user != null && user.getSettings() != null) {
			Integer maxLogins = user.getSettings().getMaxLogins();
			if (maxLogins == null)
				return false;
			LocalDate today = new LocalDate();
			XMLLoginLogDAO dao = XMLLoginLogDAO.getInstance();
			int qty = dao.getUserLogins(user, today);

			if (qty < maxLogins)
				return false;
			else
				return true;
		}
		return false;
	}

	public static boolean ipIsDenied(List<String> ipBlackList, String ip) {
		if (ipBlackList != null && ip != null && !ip.equals("")) {
			return ipBlackList.contains(ip);
		}
		return false;
	}

}
