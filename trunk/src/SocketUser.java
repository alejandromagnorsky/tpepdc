import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketUser {

	protected Socket socket;

	protected boolean debug = false;
	protected Logger logger = Logger.getLogger("logger");

	protected BufferedReader reader;
	protected PrintWriter writer;
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	public void disconnect() throws IOException {
		if (!isConnected())
			throw new IllegalStateException("Not connected to a host");
		socket.close();
		reader = null;
		writer = null;
		if (debug)
			logger.info("Disconnected from the host");
	}
}
