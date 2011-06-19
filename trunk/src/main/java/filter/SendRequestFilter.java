package filter;

import java.io.IOException;
import java.io.PrintWriter;

import proxy.POP3Client;
import proxy.POP3Proxy;

// Final entry in filter chain
public class SendRequestFilter extends RequestFilter {

	@Override
	protected Response apply(Request request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {
		if (client.isConnected())
			try {

				String resp = client.send(request.getRequestString());
				if (resp == null)
					throw new IllegalArgumentException(
							"Error sending message to POP3 server");

				return new Response(request.getUser(), resp);
			} catch (IOException e) {
				POP3Proxy.logger.fatal("Error sending message to POP3 server");
				throw new IllegalArgumentException(
						"Error sending message to POP3 server");
			}
		else
			throw new IllegalArgumentException("Not connected to POP3 server");
	}
}
