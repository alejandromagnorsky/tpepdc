package filter;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import model.AccessControl;
import model.User;
import proxy.POP3Client;
import proxy.POP3Proxy;
import proxy.handler.POP3ConnectionHandler;
import dao.XMLSettingsDAO;

public class AccessRequestFilter extends RequestFilter {

	private XMLSettingsDAO loader = null;
	private List<String> ipBlackList = null;
	private User user = null;
	private Socket userSocket = null;

	public AccessRequestFilter(Socket userSocket) {
		this.loader = XMLSettingsDAO.getInstance();
		// TODO el load deberia estar aca?
		try {
			loader.load();
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error loading Settings");
		}
		this.ipBlackList = loader.getBlacklistIP();
		this.userSocket = userSocket;
	}

	@Override
	protected String apply(Request r, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {

		try {

			String request = r.getRequestString();
			// Check this constantly, so that an user can be blocked even when
			// it is connected
			String ip = userSocket.getInetAddress().toString().substring(1);
			boolean accessDenied = ipIsBlacklisted(responseWriter, ip);
			POP3Proxy.logger.info(request);
			if (request.toUpperCase().contains("USER ") && !client.isConnected()) {
				String server = POP3ConnectionHandler.DEFAULT_SERVER;

				user = loader.getUser(request.substring(request
						.lastIndexOf(' ') + 1));

				if (this.user != null && user.getSettings() != null) {
					String userServer = user.getSettings().getServer();
					if (userServer != null && !userServer.equals("")) {
						server = userServer;
					}
					accessDenied = accessDenied
							&& accessIsDenied(responseWriter, ip);

				}

				if (!accessDenied) {
					client.connect(server);

					// Inject user for first login
					r.setUser(user);
					return chain.doFilter(r, responseWriter, client);
				}
			}

			// Access can be denied even when USER command was not used. (banned
			// IP)
			if (accessDenied) {
				this.user = null;
				if (client.isConnected()) {
					request = "QUIT";
					client.send(request);
					responseWriter.println("-ERR. Access denied.");
					return "";
				}
			}

			if (!client.isConnected() && !accessDenied) {
				responseWriter.println("-ERR. Must use USER command first.");
				return "";
			}

			// Inject user
			r.setUser(user);

			// If nothing strange happens, continue
			return chain.doFilter(r, responseWriter, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean ipIsBlacklisted(PrintWriter writer, String ip) {
		if (AccessControl.ipIsDenied(ipBlackList, ip)) {
			writer.println("-ERR. Your IP has been banned.");
			return true;
		}
		return false;
	}

	private boolean accessIsDenied(PrintWriter writer, String ip) {

		if (user != null) {
			if (AccessControl.exceedsMaxLogins(user)) {
				writer.println("-ERR. You have exceeded the ammount of "
						+ "logins for today. Please try again tomorrow");
				return true;
			}

			if (AccessControl.hourIsOutOfRange(user)) {
				writer
						.println("-ERR. You are not allowed to login now. Try again"
								+ " between "
								+ minutesToString(user.getSettings()
										.getSchedule().getFrom())
								+ " and "
								+ minutesToString(user.getSettings()
										.getSchedule().getTo()) + "hs");
				return true;
			}
		}

		return false;
	}

	public String minutesToString(int mins) {
		String str = "";
		str += (mins - mins % 60) / 60;
		str += ":";
		str += mins % 60;
		return str;
	}
}
