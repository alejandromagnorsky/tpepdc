package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import proxy.Content;
import proxy.Content.Type;

public class Message {

	private final Map<String, List<String>> headers;
	
	private SortedSet<Content> orderedContent = new TreeSet<Content>(); 
	private Map<Content.Type, List<Content>> contentMap = new HashMap<Content.Type, List<Content>>();
	private String body;

	protected Message(Map<String, List<String>> headers, String body) {
		this.headers = headers;
		this.body = body;
	}
	
	protected Message(Map<String, List<String>> headers){
		this.headers = headers;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void addContent(Content content){
		List<Content> contents = contentMap.get(content.getType());
		if(contents == null){
			contents = new ArrayList<Content>();
			contentMap.put(content.getType(), contents);
		}
		contents.add(content);
		orderedContent.add(content);
	}
	
	public SortedSet<Content> getContents(){
		return this.orderedContent;
	}
}