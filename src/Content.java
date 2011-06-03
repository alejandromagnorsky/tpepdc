public abstract class Content implements Comparable<Content>{

	private int id;

	public enum Type {
		PLAINTEXT, IMAGE, HTML, APPLICATION, VIDEO
	}

	private Type type;
	private String header;

	public Content(Type type) {

	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Content other = (Content) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public int compareTo(Content other){
		return this.id-other.id;
	}	
	
}
