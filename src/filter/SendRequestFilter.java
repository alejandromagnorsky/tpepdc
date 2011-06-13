package filter;

import java.io.IOException;
import java.io.PrintWriter;

import proxy.POP3Client;
import proxy.POP3Proxy;

// Final entry in filter chain
public class SendRequestFilter extends RequestFilter {

	@Override
	protected String apply(Request request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {
		if (client.isConnected())
			try {
				return client.send(request.getRequestString());
			} catch (IOException e) {
				POP3Proxy.logger.fatal("Error sending message to POP3 server");
			}

		return "";
	}

}
