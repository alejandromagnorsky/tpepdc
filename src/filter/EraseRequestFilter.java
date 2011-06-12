package filter;

import java.io.PrintWriter;

import proxy.POP3Client;

public class EraseRequestFilter extends RequestFilter {

	@Override
	protected String apply(String request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {

		if (request.contains("DEL ") && client.isConnected()) {

		}

		return "";
	}

}
