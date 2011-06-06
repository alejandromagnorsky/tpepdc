import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * user: tpepdc@yahoo.com.ar
 * pass: 123456
 * 
 */

public class POP3Proxy {

	private static int POP3_PORT = 9999;
	private static Logger logger = Logger.getLogger("logger");
	
	public static void main(String args[]) {
		PropertyConfigurator.configure("resources/log4j.properties");
		logger.info("Logger started");
		
		Thread statistics = new Thread(new StatisticsServer());
		statistics.setName("StatisticsServer");
		statistics.start();
		try {
			ServerSocket server = new ServerSocket(POP3_PORT);
			logger.info("Proxy POP3 listening in port "+POP3_PORT);
			while (true) {
				Socket socket = server.accept();
				logger.info("Connected to host "+socket.getInetAddress()+":"+socket.getPort());
				Thread thread = new Thread(new POP3ConnectionHandler(socket));
				thread.setName("POP3Handler"+socket.getInetAddress()+":"+socket.getPort());
				thread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
