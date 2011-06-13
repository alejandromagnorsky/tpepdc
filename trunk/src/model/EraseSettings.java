package model;

import java.util.ArrayList;
import java.util.List;


import org.joda.time.DateTime;

public class EraseSettings {

	private Range<DateTime> date = new Range<DateTime>();
	private Range<Integer> size = new Range<Integer>();
	private List<String> senders = new ArrayList<String>();
	private List<String> contentTypes = new ArrayList<String>();;
	private List<String> headerPatterns = new ArrayList<String>();;
	private String structure = null;

	public String toString() {
		String out = "";
		
		if (date.getFrom() != null)
			out += "Date from: " + date.getFrom() + ", ";
		if (date.getTo() != null)
			out += "Date to: " + date.getTo() + ", ";
		if (size.getFrom() != null)
			out += "Size from: " + size.getFrom() + ", ";
		if (size.getTo() != null)
			out += "Size to: " + size.getTo() + ", ";

		out += "Structure: " + structure + ", ";

		out += senders.toString();
		out += contentTypes.toString();
		out += headerPatterns.toString();
		return out;
	}

	public List<String> getSenders() {
		return senders;
	}

	public void addSender(String sender) {
		this.senders.add(sender);
	}

	public List<String> getContentTypes() {
		return contentTypes;
	}

	public void addContentHeader(String contentHeader) {
		this.contentTypes.add(contentHeader);
	}

	public List<String> getHeaderPattern() {
		return headerPatterns;
	}

	public void addHeaderPattern(String headerPattern) {
		this.headerPatterns.add(headerPattern);
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public Range<DateTime> getDate() {
		return date;
	}

	public Range<Integer> getSize() {
		return size;
	}
}
