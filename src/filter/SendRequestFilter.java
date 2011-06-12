package filter;

import java.io.IOException;
import java.io.PrintWriter;

import proxy.POP3Client;

// Final entry in filter chain
public class SendRequestFilter extends RequestFilter {

	@Override
	protected String apply(Request request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {
		if (client.isConnected())
			try {
				return client.send(request.getRequestString());
			} catch (IOException e) {
				e.printStackTrace();
			}

		return "";
	}

}
