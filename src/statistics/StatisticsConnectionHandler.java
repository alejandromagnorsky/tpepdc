package statistics;

import java.io.IOException;
import java.net.Socket;

import proxy.ConnectionHandler;

import model.User;

public class StatisticsConnectionHandler extends ConnectionHandler {

	public StatisticsConnectionHandler(Socket socket) {
		super(socket);

		User user1 = new User("USER1");
		User user2 = new User("USER2");

		Statistics.addAccess(user1);
		Statistics.addAccess(user1);
		Statistics.addAccess(user2);
		Statistics.addAccess(user1);
		Statistics.addAccess(user1);
		Statistics.addAccess(user2);
		Statistics.addAccess(user1);
		Statistics.addAccess(user2);
		Statistics.addAccess(user2);
		Statistics.addBytesTransfered(user1, (long) 1000);
		Statistics.addBytesTransfered(user1, (long) 1000);
		Statistics.addBytesTransfered(user2, (long) 5000);
		Statistics.addRed(user2);
		Statistics.addRed(user2);
		Statistics.addRed(user2);
		Statistics.addRed(user1);
		Statistics.addDeleted(user2);
	}

	public void run() {
		try {
			String request, response;
			User user = null;
			do {
				request = reader.readLine().toUpperCase();

				if (request.startsWith("USER ")) {
					// TODO Obtener al user del XML
					user = new User(request
							.substring(request.lastIndexOf(' ') + 1));
					response = "OK. Now the statistics are for the user "
							+ user.getName();
				} else if (request.equals("PUBLIC")) {
					user = null;
					response = "OK. Now the statistics are public";
				} else if (request.equals("AQ"))
					response = "OK. Access quant: "
							+ ((user == null) ? Statistics.getAccessQuant()
									: Statistics.getAccessQuant(user));
				else if (request.equals("BT"))
					response = "OK. Bytes transfered: "
							+ ((user == null) ? Statistics.getBytesTransfered()
									: Statistics.getBytesTransfered(user));
				else if (request.equals("RQ"))
					response = "OK. Red quant: "
							+ ((user == null) ? Statistics.getRedQuant()
									: Statistics.getRedQuant(user));
				else if (request.equals("DQ"))
					response = "OK. Deleted quant: "
							+ ((user == null) ? Statistics.getDeletedQuant()
									: Statistics.getDeletedQuant(user));
				else if (request.equals("AH")) {
					StringBuilder ans = new StringBuilder();
					ans.append("OK. Access histogram:\n");
					for (Statistics.Access access : (user == null) ? Statistics
							.getAccessHistogram() : Statistics
							.getAccessHistogram(user))
						ans.append("Date: " + access.getDate().getDayOfMonth()
								+ "/" + access.getDate().getMonthOfYear() + "/"
								+ access.getDate().getYear()
								+ " - Access quant: " + access.getQuant()
								+ "\n");
					response = ans.toString();
				} else if (request.equals("EXIT"))
					response = "OK.";
				else
					response = "ERROR. Invalid command";

				writer.println(response);
			} while (isConnected() && !request.equals("EXIT"));

			disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
