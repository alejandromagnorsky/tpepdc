import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;


public class StatisticsServer implements Runnable {

	private static int STATISTICS_PORT = 9008;
	private static Logger logger = Logger.getLogger("logger");
	
	public void run() {
		try {
			ServerSocket server = new ServerSocket(STATISTICS_PORT);
			logger.info("Statistics server listening in port "+STATISTICS_PORT);
			while(true){
				Socket socket = server.accept();
				logger.info("Connected to host "+socket.getInetAddress()+":"+socket.getPort());
				Thread thread = new Thread(new StatisticsConnectionHandler(socket));
				thread.setName("StatisticsHandler"+socket.getInetAddress()+":"+socket.getPort());
				thread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
