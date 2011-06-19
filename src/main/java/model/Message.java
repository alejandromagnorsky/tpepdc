package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Message {

	private final Map<String, List<String>> headers = new HashMap<String, List<String>>();
	private SortedSet<Content> orderedContent = new TreeSet<Content>();
	private Map<Content.Type, List<Content>> contentMap = new HashMap<Content.Type, List<Content>>();
	private String body;

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void addContent(Content content) {
		List<Content> contents = contentMap.get(content.getType());
		if (contents == null) {
			contents = new ArrayList<Content>();
			contentMap.put(content.getType(), contents);
		}
		contents.add(content);
		orderedContent.add(content);
	}

	public SortedSet<Content> getContents() {
		return this.orderedContent;
	}
	
	public void addHeaderValue(String headerName, String headerValue){
		List<String> headerValues = headers.get(headerName); 
		if (headerValues == null) {
			headerValues = new ArrayList<String>();
			headers.put(headerName, headerValues);
		}
		headerValues.add(headerValue.toString());
	}

	public String putEnters(String message) {
		StringBuilder builder = new StringBuilder();
		int i, count = 0;
		builder.append(message.charAt(0));
		for(i = 1; !(message.charAt(i-1) == '\n' && message.charAt(i) == '\n') && i < message.length(); i++)
			builder.append(message.charAt(i));
		while(i < message.length()){
			if(message.charAt(i) == '\n')
				count = 0;
			else
				count++;
			
			builder.append(message.charAt(i));
			
			if(count == 76){
				count = 0;
				builder.append('\n');			
			}
			i++;
		}
		
		return builder.toString();		
	}
}