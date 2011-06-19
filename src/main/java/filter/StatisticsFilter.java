package filter;

import java.io.PrintWriter;

import proxy.POP3Client;
import statistics.Statistics;

public class StatisticsFilter extends RequestFilter {

	@Override
	protected Response apply(Request request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {
		String response = chain.doFilter(request, responseWriter, client)
				.getResponseString();

		if ((response != null && response.contains("+OK"))
				&& (request != null && request.getUser() != null)) {
			if (request.getRequestString().toUpperCase().contains("RETR")) {
				Statistics.addRed(request.getUser());
			} else if (request.getRequestString().toUpperCase().contains("PASS"))
				Statistics.addAccess(request.getUser());
			else if (request.getRequestString().toUpperCase().contains("DELE"))
				Statistics.addDeleted(request.getUser());
			else if (request.getRequestString().toUpperCase().contains("LIST"))
				Statistics.addListed(request.getUser());
		}
		return new Response(request.getUser(), response);
	}

}