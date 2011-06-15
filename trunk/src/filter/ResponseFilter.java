package filter;

import model.Message;
import model.User;

public abstract class ResponseFilter {

	private ResponseFilter next = null;

	public void setNext(ResponseFilter next) {
		this.next = next;
	}

	public void doFilter(Message message, User user) {
		apply(message, user, next);
	}

	protected abstract void apply(Message message, User user,
			ResponseFilter chain);

}
