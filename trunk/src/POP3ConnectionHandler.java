import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class POP3ConnectionHandler extends SocketUser implements Runnable {

	private POP3Client POP3client;

	public POP3ConnectionHandler(Socket socket) {
		this.POP3client = new POP3Client();
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			// Enable auto-flush after println
			this.writer = new PrintWriter(new OutputStreamWriter(socket
					.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			POP3client.connect("pop.mail.yahoo.com.ar");
			POP3client.setDebug(true);
			setDebug(true);
			String request, response;
			do {
				request = reader.readLine();
				response = POP3client.send(request);
				writer.println(response);
				if (request.contains("RETR") && response.contains("+OK")) {
					Message message = POP3client.getMessage(Integer
							.valueOf(request
									.substring(request.indexOf(' ') + 1)));
					writer.println(message.getBody());
				}

			} while (isConnected()
					&& !(request.contains("QUIT") && response.contains("+OK")));
			POP3client.disconnect();
			disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
