package model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import proxy.POP3Proxy;

public class MessageParser {

	private int id;
	private BufferedReader reader;
	private PrintWriter writer;
	private User user;

	public MessageParser(BufferedReader reader, PrintWriter writer, User user) {
		this.id = 0;
		this.reader = reader;
		this.writer = writer;
		this.user = user;
	}

	private String readResponseLine() throws IOException {
		String response = reader.readLine();
		POP3Proxy.logger.info("[in]: " + response);
		return response;
	}

	public Message parseMessage() throws IOException {
		Message message = new Message();
		String response = readResponseLine();
		parseHeaders(response, message);
		parseBody(message);
		return message;
	}

	private void parseHeaders(String response, Message message)
			throws IOException {
		String headerName = null;
		StringBuilder headerValue = new StringBuilder();
		do {
			writer.println(response);
			// For each header, first get the name
			// and then get the content that might be
			// in different lines and append them in
			// a string
			if (headerName == null) {
				int separator = response.indexOf(":");
				if (separator == -1)
					return;
				headerName = response.substring(0, separator);
				if (response.length() > separator)
					headerValue.append(response.substring(separator + 2));
			} else
				headerValue.append(response);
		} while ((response = readResponseLine()).length() != 0
				&& (response.startsWith(" ") || response.startsWith("\t")));

		message.addHeaderValue(headerName, headerValue.toString());

		// Body's start
		if (response.length() == 0) {
			writer.println();
			return;
		} else
			parseHeaders(response, message);
	}

	private void parseBody(Message message) throws IOException {
		String response, contentTypeHeader, boundary = "";
		if (message.getHeaders().get("Content-Type") == null)
			contentTypeHeader = "Content-Type: text/plain";
		else
			contentTypeHeader = "Content-Type: "
					+ message.getHeaders().get("Content-Type").get(0);

		if (contentTypeHeader.toUpperCase().contains("MULTIPART"))
			boundary = getBoundary(contentTypeHeader);

		if (boundary.isEmpty())
			// Single content
			putContent(message, contentTypeHeader, boundary);
		else {
			// Multipart content
			do
				parseContents(message, boundary);
			while (!(response = readResponseLine()).equals("."));
		}
		writer.println(".");
	}

	private String putContent(Message message, String header, String boundary)
			throws IOException {
		Content content;
		String response, contentTypeHeader = header.substring(header
				.indexOf(':') + 2);
		String type = contentTypeHeader.substring(0, contentTypeHeader
				.indexOf('/'));
		String encoding = null;
		if (type.toUpperCase().equals("TEXT"))
			content = new TextContent(contentTypeHeader);
		else if (type.toUpperCase().equals("IMAGE"))
			content = new ImageContent(contentTypeHeader);
		else
			content = new OtherContent(contentTypeHeader);

		id++;
		content.setId(id);

		if (!boundary.isEmpty()) {
			writer.println("--" + boundary);
			// Read content's headers if the message use multipart
			// because if the message is a simple content, it has the
			// content's headers in the message's headers
			response = header;
			do {
				writer.println(response);
				if (response.contains("Content-Transfer-Encoding:"))
					encoding = response.substring(response.indexOf(":") + 2);
			} while ((response = readResponseLine()).length() != 0);
			writer.println(response);
		} else {
			if (message.getHeaders().get("Content-Transfer-Encoding") != null)
				encoding = message.getHeaders()
						.get("Content-Transfer-Encoding").get(0);
		}
		boolean needToTransformImage = needToTransformImage(type);
		boolean needToTransformText = needToTransformText(type);
		// Put content's data in contentText
		StringBuilder contentText = new StringBuilder();
		if (!boundary.isEmpty()
				&& (needToTransformImage || needToTransformText)) {
			if (needToTransformImage) {
				while (!(response = readResponseLine()).contains("--"
						+ boundary)) {
					contentText.append(response + "\n");
				}
			} else if (needToTransformText && contentTypeHeader.toUpperCase().contains("TEXT/PLAIN")) {
				while (!(response = readResponseLine()).contains("--"
						+ boundary)) {
					if (encoding != null && encoding.equals("quoted-printable")) {
						contentText.append(response + "\n");
					} else {
						TextTransformer textTransformer = new TextTransformer();
						String transformedLine = textTransformer.transform(
								response, message);
						writer.println(transformedLine);
					}
				}
			} else {
				while (!(response = readResponseLine()).contains("--"
						+ boundary)) {
					writer.println(response);
				}
			}
		} else {
			while (!(response = readResponseLine()).equals(".")) {
				writer.println(response);
			}
		}
		
		if (needToTransformImage && type.toUpperCase().equals("IMAGE")) {
			if (encoding != null && encoding.equals("base64")) {
				// ImageTransformer imageTransformer = new ImageTransformer();
				// String transformedImage = imageTransformer.transform(contentText.toString());
				printLines(contentText.toString());
				// TODO cambiar la linea anterior por esta y descomentar lo de arriba cuando anden las imagenes
				// printLines(transformedImage);

			}
		} else if(needToTransformText &&
				encoding != null && encoding.equals("quoted-printable")) {
			TextTransformer textTransformer = new TextTransformer();
			printLines(decodeQuotedPrintable(contentText.toString()));
		}

		// Add content to message
		message.addContent(content);
		return response;
	}

	private void parseContents(Message message, String boundary)
			throws IOException {
		String response = readResponseLine();

		if (response.contains("--" + boundary))
			response = readResponseLine();

		if (response.contains("Content-Type:")) {
			if (response.toUpperCase().contains("MULTIPART")) {
				writer.println("--" + boundary);
				writer.println(response);
				String subBoundary = getBoundary(response);
				writer.println();
				response = readResponseLine();
				parseContents(message, subBoundary);
			} else {
				response = putContent(message, response, boundary);
				if (response.equals("--" + boundary + "--")) {
					writer.println(response);
					return;
				}
				parseContents(message, boundary);
			}
		}
	}

	private String getBoundary(String line) throws IOException {
		if (line.indexOf("=") == -1) {
			line = readResponseLine();
			writer.println(line);
		}
		String boundary = line.substring(line.indexOf("=") + 1);
		String[] tmp = boundary.split("\"");
		if (tmp.length == 2)
			boundary = tmp[1];
		return boundary;
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

	private void printLines(String message) {
		StringBuilder builder = new StringBuilder();
		int i = 0, count = 0;
		// builder.append(message.charAt(0));
		while (i < message.length()) {
			builder.append(message.charAt(i));

			if (count == 76) {
				count = 0;
				// builder.append('\n');
				// System.out.println(builder.toString());
				writer.println(builder.toString());
				builder = new StringBuilder();
			} else {
				count++;
			}
			i++;
		}

	}

	private boolean needToTransformImage(String type) {
		if (user != null && user.getSettings() != null) {
			if (type.toUpperCase().equals("IMAGE")) {
				if (user.getSettings().isRotate() != null
						&& user.getSettings().isRotate()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean needToTransformText(String type) {
		if (user != null && user.getSettings() != null) {
			if (type.toUpperCase().equals("TEXT")) {
				if (user.getSettings().isRotate() != null
						&& user.getSettings().isLeet()) {
					return true;
				}
			}
		}
		return false;
	}
}
