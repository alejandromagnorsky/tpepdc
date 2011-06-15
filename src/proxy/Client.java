package proxy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Client extends SocketUser {

	public void connect(String host, int port) throws IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));
		reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		// Enable auto-flush after println
		writer = new PrintWriter(new OutputStreamWriter(socket
				.getOutputStream()), true);
		POP3Proxy.logger.info("Connected to the host " + socket.getInetAddress());
	}

}
