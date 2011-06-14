package model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import proxy.POP3Proxy;

public class MessageParser {

	private int id;
	private BufferedReader reader;
	private StringBuilder skeleton;

	public MessageParser(BufferedReader reader) {
		this.id = 0;
		this.reader = reader;
		this.skeleton = new StringBuilder();
	}

	private String readResponseLine() throws IOException {
		String response = reader.readLine();
		POP3Proxy.logger.info("[in]: " + response);
		return response;
	}

	public Message parseMessage() throws IOException {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String headerName, headerValue, response;
		StringBuilder mainHeader = new StringBuilder();

		// Process headers
		while ((response = readResponseLine()).length() != 0) {
			mainHeader.append(response + "\n");
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
		Message message = new Message(headers, mainHeader.toString());
		processBody(message);
		return message;
	}

	private void processBody(Message message) throws IOException {
		String response, contentTypeHeader, boundary = "";
		contentTypeHeader = "Content-Type: "
				+ message.getHeaders().get("Content-Type").get(0);
		if (contentTypeHeader.contains("multipart"))
			boundary = getBoundary(contentTypeHeader);

		StringBuilder bodyBuilder = new StringBuilder();
		if (boundary.isEmpty())
			// Single content
			putContent(message, contentTypeHeader, boundary, bodyBuilder);
		else {
			// Multipart content
			do
				processContent(message, boundary, bodyBuilder);
			while (!(response = readResponseLine()).equals("."));
		}
		message.setBody(bodyBuilder.toString());
		message.setBodySkeleton(skeleton.toString());
	}

	private String putContent(Message message, String header, String boundary,
			StringBuilder bodyBuilder) throws IOException {
		Content content;
		String response, contentTypeHeader = header.substring(header
				.indexOf(':') + 2);
		String type = contentTypeHeader.substring(0,
				contentTypeHeader.indexOf('/'));
		String encoding = null;
		if (type.equals("text"))
			content = new TextContent(contentTypeHeader);
		else if (type.equals("image"))
			content = new ImageContent(contentTypeHeader);
		else
			content = new OtherContent(contentTypeHeader);

		id++;
		skeleton.append(">>" + id + "<<" + "\n");
		content.setId(id);

		if (!boundary.isEmpty()) {
			// Read content's headers if the message use multipart
			// because if the message is a simple content, it has the
			// content's headers in the message's headers
			response = header;
			do {
				bodyBuilder.append(response + "\n");
				if (response.contains("Content-Transfer-Encoding:"))
					encoding = response.substring(response.indexOf(":") + 2);
			} while ((response = readResponseLine()).length() != 0);
			bodyBuilder.append(response + "\n");
		} else {
			if (message.getHeaders().get("Content-Transfer-Encoding") != null)
				encoding = message.getHeaders()
						.get("Content-Transfer-Encoding").get(0);
		}

		// Put context's data in contentText
		StringBuilder contentText = new StringBuilder();
		if (!boundary.isEmpty())
			while (!(response = readResponseLine()).contains("--" + boundary)) {
				bodyBuilder.append(response + "\n");
				contentText.append(response + "\n");
			}
		else
			while (!(response = readResponseLine()).equals(".")) {
				bodyBuilder.append(response + "\n");
				contentText.append(response + "\n");
			}

		if (type.equals("text")) {
			if (encoding != null && encoding.equals("quoted-printable"))
				((TextContent) content)
						.setText(decodeQuotedPrintable(contentText.toString()));
			else
				((TextContent) content).setText(contentText.toString());
		} else if (type.equals("image"))
			if (encoding != null && encoding.equals("base64"))
				((ImageContent) content).setImage(base64ToImage(contentText
						.toString()));

		// Add content to message
		message.addContent(content);
		return response;
	}

	private void processContent(Message message, String boundary,
			StringBuilder bodyBuilder) throws IOException {
		String response = readResponseLine();

		if (response.contains("--" + boundary)) {
			response = readResponseLine();
			skeleton.append(response + "\n");
		}

		if (response.contains("Content-Type:")) {
			if (response.contains("multipart")) {
				String subBoundary = getBoundary(response);
				response = readResponseLine();
				skeleton.append(response + "\n");
				processContent(message, subBoundary, bodyBuilder);
			} else {
				response = putContent(message, response, boundary, bodyBuilder);
				if (response.equals("--" + boundary + "--")) {
					skeleton.append(response + "\n");
					return;
				}
				processContent(message, boundary, bodyBuilder);
			}
		}
	}

	private String getBoundary(String line) {
		String boundary = line.substring(line.indexOf("=") + 1);
		String[] tmp = boundary.split("\"");
		if (tmp.length == 2)
			boundary = tmp[1];
		return boundary;
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

	private String encodeBase64(String plain) {
		return Base64.encodeBase64String(plain.getBytes());
	}

	private String decodeBase64(String base64String) {
		byte[] buf = Base64.decodeBase64(base64String);

		return byteArrayToString(buf);
	}

	private BufferedImage base64ToImage(String base64String) {
		try {
			byte[] imageInBytes = Base64.decodeBase64(base64String);
			return ImageIO.read(new ByteArrayInputStream(imageInBytes));
		} catch (Exception e) {
			return null;
		}
	}

	private String decodeQuotedPrintable(String quotedPrintable) {
		try {
			quotedPrintable = quotedPrintable.replaceAll("=\n", "-\n");
			QuotedPrintableCodec codec = new QuotedPrintableCodec("ISO-8859-1");
			return codec.decode(quotedPrintable);
		} catch (Exception e) {
			return null;
		}
	}

	private String encodeQuotedPrintable(String text) {
		try {
			QuotedPrintableCodec codec = new QuotedPrintableCodec("ISO-8859-1");
			String ans = codec.encode(text);
			ans = ans.replaceAll("-=0A", "=\n");
			ans = ans.replaceAll("=0A", "\n");
			return ans;
		} catch (Exception e) {
			return null;
		}
	}
}
