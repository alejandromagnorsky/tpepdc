package proxy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketUser {

	protected Socket socket;

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
		POP3Proxy.logger.info("Disconnected from the host");
	}
}
