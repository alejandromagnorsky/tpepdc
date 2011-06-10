import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class POP3Client extends Client {

	private static final int PORT = 995; // TODO

	public void connect(String host) throws IOException {
		connect(host, PORT);
		// To automatically read the welcome message
		readResponseLine();
	}

	protected String readResponseLine() throws IOException {
		String response = reader.readLine();
		logger.info("[in] : " + response);
		return response;
	}

	protected String send(String command) throws IOException {
		logger.info("[out]: " + command);
		writer.println(command);
		return readResponseLine();
	}

	public int getQuantOfNewMessages() throws IOException {
		String response = send("STAT");
		return Integer.parseInt(response.split(" ")[1]);
	}

	protected Message getMessage(int i) throws IOException {
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

	public List<Message> getMessages() throws IOException {
		int quant = getQuantOfNewMessages();
		List<Message> messages = new ArrayList<Message>();
		for (int i = 1; i <= quant; i++)
			messages.add(getMessage(i));

		return messages;
	}

	private void processBody(Message message) throws IOException {
		Content content;
		String response, boundary, contentTypeHeader, type;
		contentTypeHeader = message.getHeaders().get("Content-Type").get(0);
		boundary = contentTypeHeader
				.substring(contentTypeHeader.indexOf("=") + 1);

		StringBuilder bodyBuilder = new StringBuilder(), contentText;
		while (!(response = readResponseLine()).equals(".")) {
			bodyBuilder.append(response + "\n");

			if (response.equals("--" + boundary)) {
				response = readResponseLine();

				if (response.contains("Content-Type:")) {
					contentTypeHeader = response.substring(response
							.indexOf(':') + 2);
					type = contentTypeHeader
							.substring(0, response.indexOf('/'));
					if (type.equals("text"))
						content = new TextContent(contentTypeHeader);
					else if (type.equals("image"))
						content = new ImageContent(contentTypeHeader);
					else
						content = new OtherContent(contentTypeHeader);

					do
						bodyBuilder.append(response + "\n");
					while ((response = readResponseLine()).length() != 0);
					bodyBuilder.append(response + "\n");

					contentText = new StringBuilder();
					while (!(response = readResponseLine()).contains("--" + boundary)){
						bodyBuilder.append(response + "\n");
						contentText.append(response);
					}
								
					if (type.equals("text"))
						((TextContent)content).setText(contentText.toString());
					else if (type.equals("image"))
						((ImageContent)content).setImage(base64ToImage(contentText.toString()));
					else
						content = new OtherContent(contentTypeHeader);
				}

			}
		}
		message.setBody(bodyBuilder.toString());
	}

	private ImageIO base64ToImage(String base64String) {
		return null;
	}
}
