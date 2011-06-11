import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.User;

public class POP3ConnectionHandler extends ConnectionHandler {

	private POP3Client POP3client;
	private static String DEFAULT_SERVER = "pop.mail.yahoo.com.ar";

	// TODO cargar el usuario y la blacklist
	private User user;
//	private IPBlacklist ipBlackList;

	public POP3ConnectionHandler(Socket socket) {
		super(socket);
		this.POP3client = new POP3Client();

	}

	public void run() {
		try {
			String userServer = "SERVER"; // TODO user.getServer();
			String server;
			String ip = socket.getInetAddress().toString();
			if (userServer == null || userServer.equals("")) {
				server = DEFAULT_SERVER;
			} else {
				server = userServer;
			}

//			// TODO hacer algo con la respuesta
//			AccessControl.exceedsMaxLogins(user);
//			AccessControl.hourIsOutOfRange(user);
//			AccessControl.ipIsDenied(ipBlackList, ip);

			POP3client.connect(server);
			String request, response;

			do {
				request = reader.readLine();
				if (!request.toUpperCase().contains("PASS"))
					request = request.toUpperCase();
				response = POP3client.send(request);
				writer.println(response);
				if (request.contains("RETR") && response.contains("+OK")) {
					Message message = POP3client.getMessage();
					processMessage(message);
					writer.println(message.getBody());
				}

			} while (isConnected() && !request.contains("QUIT"));

			POP3client.disconnect();
			disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processMessage(Message message) {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new ExternalProgram("./printBody"));
		filters.add(new MessageTransformerFilter());
		for (Filter f : filters)
			f.apply(message);
	}
}
