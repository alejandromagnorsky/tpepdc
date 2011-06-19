package proxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import proxy.handler.POP3ConnectionHandler;
import statistics.StatisticsServer;

public class POP3Proxy {

	private static int PROXY_PORT;
	public static String DEFAULT_SERVER;
	public static int DEFAULT_PORT;
	public static Logger logger = Logger.getLogger("logger");

	public static void main(String args[]) {
		try {
			PropertyConfigurator.configure("resources/log4j.properties");
		} catch (Exception e) {
			System.out.println("Error loading logger");
			return;
		}
		logger.info("Logger started");

		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("resources/proxy.properties"));
			DEFAULT_SERVER = prop.getProperty("default_server");
			DEFAULT_PORT = Integer.valueOf(prop.getProperty("default_port"));
			PROXY_PORT = Integer.valueOf(prop.getProperty("proxy_port"));
		} catch (Exception e) {
			logger.fatal("Could not read properties file. Setting 9999 as proxy port, localhost as POP3 server and 110 as POP3 port...");
			DEFAULT_SERVER = "localhost";
			DEFAULT_PORT = 110;
			PROXY_PORT = 9999;
		}

		Thread configuration = new Thread(new ConfigurationServer());
		configuration.setName("ConfigurationServer");
		configuration.start();

		Thread statistics = new Thread(new StatisticsServer());
		statistics.setName("StatisticsServer");
		statistics.start();

		try {
			ServerSocket server = new ServerSocket(PROXY_PORT);
			logger.info("Proxy POP3 listening in port " + PROXY_PORT);
			while (true) {
				Socket socket = server.accept();
				logger.info("Connected to host " + socket.getInetAddress()
						+ ":" + socket.getPort());
				Thread thread = new Thread(new POP3ConnectionHandler(socket));
				thread.setName("POP3Handler" + socket.getInetAddress() + ":"
						+ socket.getPort());
				thread.start();
			}
		} catch (Exception e) {
			logger.fatal("Error connecting POP3 proxy");
		}
	}

}
