import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class Message {

	private final Map<String, List<String>> headers;
	
	private SortedSet<Content> orderedContent; 
	private Map<Content.Type, List<Content>> contentMap;
	private String body;

	protected Message(Map<String, List<String>> headers, String body) {
		this.headers = headers;
		this.body = body;
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

}