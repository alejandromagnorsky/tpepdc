package proxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import proxy.handler.ConfigurationServiceHandler;

public class ConfigurationServer implements Runnable {

	private static int CONFIGURATION_PORT;
	private static Logger logger = Logger.getLogger("logger");

	public ConfigurationServer() {

		Properties prop = new Properties();
		// TODO Modificar cuando este bien el pom.xml
		// prop.load(POP3Proxy.class.getResourceAsStream("connection.properties"));
		try {
			prop.load(new FileInputStream("resources/proxy.properties"));
			CONFIGURATION_PORT = Integer.valueOf(prop
					.getProperty("config_port"));
		} catch (Exception e) {
			logger.fatal("Could not read properties file. Setting 9007 as configuration port server...");
			CONFIGURATION_PORT = 9007;
		}
	}

	public void run() {
		try {
			ServerSocket server = new ServerSocket(CONFIGURATION_PORT);
			logger.info("Configuration server listening in port "
					+ CONFIGURATION_PORT);
			while (true) {
				Socket socket = server.accept();
				logger.info("Connected to host " + socket.getInetAddress()
						+ ":" + socket.getPort());
				Thread thread = new Thread(new ConfigurationServiceHandler(
						socket));
				thread.setName("ConfigurationHandler "
						+ socket.getInetAddress() + ":" + socket.getPort());
				thread.start();
			}
		} catch (IOException e) {
			POP3Proxy.logger.fatal("Error connecting Configuration Server");
		}

	}
}