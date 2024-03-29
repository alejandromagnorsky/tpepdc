package proxy.handler;

import java.io.IOException;
import java.net.Socket;

import model.User;
import proxy.POP3Proxy;
import statistics.Statistics;
import dao.XMLSettingsDAO;

public class StatisticsConnectionHandler extends ServiceConnectionHandler {

	private XMLSettingsDAO loader = XMLSettingsDAO.getInstance();

	public StatisticsConnectionHandler(Socket socket) {
		super(socket);
	}

	public void run() {
		try {
			String request, response;
			User user = null;

			if (validateLogin()) {
				writer.println("OK. Welcome to the statistics service");
				do {
					request = reader.readLine();
					if (request != null) {
						if (request.toUpperCase().startsWith("USER ")) {
							String username = request.substring(request
									.lastIndexOf(' ') + 1);
							user = loader.getUser(username);
							if (user == null)
								response = "ERROR. User " + username
										+ " doesn't exists";
							else
								response = "OK. Now the statistics are for the user "
										+ user.getName();
						} else {
							request = request.toUpperCase();
							if (request.equals("PUBLIC")) {
								user = null;
								response = "OK. Now the statistics are public";
							} else if (request.equals("AQ"))
								response = "OK. Access quant: "
										+ ((user == null) ? Statistics
												.getAccessQuant() : Statistics
												.getAccessQuant(user));
							else if (request.equals("BT"))
								response = "OK. Bytes transfered: "
										+ ((user == null) ? Statistics
												.getBytesTransfered()
												: Statistics
														.getBytesTransfered(user));
							else if (request.equals("RQ"))
								response = "OK. Red quant: "
										+ ((user == null) ? Statistics
												.getRedQuant() : Statistics
												.getRedQuant(user));
							else if (request.equals("DQ"))
								response = "OK. Deleted quant: "
										+ ((user == null) ? Statistics
												.getDeletedQuant() : Statistics
												.getDeletedQuant(user));
							else if (request.equals("LQ"))
								response = "OK. Listed quant: "
										+ ((user == null) ? Statistics
												.getListedQuant() : Statistics
												.getListedQuant(user));
							else if (request.equals("AH")) {
								StringBuilder ans = new StringBuilder();
								ans.append("OK. Access histogram:\n");
								for (Statistics.Access access : (user == null) ? Statistics
										.getAccessHistogram()
										: Statistics.getAccessHistogram(user))
									ans.append("Date: "
											+ access.getDate().getDayOfMonth()
											+ "/"
											+ access.getDate().getMonthOfYear()
											+ "/" + access.getDate().getYear()
											+ " - Access quant: "
											+ access.getQuant() + "\n");
								response = ans.toString();
							} else if (request.equals("EXIT"))
								response = "OK.";
							else
								response = "ERROR. Invalid command";
						}
						writer.println(response);
					}
				} while (isConnected() && request != null
						&& !request.equals("EXIT"));
			} else {
				writer.println("ERROR. User or password incorrect");
			}
			disconnect();
		} catch (IOException e) {
			POP3Proxy.logger.fatal("Error with connection. Disconnecting client...");
			try {
				disconnect();
			} catch (Exception e1) {
				POP3Proxy.logger.fatal("Error disconnecting");
			}
		}
	}
}