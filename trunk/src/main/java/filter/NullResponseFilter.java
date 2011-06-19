package filter;

import model.Message;
import model.User;

public class NullResponseFilter extends ResponseFilter {

	@Override
	protected void apply(Message message, User user, ResponseFilter chain) {
	}

}
