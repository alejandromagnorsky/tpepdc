package filter;

import java.io.PrintWriter;

import proxy.POP3Client;

public class EraseRequestFilter extends RequestFilter {

	@Override
	protected String apply(Request r, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {

		String request = r.getRequestString();

		if (request.contains("DEL ") && client.isConnected()) {

		}

		return "";
	}
}
