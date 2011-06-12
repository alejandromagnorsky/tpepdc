import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketUser {

	protected Socket socket;

	protected Logger logger = Logger.getLogger("logger");

	protected BufferedReader reader;
	protected PrintWriter writer;

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	public void disconnect() throws IOException {
		if (!isConnected())
			throw new IllegalStateException("Not connected to a host");
		socket.close();
		reader = null;
		writer = null;
		logger.info("Disconnected from the host");
	}
}
