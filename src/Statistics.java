import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;

import settings.User;

public class Statistics {

	// Global statistics
	private static long accessQuant;
	private static long bytesTransfered;
	private static long redQuant;
	private static long deletedQuant;
	private static SortedSet<Access> accessHistogram = new TreeSet<Access>();

	// User statistics
	// TODO hashCode e equals en User
	private static Map<User, Long> accessQuantPerUser = new HashMap<User, Long>();
	private static Map<User, Long> bytesTransferedPerUser = new HashMap<User, Long>();
	private static Map<User, Long> redQuantPerUser = new HashMap<User, Long>();
	private static Map<User, Long> deletedQuantPerUser = new HashMap<User, Long>();
	private static Map<User, SortedSet<Access>> accessHistogramPerUser = new HashMap<User, SortedSet<Access>>();

	public static class Access implements Comparable<Access> {
		private DateTime date;
		private long quant;

		public Access(DateTime date) {
			this.date = new DateTime(date.getYear(), date.getMonthOfYear(),
					date.getDayOfMonth(), 0, 0, 0, 0);
			this.quant = 0;
		}

		public DateTime getDate(){
			return date;
		}
		
		public long getQuant(){
			return quant;
		}
		
		public void setQuant(long quant) {
			this.quant = quant;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Access other = (Access) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			return true;
		}

		public int compareTo(Access other) {
			return this.date.compareTo(other.date);
		}
	}

	public static void addAccess(User user) {
		accessQuant++;
		add(accessQuantPerUser, user);

		addAccessToHistogram(accessHistogram);
		SortedSet<Access> histogram = accessHistogramPerUser.get(user);
		if (histogram == null) {
			histogram = new TreeSet<Access>();
			accessHistogramPerUser.put(user, histogram);
		}
		addAccessToHistogram(accessHistogramPerUser.get(user));

	}

	private static void addAccessToHistogram(SortedSet<Access> histogram) {
		Access access = null;
		if (!histogram.isEmpty())
			access = histogram.last();
		if (access == null || !access.equals(new Access(new DateTime())))
			access = new Access(new DateTime());
		access.setQuant(access.quant + 1);
		histogram.add(access);
	}

	public static void addBytesTransfered(User user, Long bytes) {
		bytesTransfered += bytes;
		addQuant(bytesTransferedPerUser, user, bytes);
	}

	public static void addRed(User user) {
		redQuant++;
		add(redQuantPerUser, user);
	}

	public static void addDeleted(User user) {
		deletedQuant++;
		add(deletedQuantPerUser, user);
	}

	private static void add(Map<User, Long> map, User user) {
		addQuant(map, user, Long.valueOf(1));
	}

	private static void addQuant(Map<User, Long> map, User user, Long quant) {
		Long tmp = map.get(user);
		map.put(user, (tmp == null) ? quant : tmp + quant);
	}

	
	public static long getAccessQuant() {
		return accessQuant;
	}

	public static long getBytesTransfered() {
		return bytesTransfered;
	}

	public static long getRedQuant() {
		return redQuant;
	}

	public static long getDeletedQuant() {
		return deletedQuant;
	}

	public static SortedSet<Access> getAccessHistogram() {
		return accessHistogram;
	}

	public static Map<User, Long> getAccessQuantPerUser() {
		return accessQuantPerUser;
	}

	public static Map<User, Long> getBytesTransferedPerUser() {
		return bytesTransferedPerUser;
	}

	public static Map<User, Long> getRedQuantPerUser() {
		return redQuantPerUser;
	}

	public static Map<User, Long> getDeletedQuantPerUser() {
		return deletedQuantPerUser;
	}

	public static Map<User, SortedSet<Access>> getAccessHistogramPerUser() {
		return accessHistogramPerUser;
	}

}
