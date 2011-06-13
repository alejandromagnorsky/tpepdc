package filter;
import proxy.Content;
import proxy.TextContent;
import model.Message;


public class MessageTransformerFilter implements Filter {

	public void apply(Message message) {
		
		for (Content c : message.getContents()) {
			if (c.getType().equals(Content.Type.TEXT) && c.getContentTypeHeader().contains("text/plain")) {
				String leet = l33t(((TextContent)c).getText());
				((TextContent)c).setText(leet);
			}
		}
	}
	
	public String l33t(String message) {
		char[] msg = message.toCharArray();
		for(int i = 0; i < message.length(); i++) {
			switch(msg[i]) {
			case 'a': 
				msg[i] = '4';
				break;
			case 'e':
				msg[i] = '3';
				break;
			case 'i':
				msg[i] = '1';
				break;
			case 'o':
				msg[i] = '0';
				break;
			}
		}
		return String.valueOf(msg);
	}
}
