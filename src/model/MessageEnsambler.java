package model;

import java.util.List;
import java.util.Map;

import org.apache.log4j.chainsaw.Main;

import proxy.Content;
import proxy.OtherContent;
import proxy.TextContent;

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
		Content[] sortedContent = message.getContents().toArray( new Content[message.getContents().size()]);
		for (String s : message.getBodySkeleton().split("\n")) {
			if (s.startsWith(">>")) {
				String number = s.substring(s.indexOf(">>") + 2, s
						.indexOf("<<"));
				int n = Integer.valueOf(number);
				Content content = sortedContent[n - 1];
				if (content.getType().equals(Content.Type.TEXT)) {
					msg.append(((TextContent) content).getText());
				} else if (content.getType().equals(Content.Type.IMAGE)) {
					// TODO appendear la imagen
//					 BufferedImage img = ((ImageContent)c).getImage();
//										
//										
//					 ImageReader imageReader = img.toString();
//					 BufferedImage bufferedImage = imageReader.read(0);
//					 String formatName = imageReader.getFormatName();
//					 ByteArrayOutputStream byteaOutput = new
//					 ByteArrayOutputStream();
//					 String base64 = new String(byteaOutput.toByteArray());
//										
//					 msg.append();
				} else {
					msg.append(((OtherContent) content).getContent());
				}
			} else {
				msg.append(s + "\n");
			}
		}
	}
}
