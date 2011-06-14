package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import proxy.handler.ConfigurationServiceHandler;

public class ConfigurationServer implements Runnable {

        private static int CONFIGURATION_PORT = 9007;
        private static Logger logger = Logger.getLogger("logger");

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