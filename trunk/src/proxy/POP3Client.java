package proxy;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import filter.ImageTransformerFilter;

import net.iharder.Base64;

public class POP3Client extends Client {

	private static final int PORT = 995; // TODO
	private int id;

	public void connect(String host) throws IOException {
		connect(host, PORT);
		// To automatically read the welcome message
		readResponseLine();
	}

	protected String readResponseLine() throws IOException {
		String response = reader.readLine();
		logger.info("[in]: " + response);
		return response;
	}

	public String send(String command) throws IOException {
		logger.info("[out]: " + command);
		writer.println(command);
		return readResponseLine();
	}

	public int getQuantOfNewMessages() throws IOException {
		String response = send("STAT");
		return Integer.parseInt(response.split(" ")[1]);
	}

	protected Message getMessage() throws IOException {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String headerName, headerValue, response;

		// Process headers
		while ((response = readResponseLine()).length() != 0) {
			if (response.startsWith("\t"))
				continue;

			int separator = response.indexOf(":");
			if (separator == -1)
				continue;

			headerName = response.substring(0, separator);
			if (response.length() > separator)
				headerValue = response.substring(separator + 2);
			else
				headerValue = "";

			List<String> headerValues = headers.get(headerName);
			if (headerValues == null) {
				headerValues = new ArrayList<String>();
				headers.put(headerName, headerValues);
			}
			headerValues.add(headerValue);
		}

		// Between the header and the body there is a \n
		Message message = new Message(headers);
		processBody(message);
		return message;
	}

	private void processBody(Message message) throws IOException {
		String response, contentTypeHeader, boundary = "";
		id = 0;
		contentTypeHeader = message.getHeaders().get("Content-Type").get(0);
		if (contentTypeHeader.contains("multipart")) {
			boundary = contentTypeHeader.substring(contentTypeHeader
					.indexOf("=") + 1);
		}

		StringBuilder bodyBuilder = new StringBuilder();
		response = readResponseLine();
		if (boundary == "") {
			addContent(message, boundary, bodyBuilder);
		} else {
			while (!response.equals(".")) {
				bodyBuilder.append(response + "\n");

				addContent(message, boundary, bodyBuilder);
				response = readResponseLine();
			}
		}
		message.setBody(bodyBuilder.toString());
	}

	private void addContent(Message message, String boundary,
			StringBuilder bodyBuilder) throws IOException {
		Content content;
		String response, contentTypeHeader, type;
		StringBuilder contentText;

		response = readResponseLine();
		if (response.contains("multipart")) {
			String subBoundary = response.substring(response.indexOf("=") + 1);
			response = readResponseLine();
			addContent(message, subBoundary, bodyBuilder);
			response = readResponseLine();
		}

		if (response.contains("--" + boundary)) {
			response = readResponseLine();
		}
		if (boundary == "" || response.contains("Content-Type:")) {
			if (boundary == "") {
				contentTypeHeader = message.getHeaders().get("Content-Type")
						.get(0).substring(response.indexOf(':') + 1);
			} else {
				contentTypeHeader = response
						.substring(response.indexOf(':') + 2);
			}
			type = contentTypeHeader.substring(0, contentTypeHeader
					.indexOf('/'));
			id++;
			if (type.equals("text")) {
				content = new TextContent(contentTypeHeader);
			} else if (type.equals("image")) {
				content = new ImageContent(contentTypeHeader);
			} else {
				content = new OtherContent(contentTypeHeader);
			}

			content.setId(id);

			do
				bodyBuilder.append(response + "\n");
			while ((response = readResponseLine()).length() != 0);
			bodyBuilder.append(response + "\n");

			contentText = new StringBuilder();

			if (boundary == "") {
				while (!(response = readResponseLine()).equals(".")) {
					bodyBuilder.append(response + "\n");
					contentText.append(response);
				}
			} else {
				while (!(response = readResponseLine()).contains("--"
						+ boundary)) {
					bodyBuilder.append(response + "\n");
					contentText.append(response);
				}
			}

			if (type.equals("text"))
				((TextContent) content).setText(contentText.toString());
			else if (type.equals("image"))
				((ImageContent) content).setImage(base64ToImage(contentText
						.toString()));
			else
				content = new OtherContent(contentTypeHeader);

			message.addContent(content);
			if (boundary == "" || response.equals("--" + boundary + "--"))
				return;
			addContent(message, boundary, bodyBuilder);
		}

	}

	private BufferedImage base64ToImage(String base64String) {
		try {
			byte[] imageInBytes = Base64.decode(base64String);
			return ImageIO.read(new ByteArrayInputStream(imageInBytes));
		} catch (Exception e) {
			return null;
		}
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
					System.out.println(((TextContent) content).getText());
				} else if (content.getType().equals(Content.Type.IMAGE)) {
					System.out.println("--------------------------");
					System.out.println("IMAGE");
					ImageTransformerFilter rotate = new ImageTransformerFilter();
					rotate.apply(message);

					ImageIO.write(((ImageContent) content).getImage(), "png",
							new File("email.png"));
				} else {
					System.out.println("--------------------------");
					System.out.println("OTHERRRR");
					System.out.println(((OtherContent) content)
							.getContentTypeHeader());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
