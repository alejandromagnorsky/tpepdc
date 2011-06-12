import java.io.IOException;
import java.io.PrintWriter;

// Final entry in filter chain
public class SendRequestFilter extends RequestFilter {

	@Override
	protected String apply(String request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {
		if (client.isConnected())
			try {
				return client.send(request);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		return "";
	}

}
