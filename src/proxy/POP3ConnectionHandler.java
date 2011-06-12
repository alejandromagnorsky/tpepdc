package proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import filter.AccessRequestFilter;
import filter.Filter;
import filter.ImageTransformerFilter;
import filter.MessageTransformerFilter;
import filter.RequestFilter;
import filter.SendRequestFilter;

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
		addRequestFilter(new AccessRequestFilter(this.socket));

		try {
			String request, response;

			do {
				request = reader.readLine();
				if (request == null)
					request = "";
				response = filterChain.doFilter(request, writer, POP3client);

				writer.println(response);
				if (request.contains("RETR") && response.contains("+OK")) {
					Message message = POP3client.getMessage();
					processMessage(message);
					writer.println(message.getBody());
				}

			} while (isConnected() && !request.contains("QUIT"));

			if (POP3client.isConnected())
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
		filters.add(new ImageTransformerFilter());
		for (Filter f : filters)
			f.apply(message);
	}

}
