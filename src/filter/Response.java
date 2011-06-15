package filter;

import model.User;

public class Response {

	User user = null;
	private String responseString = null;

	public Response(User user, String requestString) {
		this.user = user;
		this.setResponseString(requestString);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setResponseString(String requestString) {
		this.responseString = requestString;
	}

	public String getResponseString() {
		return responseString;
	}

}
