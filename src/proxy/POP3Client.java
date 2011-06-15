package proxy;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Content;
import model.ImageContent;
import model.Message;
import model.MessageAssembler;
import model.MessageParser;
import model.OtherContent;
import model.TextContent;

import org.apache.commons.codec.binary.Base64;

import filter.ImageTransformerFilter;
import filter.MessageTransformerFilter;

public class POP3Client extends Client {

	private static final int PORT = 110;

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

	public String getListOfMessage() throws IOException {
		String response;
		StringBuilder listBuilder = new StringBuilder();

		while (!(response = readResponseLine()).equals("."))
			listBuilder.append(response + "\n");
		listBuilder.append(".");
		return listBuilder.toString();
	}

	public static void main(String args[]) {
		try {
			POP3Client client = new POP3Client();
			client.reader = new BufferedReader(new FileReader("email.txt"));
			Message message = client.getMessage();
			System.out.println(message.getContents().size());
			for (Content content : message.getContents()) {
				if (content.getType().equals(Content.Type.TEXT)) {
					System.out.println("--------------------------");
					System.out.println("TEXT");
					MessageTransformerFilter mstr = new MessageTransformerFilter();
//					mstr.apply(message);
					System.out.println(((TextContent) content).getText());
					System.out.println("--------------------------");
				} else if (content.getType().equals(Content.Type.IMAGE)) {
					System.out.println("--------------------------");
					System.out.println("IMAGE");
					ImageTransformerFilter rotate = new ImageTransformerFilter();
//					rotate.apply(message);
					ImageIO.write(((ImageContent) content).getImage(), "png",
							new File("email.png"));

					Image image = ((ImageContent) content).getImage();

					String imgStr = imageToString((BufferedImage) image, "png");
					String enc64 = encodeBase64(imgStr);

					System.out.println(enc64);

					Image parsed = base64ToImage(enc64);
					
					ImageIO.write(((ImageContent) content).getImage(), "png",
							new File("test_mail.png"));

				} else {
					System.out.println("--------------------------");
					System.out.println("OTHERRRR");
					System.out.println(((OtherContent) content)
							.getContentTypeHeader());
				}
			}

			System.out.println("**********************************");
			System.out.println(message.reconstruct());
			System.out.println("**********************************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String encodeBase64(String plain) {
		return Base64.encodeBase64String(plain.getBytes());
	}

	private static BufferedImage base64ToImage(String base64String) {
		try {
			byte[] imageInBytes = Base64.decodeBase64(base64String);
			return ImageIO.read(new ByteArrayInputStream(imageInBytes));
		} catch (Exception e) {
			return null;
		}
	}

	// Format: png,jpg etc
	private static String imageToString(BufferedImage image, String format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format, baos);
			byte[] buf = baos.toByteArray();
			return new String(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
