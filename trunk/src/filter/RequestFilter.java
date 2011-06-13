package filter;

import java.io.PrintWriter;

import proxy.POP3Client;

public abstract class RequestFilter {

	private RequestFilter next = null;
	
	public void setNext(RequestFilter next) {
		this.next = next;
	}

	public String doFilter(Request request, PrintWriter responseWriter,
			POP3Client client) {
		return apply(request, responseWriter, client, next);
	}

	protected abstract String apply(Request request,
			PrintWriter responseWriter, POP3Client client, RequestFilter chain);
}
