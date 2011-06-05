import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import settings.IPBlacklist;
import settings.ScheduleRestriction;
import settings.User;


public class AccessControlFilter implements Filter {

	private User user;
	private IPBlacklist ipBlackList;
	
	public AccessControlFilter(User user, IPBlacklist ipBlackList) {
		this.user = user;
		this.ipBlackList = ipBlackList;
	}
	
	public void apply(Message message) {
		//TODO exceedsMaxLogins de donde saca la cantidad? --> modificar el 2 de abajo
		//TODO de donde saco el ip? --> modificar el string vacio de abajo
		if(hourIsOutOfRange() || exceedsMaxLogins(2) || ipIsDenied("")) {
			//TODO tirar excepcion?
		}
	}
	
	//TODO permitir minutos?
	public boolean hourIsOutOfRange() {
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
	
	//TODO el currentQuantity de donde sale? va en el xml?
	public boolean exceedsMaxLogins(int currentQuantity) {
		if(currentQuantity >= user.getMaxLogins()) {
			return true;
		}
		return false;
	}
	
	public boolean ipIsDenied(String ip) {
		return this.ipBlackList.getIp().contains(ip);
	}

}
