package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import proxy.Content;

public class Message {

	private final Map<String, List<String>> headers;
	private String mainHeader;
	private SortedSet<Content> orderedContent = new TreeSet<Content>(); 
	private Map<Content.Type, List<Content>> contentMap = new HashMap<Content.Type, List<Content>>();
	private String body;
	private String bodySkeleton;

	//TODO borrar? no se esta usando..
//	protected Message(Map<String, List<String>> headers, String body) {
//		this.headers = headers;
//		this.body = body;
//	}
	
	protected Message(Map<String, List<String>> headers, String mainHeader){
		this.headers = headers;
		this.mainHeader = mainHeader;
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
	
	public String getBodySkeleton() {
		return bodySkeleton;
	}

	public void setBodySkeleton(String bodySkeleton) {
		this.bodySkeleton = bodySkeleton;
	}
	
	public String getMainHeader() {
		return mainHeader;
	}

	public void setMainHeader(String mainHeader) {
		this.mainHeader = mainHeader;
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