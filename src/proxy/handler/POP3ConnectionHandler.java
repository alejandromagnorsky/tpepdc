package proxy.handler;

import java.io.IOException;
import java.net.Socket;

import model.Message;
import proxy.POP3Client;
import filter.AccessRequestFilter;
import filter.EraseRequestFilter;
import filter.ImageTransformerFilter;
import filter.MessageTransformerFilter;
import filter.NullResponseFilter;
import filter.Request;
import filter.RequestFilter;
import filter.Response;
import filter.ResponseFilter;
import filter.SendRequestFilter;
import filter.StatisticsFilter;

public class POP3ConnectionHandler extends ConnectionHandler {

	private POP3Client POP3client;
	public static String DEFAULT_SERVER = "pop3.alu.itba.edu.ar";

	private RequestFilter requestFilterChain;

	private ResponseFilter responseFilterChain;

	public POP3ConnectionHandler(Socket socket) {
		super(socket);
		this.POP3client = new POP3Client();
	}

	private void addRequestFilter(RequestFilter filter) {
		if (requestFilterChain != null) {
			RequestFilter tmp = requestFilterChain;
			filter.setNext(tmp);
		}
		requestFilterChain = filter;
	}

	private void addResponseFilter(ResponseFilter filter) {
		if (responseFilterChain != null) {
			ResponseFilter tmp = responseFilterChain;
			filter.setNext(tmp);
		}
		responseFilterChain = filter;
	}

	public void run() {

		// Prepare filter chain
		addRequestFilter(new SendRequestFilter());
		addRequestFilter(new StatisticsFilter());
		addRequestFilter(new EraseRequestFilter());

		// Este filtro va al final, asi se ejecuta primero
		addRequestFilter(new AccessRequestFilter(this.socket));

		addResponseFilter(new NullResponseFilter());
		addResponseFilter(new ImageTransformerFilter());
		addResponseFilter(new MessageTransformerFilter());

		try {
			String request, response;

			writer.println("+OK Welcome");
			do {
				request = reader.readLine();

				if (request != null && request.toUpperCase().contains("CAPA")) {
					writer.println("+OK Capability list follows");
					writer.println("USER");
					writer.println(".");
				}

				if (request != null && !request.toUpperCase().contains("PASS")
						&& !request.toUpperCase().contains("USER"))
					request = request.toUpperCase();

				if (request != null && !request.isEmpty()) {
					Response rsp = requestFilterChain.doFilter(new Request(
							null, request), writer, POP3client);

					response = rsp.getResponseString();

					writer.println(response);

					if (response.contains("+OK")) {
						if (request.contains("LIST")
								|| request.contains("UIDL"))
							writer.println(POP3client.getListOfMessage());
						else if (request.contains("RETR")) {
							Message message = POP3client.getMessage();
							responseFilterChain
									.doFilter(message, rsp.getUser());
							writer.println(message.reconstruct());
						}
					}
				}
			} while (isConnected()
					&& (request != null && !request.contains("QUIT")));

			if (POP3client.isConnected())
				POP3client.disconnect();
			disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
