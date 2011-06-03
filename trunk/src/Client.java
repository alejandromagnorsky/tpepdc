import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public abstract class Client extends SocketUser{

	private static final SSLSocketFactory SocketFactory = (SSLSocketFactory) SSLSocketFactory
			.getDefault();
	
	public void connect(String host, int port) throws IOException {
        socket = (SSLSocket)SocketFactory.createSocket();
        socket.connect(new InetSocketAddress(host, port));
        reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
        // Enable auto-flush after println
        writer = new PrintWriter(new OutputStreamWriter(socket
				.getOutputStream()), true);
		if (debug)
			System.out.println("Connected to the host");
	}

	
}
