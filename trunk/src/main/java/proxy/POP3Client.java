package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import model.ExternalProgram;
import model.Message;
import model.MessageParser;
import model.User;

public class POP3Client extends Client {

	public void connectAndReadResponseLine(String host, int port) throws IOException {
		connect(host, port);
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

	public Message getMessage(PrintWriter writer, User user) throws IOException {
		MessageParser messageParser;
		Process process = null;
		BufferedReader input = reader;
		if(user != null && user.getSettings() != null 
			&& user.getSettings().getExternal() != null && !user.getSettings().getExternal().equals("none")){ 
			ExternalProgram externalProgram = new ExternalProgram(user.getSettings().getExternal(), reader);
			process = externalProgram.execute();
			if(process != null)
				input = externalProgram.getReader(process);
		}
		

		messageParser = new MessageParser(input, writer, user);
		Message message = null;
		try {
			message = messageParser.parseMessage();
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error parsing message");
			writer.println(".");
		}

		// if the input is the reader from the external program
		if (!input.equals(reader)) {
			input.close();
			try {
				int code = process.waitFor();
				if (code != 0)
					POP3Proxy.logger.fatal("Error executing the external program " + user.getSettings().getExternal());					
				process.destroy();
			} catch (Exception e) {
				POP3Proxy.logger.fatal("Error executing the external program " + user.getSettings().getExternal());
			}
		}

		return message;
	}

	public Message getMessage(PrintWriter writer) throws IOException {
		MessageParser messageParser = new MessageParser(reader, writer);
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

	/*
	 * main para pruebas public static void main(String args[]) { try {
	 * XMLSettingsDAO dao = XMLSettingsDAO.getInstance(); POP3Client client =
	 * new POP3Client(); client.reader = new BufferedReader(new
	 * FileReader("email.txt")); Message message = client.getMessage();
	 * System.out.println(message.getContents().size()); for (Content content :
	 * message.getContents()) { if (content.getType().equals(Content.Type.TEXT))
	 * { System.out.println("--------------------------");
	 * System.out.println("TEXT"); MessageTransformerFilter mstr = new
	 * MessageTransformerFilter(); //mstr.apply(message);
	 * System.out.println(((TextContent) content).getText());
	 * System.out.println("--------------------------"); } else if
	 * (content.getType().equals(Content.Type.IMAGE)) {
	 * System.out.println("--------------------------");
	 * System.out.println("IMAGE"); ImageTransformerFilter rotate = new
	 * ImageTransformerFilter(); ImageIO.write(((ImageContent)
	 * content).getImage(), "png", new File("email.png")); rotate.apply(message,
	 * dao.getUser("tpepdc"), new NullResponseFilter());
	 * 
	 * Image image = ((ImageContent) content).getImage();
	 * 
	 * String imgStr = imageToString((BufferedImage) image, "png"); String enc64
	 * = encodeBase64(imgStr);
	 * 
	 * System.out.println(enc64);
	 * 
	 * Image parsed = base64ToImage(enc64);
	 * 
	 * ImageIO.write(((ImageContent) content).getImage(), "png", new
	 * File("test_mail.png"));
	 * 
	 * } else { System.out.println("--------------------------");
	 * System.out.println("OTHERRRR"); System.out.println(((OtherContent)
	 * content) .getContentTypeHeader()); } }
	 * 
	 * System.out.println("**********************************");
	 * System.out.println(message.reconstruct());
	 * System.out.println("**********************************"); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * }
	 * 
	 * private static String encodeBase64(String plain) { return
	 * Base64.encodeBase64String(plain.getBytes()); }
	 * 
	 * private static BufferedImage base64ToImage(String base64String) { try {
	 * byte[] imageInBytes = Base64.decodeBase64(base64String); return
	 * ImageIO.read(new ByteArrayInputStream(imageInBytes)); } catch (Exception
	 * e) { e.printStackTrace(); return null; } }
	 * 
	 * // Format: png,jpg etc private static String imageToString(BufferedImage
	 * image, String format) { ByteArrayOutputStream baos = new
	 * ByteArrayOutputStream(); try { ImageIO.write(image, format, baos); byte[]
	 * buf = baos.toByteArray(); return new String(buf); } catch (IOException e)
	 * { e.printStackTrace(); } return null; }
	 */
}
