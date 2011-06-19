package statistics;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import model.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Statistics {

	// Global statistics
	private static long accessQuant;
	private static long bytesTransfered;
	private static long redQuant;
	private static long deletedQuant;
	private static long listedQuant;
	private static SortedSet<Access> accessHistogram = new TreeSet<Access>();

	// User statistics
	private static Map<User, Long> accessQuantPerUser = new HashMap<User, Long>();
	private static Map<User, Long> bytesTransferedPerUser = new HashMap<User, Long>();
	private static Map<User, Long> redQuantPerUser = new HashMap<User, Long>();
	private static Map<User, Long> deletedQuantPerUser = new HashMap<User, Long>();
	private static Map<User, Long> listedQuantPerUser = new HashMap<User, Long>();
	private static Map<User, SortedSet<Access>> accessHistogramPerUser = new HashMap<User, SortedSet<Access>>();

	public static class Access implements Comparable<Access> {
		private LocalDate date;
		private long quant;

		public Access(DateTime date) {
			this.date = new LocalDate(date.getYear(), date.getMonthOfYear(),
					date.getDayOfMonth());
			this.quant = 0;
		}

		public LocalDate getDate(){
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

	public synchronized static void addAccess(User user) {
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

	public synchronized static void addBytesTransfered(User user, Long bytes) {
		bytesTransfered += bytes;
		addQuant(bytesTransferedPerUser, user, bytes);
	}

	public synchronized static void addRed(User user) {
		redQuant++;
		add(redQuantPerUser, user);
	}

	public synchronized static void addDeleted(User user) {
		deletedQuant++;
		add(deletedQuantPerUser, user);
	}
	
	public synchronized static void addListed(User user) {
		listedQuant++;
		add(listedQuantPerUser, user);
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
	
	public static long getListedQuant() {
		return listedQuant;
	}	
	
	public static SortedSet<Access> getAccessHistogram() {
		return accessHistogram;
	}

	public static long getAccessQuant(User user) {
		return (accessQuantPerUser.get(user) == null)? 0 : accessQuantPerUser.get(user);
	}

	public static long getBytesTransfered(User user) {
		return (bytesTransferedPerUser.get(user) == null)? 0 : bytesTransferedPerUser.get(user);
	}

	public static long getRedQuant(User user) {
		return (redQuantPerUser.get(user) == null)? 0 : redQuantPerUser.get(user);
	}

	public static long getDeletedQuant(User user) {
		return (deletedQuantPerUser.get(user) == null)? 0 : deletedQuantPerUser.get(user);
	}
	
	public static long getListedQuant(User user) {
		return (listedQuantPerUser.get(user) == null)? 0 : listedQuantPerUser.get(user);
	}

	public static SortedSet<Access> getAccessHistogram(User user) {
		return accessHistogramPerUser.get(user);
	}

}