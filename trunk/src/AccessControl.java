import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import settings.IPBlacklist;
import settings.ScheduleRestriction;
import settings.User;


public final class AccessControl {

	//TODO permitir minutos?
	public static boolean hourIsOutOfRange(User user) {
		ScheduleRestriction restriction = user.getSchedule();
		int from = restriction.getFrom().getHour();
		int to = restriction.getTo().getHour();
		
		if(to < from || to > 24 || from > 24) {
			throw new IllegalArgumentException();
		}
		DateFormat df = new SimpleDateFormat("HH");
		int now = Integer.valueOf(df.format(new Date()));
		if(from > now || to < now) {
			return true;
		}
		return false;
	}
	
	//TODO ver si se pueden manejar mejor las fechas
	public static boolean exceedsMaxLogins(User user) {
		if(user.getLoginsPerDay().getDate().getDay() == new Date().getDay() &&
				user.getLoginsPerDay().getDate().getMonth() == new Date().getMonth() &&
				user.getLoginsPerDay().getDate().getYear() == new Date().getYear()) {
			
			if(user.getLoginsPerDay().getTimes() >= user.getMaxLogins()) {
				return true;
			}
			
			
		}
		user.getLoginsPerDay().getDate().setDay(new Date().getDay());
		user.getLoginsPerDay().getDate().setMonth(new Date().getMonth());
		user.getLoginsPerDay().getDate().setYear(new Date().getYear());
		
		return false;
	}
	
	public static boolean ipIsDenied(IPBlacklist ipBlackList, String ip) {
		return ipBlackList.getIp().contains(ip);
	}

}
