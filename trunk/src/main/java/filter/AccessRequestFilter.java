package filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import model.AccessControl;
import model.Range;
import model.User;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import proxy.POP3Client;
import proxy.POP3Proxy;
import dao.XMLLoginLogDAO;
import dao.XMLSettingsDAO;

public class AccessRequestFilter extends RequestFilter {

	private XMLSettingsDAO loader = null;
	private List<String> ipBlackList = null;
	private User user = null;
	private static Logger logger = Logger.getLogger("logger");
	private Socket userSocket = null;

	public AccessRequestFilter(Socket userSocket) {
		this.loader = XMLSettingsDAO.getInstance();
		this.ipBlackList = loader.getBlacklistIP();
		this.userSocket = userSocket;
	}

	@Override
	protected Response apply(Request r, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {

		try {
			String request = r.getRequestString();

			// Check this constantly, so that a user can be blocked even when
			// it is connected
			String ip = userSocket.getInetAddress().toString().substring(1);
			boolean accessDenied = ipIsBlacklisted(responseWriter, ip);

			if (accessDenied)
				throw new IllegalArgumentException("IP is banned");

			if (request.toUpperCase().contains("USER ")
					&& !client.isConnected()) {
				String server = POP3Proxy.DEFAULT_SERVER;

				user = loader.getUser(request.substring(request
						.lastIndexOf(' ') + 1));

				if (this.user != null && user.getSettings() != null) {
					String userServer = user.getSettings().getServer();
					if (userServer != null && !userServer.equals("")) {
						server = userServer;
					}
					accessDenied = accessIsDenied(responseWriter, ip)
							|| accessDenied;
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
					return new Response(user, "");
				}
			}

			if (!client.isConnected() && !accessDenied) {
				responseWriter.println("-ERR. Must use USER command first.");
				return new Response(user, "");
			}

			// If user is logged, update settings
			if (user != null)
				user = loader.getUser(user.getName());

			// Inject user
			r.setUser(user);

			// If nothing strange happens, continue
			Response resp = chain.doFilter(r, responseWriter, client);

			String response = resp.getResponseString();
			if ((response != null && response.contains("+OK"))
					&& (request != null
							&& request.toUpperCase().contains("PASS") && user != null)) {

				XMLLoginLogDAO loginDAO = XMLLoginLogDAO.getInstance();
				int qty = loginDAO.getUserLogins(user, new LocalDate());
				loginDAO.saveLogin(user, new LocalDate(), qty + 1);
				loginDAO.commit();
			}
			return resp;

		} catch (IOException e) {
			logger.fatal("Error connecting with the user.");
			throw new IllegalArgumentException("Server disconnected.");
		}
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
				writer.println("-ERR. You are not allowed to login now. Try again between the following ranges:");

				if (user.getSettings() != null)
					for (Range<Integer> range : user.getSettings()
							.getScheduleList()) {
						String rsp = "";
						if (range.getFrom() != null)
							rsp += "from: " + range.getFrom();
						if (range.getTo() != null)
							rsp += " to: " + range.getTo();

						writer.println(rsp);
					}
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
