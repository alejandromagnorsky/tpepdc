package filter;

import model.User;

public class Request {

	User user = null;
	private String requestString = null;

	public Request(User user, String requestString) {
		this.user = user;
		this.setRequestString(requestString);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}

	public String getRequestString() {
		return requestString;
	}

}
