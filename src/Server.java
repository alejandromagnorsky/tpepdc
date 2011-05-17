import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Server {

	private Socket socket;

	protected boolean debug = false;

	protected BufferedReader reader;
	protected BufferedWriter writer;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void connect(String host, int port) throws IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));
		reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket
				.getOutputStream()));
		if (debug)
			System.out.println("Connected to the host");
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
			System.out.println("Disconnected from the host");
	}
}
