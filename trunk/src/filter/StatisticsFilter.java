package filter;

import java.io.PrintWriter;

import proxy.POP3Client;
import proxy.POP3Proxy;
import statistics.Statistics;

public class StatisticsFilter extends RequestFilter {

	@Override
	protected String apply(Request request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {
		String response = chain.doFilter(request, responseWriter, client);
		
		if (response.contains("+OK") && request.getUser() != null) {
			if (request.getRequestString().contains("RETR")) {
				Statistics.addRed(request.getUser());
				int separator = response.indexOf(' ')+1;
				try{
				long bytes = Long.valueOf(response.substring(separator, response.indexOf(' ', separator)));
				Statistics.addBytesTransfered(request.getUser(), bytes);
				} catch(NumberFormatException e){
					POP3Proxy.logger.info("Server POP3 doesn't write quantity of bytes in the response");
				}
			}
			else if (request.getRequestString().toUpperCase().contains("PASS"))
				Statistics.addAccess(request.getUser());
			else if (request.getRequestString().contains("DELE"))
				Statistics.addDeleted(request.getUser());
		}
		return response;
	}

}
