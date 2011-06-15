package proxy.handler;

import java.io.IOException;
import java.net.Socket;

import proxy.POP3Proxy;

import model.Administrator;

import dao.XMLAdminDAO;

public abstract class ServiceConnectionHandler extends ConnectionHandler {

	private XMLAdminDAO adminDAO = XMLAdminDAO.getInstance();

	public ServiceConnectionHandler(Socket socket) {
		super(socket);
		try {
			adminDAO.load();
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error loading Admin");
		}
	}

	protected boolean validateLogin() {
		String request;
		boolean login = false;
		try {
			do {
				request = reader.readLine();
				if (!request.toUpperCase().contains("LOGIN"))
					writer.println("ERROR. Login first");
				else
					login = true;
			} while (!login);
		} catch (IOException e) {
			return false;
		}

		String[] args = request.split(" ");
		if (args.length != 3)
			return false;
		String name = args[1];
		String password = args[2];
		Administrator admin = adminDAO.getAdministrator(name);
		if (admin == null)
			return false;
		return admin.verifyPassword(password);
	}

}
