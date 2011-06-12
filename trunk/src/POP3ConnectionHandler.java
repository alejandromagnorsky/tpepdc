import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import settings.XMLSettingsLoader;

import model.LoginsPerDay;
import model.User;

public class POP3ConnectionHandler extends ConnectionHandler {

	private POP3Client POP3client;
	private static String DEFAULT_SERVER = "pop.mail.yahoo.com.ar";

	// TODO cargar el usuario, la blacklist y los loginsPerDay
	private User user;
	private List<String> ipBlackList;
	private LoginsPerDay loginsPerDay;
	
	//TODO cambiar esto cuando el loader sea un singleton
	private XMLSettingsLoader loader = new XMLSettingsLoader("settings.xml");
	 
	public POP3ConnectionHandler(Socket socket) {
		super(socket);
		//TODO el load deberia estar aca?
		try {
			loader.load();
		} catch (Exception e) {
			//TODO
		}
		this.POP3client = new POP3Client();
		this.ipBlackList = loader.getBlacklistIP();
		this.loginsPerDay = new LoginsPerDay();
	}

	public void run() {
		try {
			
			String request, response;

			do {
				request = reader.readLine();
				if (!request.toUpperCase().contains("PASS"))
					request = request.toUpperCase();
				if(request.startsWith("USER ")) {
					loadUser(request.substring(request.lastIndexOf(' ') + 1));
					
					String userServer = user.getSettings().getServer();
					String server;
					String ip = socket.getInetAddress().toString();
					if (userServer == null || userServer.equals("")) {
						server = DEFAULT_SERVER;
					} else {
						server = userServer;
					}
					
					//TODO borrar la siguiente linea cuando arregle el resto
					server = DEFAULT_SERVER;
					
					POP3client.connect(server);
					
					if(accessIsDenied(ip)) {
						request = "QUIT";
					}
				}
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
	
	private boolean accessIsDenied(String ip) {
		if(AccessControl.exceedsMaxLogins(user, loginsPerDay)) {
			writer.println("ERROR. You have exceeded the ammount of " +
					"logins for today. Please try again tomorrow");
			return true;
		}
		
		if(AccessControl.hourIsOutOfRange(user)) {
			writer.println("ERROR. You are not allowed to login now. Try again" +
					"between " + user.getSettings().getSchedule().getFrom() + 
					"and " + user.getSettings().getSchedule().getTo() + "hs");
			return true;
		}
		
		if(AccessControl.ipIsDenied(ipBlackList, ip)) {
			writer.println("ERROR. Login failed");
			return true;
		}
		
		return false;
	}
	
	private void loadUser(String user) {
		List<User> users = loader.getUserList();
		for(User u : users) {
			if(user.toUpperCase().equals(u.getName().toUpperCase())) {
				this.user = u;
			}
		}
	}
}
