package model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.XMLLoginLogDAO;

public final class AccessControl {

	private static boolean rangeContainsSchedule(Range<Integer> range,
			Integer schedule) {

		// If unbounded range, return true (infinite range)
		if (range.getFrom() == null && range.getTo() == null)
			return true;

		// If valid, non null range, return true
		if (range.getFrom() != null && range.getTo() != null
				&& range.getFrom() <= schedule && range.getTo() >= schedule)
			return true;

		// If unbounded from left and valid, return true
		if (range.getFrom() == null && range.getTo() >= schedule)
			return true;

		// If unbounded from right and valid, return true
		if (range.getTo() == null && range.getFrom() <= schedule)
			return true;

		return false;
	}

	public static boolean hourIsOutOfRange(User user) {
		if (user != null && user.getSettings() != null) {

			// If list is empty, user can delete
			boolean inRange = user.getSettings().getScheduleList().isEmpty();

			for (Range<Integer> range : user.getSettings().getScheduleList())
				inRange = inRange
						|| rangeContainsSchedule(range,
								new DateTime().getMinuteOfDay());

			return !inRange;
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

			if (qty < maxLogins || maxLogins == -1)
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
