package proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.Message;
import filter.AccessRequestFilter;
import filter.ExternalProgramFilter;
import filter.Filter;
import filter.ImageTransformerFilter;
import filter.MessageTransformerFilter;
import filter.Request;
import filter.RequestFilter;
import filter.SendRequestFilter;
import filter.StatisticsFilter;

public class POP3ConnectionHandler extends ConnectionHandler {

	private POP3Client POP3client;
	public static String DEFAULT_SERVER = "pop.mail.yahoo.com.ar";

	private RequestFilter filterChain;

	public POP3ConnectionHandler(Socket socket) {
		super(socket);
		this.POP3client = new POP3Client();
	}

	private void addRequestFilter(RequestFilter filter) {
		if (filterChain != null) {
			RequestFilter tmp = filterChain;
			filter.setNext(tmp);
		}
		filterChain = filter;
	}

	public void run() {

		// Prepare filter chain
		addRequestFilter(new SendRequestFilter());
		addRequestFilter(new StatisticsFilter());
		//addRequestFilter(new EraseRequestFilter());

		// Este filtro va al final, asi se ejecuta primero
		addRequestFilter(new AccessRequestFilter(this.socket));

		try {
			String request, response;

			do {
				request = reader.readLine();
				if (!request.toUpperCase().contains("PASS"))
					request = request.toUpperCase();
				
				if (request != null && !request.isEmpty()) {
					response = filterChain.doFilter(new Request(null, request),
							writer, POP3client);

					writer.println(response);
					if (request.contains("RETR") && response.contains("+OK")) {
						Message message = POP3client.getMessage();
						processMessage(message);
						writer.println(message.getBody());
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

	private void processMessage(Message message) {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new ExternalProgramFilter("./printBody"));
		filters.add(new MessageTransformerFilter());
		filters.add(new ImageTransformerFilter());
		for (Filter f : filters)
			f.apply(message);
	}

}