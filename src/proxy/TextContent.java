package proxy;


public class TextContent extends Content{
	
	
	public TextContent(String contentTypeHeader) {
		super(contentTypeHeader);
		this.setType(Type.TEXT);
	}

	private String text;
	
	public String getText(){
		return this.text;
	}
	
	public void setText(String text){
		this.text = text;
	}

}
