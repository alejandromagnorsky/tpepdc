import java.io.PrintWriter;

public abstract class RequestFilter {

	private RequestFilter next = null;

	public void setNext(RequestFilter next) {
		this.next = next;
	}

	public String doFilter(String request, PrintWriter responseWriter,
			POP3Client client) {
		return apply(request, responseWriter, client, next);
	}

	protected abstract String apply(String request, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain);
}
