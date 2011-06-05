import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import settings.IPBlacklist;
import settings.User;

public class POP3ConnectionHandler extends SocketUser implements Runnable {

	private POP3Client POP3client;
	private static String DEFAULT_SERVER = "pop.mail.yahoo.com.ar";
	
	//TODO cargar el usuario y la blacklist
	private User user;
	private IPBlacklist ipBlackList;

	public POP3ConnectionHandler(Socket socket) {
		this.POP3client = new POP3Client();
		this.socket = socket;
		POP3client.setDebug(true);
		setDebug(true);
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
			String userServer = user.getServer();
			String server;
			if(userServer == null || userServer.equals("") ) {
				server = DEFAULT_SERVER;
			} else {
				server = userServer;
			}
			POP3client.connect(server);			
			String request, response;
			
			do {
				request = reader.readLine();
				response = POP3client.send(request);
				writer.println(response);
				if (request.contains("RETR") && response.contains("+OK")) {
					int msgNumber = Integer.valueOf(request.substring(request.indexOf(' ') + 1));
					Message message = POP3client.getMessage(msgNumber);
					processMessage(message);
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
	
	private void processMessage(Message message){
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new ExternalProgram("./printBody"));
		filters.add(new MessageTransformerFilter());
//TODO	filters.add(new AccessControl(user, ipBlackList));
		for(Filter f: filters)
			f.apply(message);
	}
}
