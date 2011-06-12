import java.util.List;

import model.LoginsPerDay;
import model.Range;
import model.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;



public final class AccessControl {
	
	public static boolean hourIsOutOfRange(User user) {
		Range<Integer> range = user.getSettings().getSchedule();
		int from = range.getFrom();
		int to = range.getTo();
		int now = new DateTime().getMinuteOfDay();
		
		//TODO ver esta validacion
//		if(to < from || to > 24 || from < 0) {
//			throw new IllegalArgumentException();
//		}
		
		if(from > now || to < now) {
			return true;
		}
		
		return false;
	}

	public static boolean exceedsMaxLogins(User user, LoginsPerDay loginsPerDay) {
		int maxLogins = user.getSettings().getMaxLogins();
		LocalDate today = new LocalDate();
		
		if(loginsPerDay.getDate().isBefore(today)) {
			loginsPerDay.reset();
			return false;
		}
		
		if(loginsPerDay.getQuantity() >= maxLogins) {
			return true;
		}
		
		
		return false;
	}
	
	public static boolean ipIsDenied(List<String> ipBlackList, String ip) {
		if(ipBlackList != null && ip != null && !ip.equals("")) {
			return ipBlackList.contains(ip);
		}
		return false;
	}

}
