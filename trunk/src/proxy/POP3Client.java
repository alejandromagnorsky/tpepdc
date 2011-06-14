package proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Content;
import model.ImageContent;
import model.Message;
import model.MessageEnsambler;
import model.MessageParser;
import model.OtherContent;
import model.TextContent;
import filter.ImageTransformerFilter;
import filter.MessageTransformerFilter;

public class POP3Client extends Client {

	private static final int PORT = 995; // TODO

	public void connect(String host) throws IOException {
		connect(host, PORT);
		// To automatically read the welcome message
		readResponseLine();
	}

	protected String readResponseLine() throws IOException {
		String response = reader.readLine();
		POP3Proxy.logger.info("[in]: " + response);
		return response;
	}

	public String send(String command) throws IOException {
		POP3Proxy.logger.info("[out]: " + command);
		writer.println(command);
		return readResponseLine();
	}

	public int getQuantOfNewMessages() throws IOException {
		String response = send("STAT");
		return Integer.parseInt(response.split(" ")[1]);
	}

	public Message getMessage() throws IOException {
		MessageParser messageParser = new MessageParser(reader);
		return messageParser.parseMessage();
	}

	public static void main(String args[]) {
		try {
			POP3Client client = new POP3Client();
			client.reader = new BufferedReader(new FileReader("email2.txt"));
			Message message = client.getMessage();
			System.out.println(message.getContents().size());
			for (Content content : message.getContents()) {
				if (content.getType().equals(Content.Type.TEXT)) {
					System.out.println("--------------------------");
					System.out.println("TEXT");
					MessageTransformerFilter mstr = new MessageTransformerFilter();
					mstr.apply(message);
					System.out.println(((TextContent) content).getText());
					System.out.println("--------------------------");
				} else if (content.getType().equals(Content.Type.IMAGE)) {
					System.out.println("--------------------------");
					System.out.println("IMAGE");
					ImageTransformerFilter rotate = new ImageTransformerFilter();
					rotate.apply(message);
					ImageIO.write(((ImageContent) content).getImage(), "png",	new File("email.png"));				
				} else {
					System.out.println("--------------------------");
					System.out.println("OTHERRRR");
					System.out.println(((OtherContent) content)
							.getContentTypeHeader());
				}
			}
			
			System.out.println("**********************************");
			MessageEnsambler me = new MessageEnsambler();
			System.out.println(me.getMessage(message));
			System.out.println("**********************************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
