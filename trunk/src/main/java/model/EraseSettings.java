package model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class EraseSettings {

	private List<Range<DateTime>> dates = new ArrayList<Range<DateTime>>();
	private List<Range<Integer>> sizes = new ArrayList<Range<Integer>>();
	private List<String> senders = new ArrayList<String>();
	private List<String> contentTypes = new ArrayList<String>();;
	private List<String> headerPatterns = new ArrayList<String>();;
	private String structure = new String("");

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

	public void addSizeRestriction(Range<Integer> r) {
		this.sizes.add(r);
	}

	public void addDateRestriction(Range<DateTime> r) {
		this.dates.add(r);
	}

	public List<Range<Integer>> getSizeRestrictions() {
		return sizes;
	}

	public List<Range<DateTime>> getDateRestrictions() {
		return dates;
	}
}
