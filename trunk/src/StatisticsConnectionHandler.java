import java.io.IOException;
import java.net.Socket;

import settings.User;

public class StatisticsConnectionHandler extends ConnectionHandler {

	public StatisticsConnectionHandler(Socket socket) {
		super(socket);
	}

	public void run() {
		try {
			String request, response;
			do {
				request = reader.readLine().toUpperCase();
				User user = new User();
				user.setName("user");
				Statistics.addAccess(user);
				if(request.equals("AQ"))
					response = "OK. Access quant = "+Statistics.getAccessQuant();
				else if(request.equals("EXIT"))
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
