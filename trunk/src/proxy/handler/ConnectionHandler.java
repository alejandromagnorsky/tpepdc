package proxy.handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import proxy.POP3Proxy;
import proxy.SocketUser;

public abstract class ConnectionHandler extends SocketUser implements Runnable {

	public ConnectionHandler(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			// Enable auto-flush after println
			this.writer = new PrintWriter(new OutputStreamWriter(socket
					.getOutputStream()), true);
		} catch (IOException e) {
			POP3Proxy.logger.fatal("Error creating the reader and the writer from the socket");
		}
	}

}
