package model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import proxy.POP3Proxy;

public class MessageParser {

	private int id;
	private BufferedReader reader;

	public MessageParser(BufferedReader reader) {
		this.id = 0;
		this.reader = reader;
	}

	private String readResponseLine() throws IOException {
		String response = reader.readLine();
		POP3Proxy.logger.info("[in]: " + response);
		return response;
	}

	public Message parseMessage(PrintWriter writer) throws IOException {
		Message message = new Message();
		String response = readResponseLine();
		processHeaders(response, message, writer);
		processBody(message, writer);
		return message;
	}

	private void processHeaders(String response, Message message, PrintWriter writer) throws IOException {
		String headerName = null;
		StringBuilder headerValue = new StringBuilder();
		do {
			writer.println(response);
			// For each header, first get the name
			// and then get the content that might be
			// in different lines and append them in
			// a string
			if(headerName == null) {
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
		if(response.length() == 0) {
			writer.println();
			return;
		}
		else
			processHeaders(response, message, writer);
	}
	
	private void processBody(Message message, PrintWriter writer) throws IOException {
		String response, contentTypeHeader, boundary = "";
		if(message.getHeaders().get("Content-Type") == null)
			contentTypeHeader = "Content-Type: text/plain";
		else
			contentTypeHeader = "Content-Type: " + message.getHeaders().get("Content-Type").get(0);
				
		if (contentTypeHeader.toUpperCase().contains("MULTIPART"))
			boundary = getBoundary(contentTypeHeader, writer);

		if (boundary.isEmpty())
			// Single content
			putContent(message, contentTypeHeader, boundary, writer);
		else {
			// Multipart content
			do
				processContents(message, boundary, writer);
			while (!(response = readResponseLine()).equals("."));
		}
		writer.println(".");
	}

	private String putContent(Message message, String header, String boundary, PrintWriter writer) throws IOException {
		Content content;
		String response, contentTypeHeader = header.substring(header.indexOf(':') + 2);
		String type = contentTypeHeader.substring(0, contentTypeHeader.indexOf('/'));
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
				encoding = message.getHeaders().get("Content-Transfer-Encoding").get(0);
		}

		// Put content's data in contentText
		StringBuilder contentText = new StringBuilder();
		if (!boundary.isEmpty())
			while (!(response = readResponseLine()).contains("--" + boundary)) {
				writer.println(response);
				contentText.append(response + "\n");
			}
		else
			while (!(response = readResponseLine()).equals(".")) {
				writer.println(response);
				contentText.append(response + "\n");
			}

		if (type.toUpperCase().equals("TEXT")) {
			if (encoding != null && encoding.equals("quoted-printable"))
				((TextContent) content).setText(decodeQuotedPrintable(contentText.toString()));
			else
				((TextContent) content).setText(contentText.toString());
		} else if (type.toUpperCase().equals("IMAGE"))
			if (encoding != null && encoding.equals("base64"))
				((ImageContent) content).setImage(base64ToImage(contentText.toString()));

		// Add content to message
		message.addContent(content);
		return response;
	}

	private void processContents(Message message, String boundary, PrintWriter writer) throws IOException {
		String response = readResponseLine();

		if (response.contains("--" + boundary))
			response = readResponseLine();
		

		if (response.contains("Content-Type:")) {
			if (response.toUpperCase().contains("MULTIPART")) {
				writer.println("--" + boundary);
				writer.println(response);
				String subBoundary = getBoundary(response, writer);
				writer.println();
				response = readResponseLine();
				processContents(message, subBoundary, writer);
			} else {
				response = putContent(message, response, boundary, writer);
				if (response.equals("--" + boundary + "--")) {
					writer.println(response);
					return;
				}
				processContents(message, boundary, writer);
			}
		}
	}
	

	private String getBoundary(String line, PrintWriter writer) throws IOException {
		if(line.indexOf("=") == -1){
			line = readResponseLine();
			writer.println(line);
		}
		String boundary = line.substring(line.indexOf("=") + 1);			
		String[] tmp = boundary.split("\"");
		if (tmp.length == 2)
			boundary = tmp[1];
		return boundary;
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
	
	private String decodeBase64(String base64String) {
		byte[] buf = Base64.decodeBase64(base64String);
		return new String(buf);
	}
}
