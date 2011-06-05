import junit.framework.TestCase;

import org.junit.Test;

import settings.User;


public class TestStatistics extends TestCase {
	
	@Test
	public void testAccess(){
		User user1 = new User();
		User user2 = new User();
		user1.setName("user1");
		user2.setName("user2");
		Statistics.addAccess(user1);
		Statistics.addAccess(user1);
		Statistics.addAccess(user2);
		Statistics.addAccess(user1);
		Statistics.addAccess(user1);
		Statistics.addAccess(user2);
		Statistics.addAccess(user1);
		Statistics.addAccess(user2);
		Statistics.addAccess(user2);
		
		assertEquals(9, Statistics.getAccessQuant());
		assertEquals(5, Statistics.getAccessQuantPerUser().get(user1).longValue());
		assertEquals(4, Statistics.getAccessQuantPerUser().get(user2).longValue());
		assertEquals(9, Statistics.getAccessHistogram().last().getQuant());
		assertEquals(5, Statistics.getAccessHistogramPerUser().get(user1).last().getQuant());
		assertEquals(4, Statistics.getAccessHistogramPerUser().get(user2).last().getQuant());
		

		for (Statistics.Access access : Statistics.getAccessHistogram()) {
			System.out.println("Fecha: " + access.getDate().getDayOfMonth() + "/"
					+ access.getDate().getMonthOfYear() + "/"
					+ access.getDate().getYear() + " - Accesos: " + access.getQuant());
		}
		for(User user: Statistics.getAccessHistogramPerUser().keySet())
			for (Statistics.Access access : Statistics.getAccessHistogramPerUser().get(user)) {
				System.out.println("Usuario: "+user.getName()+" - Fecha: " + access.getDate().getDayOfMonth() + "/"
						+ access.getDate().getMonthOfYear() + "/"
						+ access.getDate().getYear() + " - Accesos: " + access.getQuant());
			}
	}

}
