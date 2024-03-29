package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import model.ExternalProgram;
import model.Message;
import model.MessageParser;
import model.User;

public class POP3Client extends Client {

	public void connectAndReadResponseLine(String host, int port) throws IOException {
		connect(host, port);
		// To automatically read the welcome message
		readResponseLine();
	}

	protected String readResponseLine() throws IOException {
		String response = reader.readLine();
		POP3Proxy.logger.info("[in]: " + response);
		return response;
	}

	public String send(String command) throws IOException {
		POP3Proxy.logger.info("[out]: " + command);
		writer.println(command);
		return readResponseLine();
	}

	public int getQuantOfNewMessages() throws IOException {
		String response = send("STAT");
		return Integer.parseInt(response.split(" ")[1]);
	}

	public Message getMessage(PrintWriter writer, User user) throws IOException {
		MessageParser messageParser;
		Process process = null;
		BufferedReader input = reader;
		if(user != null && user.getSettings() != null 
			&& user.getSettings().getExternal() != null && !user.getSettings().getExternal().equals("none")){ 
			ExternalProgram externalProgram = new ExternalProgram(user.getSettings().getExternal(), reader);
			process = externalProgram.execute();
			if(process != null)
				input = externalProgram.getReader(process);
		}
		

		messageParser = new MessageParser(input, writer, user);
		Message message = null;
		try {
			message = messageParser.parseMessage();
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error parsing message");
			writer.println(".");
		}

		// if the input is the reader from the external program
		if (!input.equals(reader)) {
			input.close();
			try {
				int code = process.waitFor();
				if (code != 0)
					POP3Proxy.logger.fatal("Error executing the external program " + user.getSettings().getExternal());					
				process.destroy();
			} catch (Exception e) {
				POP3Proxy.logger.fatal("Error executing the external program " + user.getSettings().getExternal());
			}
		}

		return message;
	}

	public Message getMessage(PrintWriter writer) throws IOException {
		MessageParser messageParser = new MessageParser(reader, writer);
		return messageParser.parseMessage();
	}

	public String getListOfMessage() throws IOException {
		String response;
		StringBuilder listBuilder = new StringBuilder();

		while (!(response = readResponseLine()).equals("."))
			listBuilder.append(response + "\n");
		listBuilder.append(".");
		return listBuilder.toString();
	}
}
