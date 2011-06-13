package model;

public class Range<T> {

	private T from = null;
	private T to = null;

	public Range(T from, T to) {
		this.from = from;
		this.to = to;
	}

	// To speedify things
	public boolean hasValues() {
		return from != null || to != null;
	}

	public Range() {
	}

	public void setFrom(T t) {
		from = t;
	}

	public T getFrom() {
		return from;
	}

	public T getTo() {
		return to;
	}

	public void setTo(T t) {
		to = t;
	}
}
