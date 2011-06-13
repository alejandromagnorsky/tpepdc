package filter;

import proxy.Content;
import proxy.TextContent;
import model.Message;

public class MessageTransformerFilter implements Filter {

	public void apply(Message message) {

		for (Content c : message.getContents()) {
			if (c.getType().equals(Content.Type.TEXT)
					&& c.getContentTypeHeader().contains("text/plain")) {
				String leet = l33t(((TextContent) c).getText(), message);
				((TextContent) c).setText(leet);
			}
		}
	}

	public String l33t(String message, Message completeMessage) {
		char[] msg = message.toCharArray();
		for (int i = 0; i < message.length(); i++) {
			switch (msg[i]) {
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
			case '[':
				i = checkImageContent(i, msg, completeMessage);
				break;
			}
		}
		return String.valueOf(msg);
	}

	private int checkImageContent(int index, char[] msg, Message completeMessage) {
		String line = "", imageName = "";
		int i = index;
		for (; i < msg.length && i < index + 8; i++) {
			line += msg[i];
		}

		if (!line.equals("[image: ")) {
			return index;
		}

		for (; i < msg.length && msg[i] != ']'; i++) {
			imageName += msg[i];
		}

		for (Content c : completeMessage.getContents()) {
			if (c.getType().equals(Content.Type.IMAGE)
					&& c.getContentTypeHeader().contains(imageName)
					&& i < msg.length && msg[i] == ']') {
				return i;
			}
		}

		return index;
	}
}
