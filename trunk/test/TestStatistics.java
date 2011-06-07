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
		assertEquals(5, Statistics.getAccessQuant(user1));
		assertEquals(4, Statistics.getAccessQuant(user2));
		assertEquals(9, Statistics.getAccessHistogram().last().getQuant());
		assertEquals(5, Statistics.getAccessHistogram(user1).last().getQuant());
		assertEquals(4, Statistics.getAccessHistogram(user2).last().getQuant());
		

		for (Statistics.Access access : Statistics.getAccessHistogram()) {
			System.out.println("Fecha: " + access.getDate().getDayOfMonth() + "/"
					+ access.getDate().getMonthOfYear() + "/"
					+ access.getDate().getYear() + " - Accesos: " + access.getQuant());
		}
		for (Statistics.Access access : Statistics.getAccessHistogram(user1)) {
			System.out.println("Usuario: "+user1.getName()+" - Fecha: " + access.getDate().getDayOfMonth() + "/"
					+ access.getDate().getMonthOfYear() + "/"
					+ access.getDate().getYear() + " - Accesos: " + access.getQuant());
		}
	}

}
