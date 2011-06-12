import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Minutes;

import model.User;
import dao.XMLSettingsDAO;

public class POP3ConnectionHandler extends ConnectionHandler {

	private POP3Client POP3client;
	private static String DEFAULT_SERVER = "pop.mail.yahoo.com.ar";

	// TODO cargar el usuario, la blacklist y los loginsPerDay
	private User user;
	private List<String> ipBlackList;

	private XMLSettingsDAO loader = new XMLSettingsDAO("settings.xml",
			"src/settings.xsd");

	public POP3ConnectionHandler(Socket socket) {
		super(socket);
		// TODO el load deberia estar aca?
		try {
			loader.load();
		} catch (Exception e) {
			// TODO
		}
		this.POP3client = new POP3Client();
		this.ipBlackList = loader.getBlacklistIP();
	}

	public void run() {
		try {
			String request, response;

			do {
				request = reader.readLine();
				boolean accesDenied = false;

				if (!request.toUpperCase().contains("PASS"))
					request = request.toUpperCase();

				if (request.contains("USER ") && !POP3client.isConnected()) {
					String ip = socket.getInetAddress().toString();
					String server = DEFAULT_SERVER;

					loadUser(request.substring(request.lastIndexOf(' ') + 1));

					if (this.user != null && user.getSettings() != null) {
						String userServer = user.getSettings().getServer();
						if (userServer != null && !userServer.equals("")) {
							server = userServer;
						}
						accesDenied = accessIsDenied(ip);
						if (accesDenied) {
							this.user = null;
							if (POP3client.isConnected()) {
								request = "QUIT";
								POP3client.send(request);
							}
						}
					}

					if (!accesDenied) {
						POP3client.connect(server);
					}

				}

				if (POP3client.isConnected())
					response = POP3client.send(request);
				else if (!accesDenied)
					response = "-ERR. Must use USER command first.";
				else
					response = "-ERR. Access denied.";

				writer.println(response);
				if (request.contains("RETR") && response.contains("+OK")) {
					Message message = POP3client.getMessage();
					processMessage(message);
					writer.println(message.getBody());
				}

			} while (isConnected() && !request.contains("QUIT"));

			if (POP3client.isConnected())
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

	private boolean accessIsDenied(String ip) {

		if (AccessControl.ipIsDenied(ipBlackList, ip)) {
			writer.println("-ERR. Login failed");
			return true;
		}

		if (AccessControl.exceedsMaxLogins(user)) {
			writer.println("-ERR. You have exceeded the ammount of "
					+ "logins for today. Please try again tomorrow");
			return true;
		}

		if (AccessControl.hourIsOutOfRange(user)) {
			writer.println("-ERR. You are not allowed to login now. Try again"
					+ "between " + minutesToString(user.getSettings().getSchedule().getFrom())
					+ " and " + minutesToString(user.getSettings().getSchedule().getTo()) + "hs");
			return true;
		}

		return false;
	}

	private void loadUser(String user) {
		List<User> users = loader.getUserList();
		for (User u : users) {
			if (user.toUpperCase().equals(u.getName().toUpperCase())) {
				this.user = u;
				return;
			}
		}
	}
	
	public String minutesToString(int mins) {
		String str = "";
		str += (mins - mins % 60) / 60;
		str+= ":";
		str += mins % 60;
		return str;
	}
	
}
