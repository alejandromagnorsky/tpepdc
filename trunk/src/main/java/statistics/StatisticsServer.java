package statistics;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import proxy.POP3Proxy;
import proxy.handler.StatisticsConnectionHandler;


public class StatisticsServer implements Runnable {

	private static int STATISTICS_PORT;
	private static Logger logger = Logger.getLogger("logger");
	
	public StatisticsServer(){
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("resources/proxy.properties"));
			STATISTICS_PORT = Integer.valueOf(prop
					.getProperty("statistics_port"));
		} catch (Exception e) {
			logger.fatal("Could not read properties file. Setting 9008 as statistics port server...");
			STATISTICS_PORT = 9008;
		}
	}
	
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
			POP3Proxy.logger.fatal("Error connecting Statistics Server");
		}
		
	}
}
