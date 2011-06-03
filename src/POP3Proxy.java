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
		
		try {
			ServerSocket server = new ServerSocket(POP3_PORT);
			
			while (true) {
				Socket socket = server.accept();
				Thread thread = new Thread(new POP3ConnectionHandler(socket));
				thread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
