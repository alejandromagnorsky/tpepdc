package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class MessageEnsambler {

	public String getMessage(Message message) {
		StringBuilder msg = new StringBuilder();
		addHeaders(msg, message);
		rearmBody(msg, message);
		return msg.toString();
	}

	private void addHeaders(StringBuilder msg, Message message) {
		msg.append(message.getMainHeader() + "\n");
	}

	private void rearmBody(StringBuilder msg, Message message) {
		Content[] sortedContent = message.getContents().toArray(
				new Content[message.getContents().size()]);
		for (String s : message.getBody().split("\n")) {
			if (s.startsWith(">>")) {
				String number = s.substring(s.indexOf(">>") + 2, s
						.indexOf("<<"));
				int n = Integer.valueOf(number);
				Content content = sortedContent[n - 1];
				if (content.getType().equals(Content.Type.TEXT)) {
					msg.append(((TextContent) content).getText());
				} else if (content.getType().equals(Content.Type.IMAGE)) {
					BufferedImage img = ((ImageContent) content).getImage();
					String format = ((ImageContent) content).getContentTypeHeader();
					format = format.substring(format.indexOf("/") + 1, format.indexOf(";"));
					String imageString = imageToString(img, format);
					msg.append(imageString);
				} else {
					msg.append(((OtherContent) content).getContent());
				}
			} else {
				msg.append(s + "\n");
			}
		}
	}

	// Format: png,jpg etc
	private String imageToString(BufferedImage image, String format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format, baos);
			byte[] buf = baos.toByteArray();
			return byteArrayToString(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String byteArrayToString(byte[] buf) {
		char[] cbuf = new char[buf.length];

		for (int i = 0; i < buf.length; i++)
			cbuf[i] = (char) buf[i];
		return new String(cbuf, 0, cbuf.length);
	}

	private String decodeBase64(String base64String) {
		byte[] buf = Base64.decodeBase64(base64String);

		return byteArrayToString(buf);
	}
}
