package model;


public class TextTransformer {

	public String transform(String line, Message message) {
//		if (user != null && user.getSettings() != null
//				&& user.getSettings().isLeet() != null && user.getSettings().isLeet())
//			for (Content c : message.getContents()) {
//				if (c.getType().equals(Content.Type.TEXT)
//						&& c.getContentTypeHeader().contains("text/plain")) {
					return l33t(line, message);
//					((TextContent) c).setText(leet);
//				}
//			}
//		chain.doFilter(message, user);
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
		
		if(i < msg.length && msg[i] == ']') {
			return i;
		}

		//TODO todavia no tengo la imagen, ver como chequear esto
//		for (Content c : completeMessage.getContents()) {
//			if (c.getType().equals(Content.Type.IMAGE)
//					&& c.getContentTypeHeader().contains(imageName)
//					&& i < msg.length && msg[i] == ']') {
//				return i;
//			}
//		}

		return index;
	}
}
