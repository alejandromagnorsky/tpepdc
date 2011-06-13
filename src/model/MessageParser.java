package model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.iharder.Base64;
import proxy.Content;
import proxy.ImageContent;
import proxy.OtherContent;
import proxy.POP3Proxy;
import proxy.TextContent;

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

	public Message parseMessage() throws IOException {
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
		contentTypeHeader = "Content-Type: "+message.getHeaders().get("Content-Type").get(0);
		if (contentTypeHeader.contains("multipart"))
			boundary = getBoundary(contentTypeHeader);

		StringBuilder bodyBuilder = new StringBuilder();
		if(boundary.isEmpty())
			// Single content
			putContent(message, contentTypeHeader, boundary, bodyBuilder);
		else {
			// Multipart content
			do
				processContent(message, boundary, bodyBuilder);
			while(!(response = readResponseLine()).equals("."));
		}
		message.setBody(bodyBuilder.toString());
	}

	private String putContent(Message message, String header, String boundary, StringBuilder bodyBuilder) throws IOException{
		Content content;
		String response, contentTypeHeader = header.substring(header.indexOf(':') + 2);
		String type = contentTypeHeader.substring(0, contentTypeHeader.indexOf('/'));			
		if (type.equals("text")) 
			content = new TextContent(contentTypeHeader);
		else if (type.equals("image"))
			content = new ImageContent(contentTypeHeader);
		else
			content = new OtherContent(contentTypeHeader);
		
		id++;
		content.setId(id);
		
		if(!boundary.isEmpty()){
			// Read content's headers if the message use multipart
			// because if the message is a simple content, it has the
			// content's headers in the message's headers
			response = header;
			do
				bodyBuilder.append(response + "\n");
			while ((response = readResponseLine()).length() != 0);
			bodyBuilder.append(response + "\n");
		}
		
		// Put context's data in contentText
		StringBuilder contentText = new StringBuilder();
		if(!boundary.isEmpty())
			while (!(response = readResponseLine()).contains("--" + boundary)) {
				bodyBuilder.append(response + "\n");
				contentText.append(response);
			}
		else
			while (!(response = readResponseLine()).equals(".")) {
				bodyBuilder.append(response + "\n");
				contentText.append(response);
			}
		
		if (type.equals("text"))
				((TextContent) content).setText(contentText.toString());
		else if (type.equals("image"))
			((ImageContent) content).setImage(base64ToImage(contentText.toString()));
			
		// Add content to message
		message.addContent(content);
		return response;
	}
	
	private void processContent(Message message, String boundary,
			StringBuilder bodyBuilder) throws IOException {
		String response = readResponseLine();
		
		if (response.contains("--" + boundary)) 
			response = readResponseLine();
		
		if (response.contains("Content-Type:")) {
			if (response.contains("multipart")) {
				String subBoundary = getBoundary(response);
				response = readResponseLine();
				processContent(message, subBoundary, bodyBuilder);
			} else {
				response = putContent(message, response, boundary, bodyBuilder);
				if (response.equals("--" + boundary + "--"))
					return;
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

	private BufferedImage base64ToImage(String base64String) {
		try {
			byte[] imageInBytes = Base64.decode(base64String);
			return ImageIO.read(new ByteArrayInputStream(imageInBytes));
		} catch (Exception e) {
			return null;
		}
	}
}
