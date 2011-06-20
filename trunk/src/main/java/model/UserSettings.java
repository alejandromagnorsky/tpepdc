package model;

import java.util.ArrayList;
import java.util.List;

import proxy.POP3Proxy;

public class UserSettings {

	private String server = POP3Proxy.DEFAULT_SERVER;
	private Integer maxLogins = -1;

	// In minutes of day, maximum 60*24=1440
	private List<Range<Integer>> scheduleList = new ArrayList<Range<Integer>>();
	private Boolean rotate = false, leet = false;
	private String external = "none";
	private EraseSettings eraseSettings = new EraseSettings();

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Integer getMaxLogins() {
		return maxLogins;
	}

	public void setMaxLogins(Integer maxLogins) {
		this.maxLogins = maxLogins;
	}

	public EraseSettings getEraseSettings() {
		return eraseSettings;
	}

	public void setEraseSettings(EraseSettings eraseSettings) {
		this.eraseSettings = eraseSettings;
	}

	public void setRotate(Boolean rotate) {
		this.rotate = rotate != null ? rotate : false;
	}

	public Boolean isRotate() {
		return rotate;
	}

	public void setLeet(Boolean leet) {
		this.leet = leet != null ? leet : false;
	}

	public Boolean isLeet() {
		return leet;
	}

	public void addScheduleRestriction(Range<Integer> r) {
		this.scheduleList.add(r);
	}

	public List<Range<Integer>> getScheduleList() {
		return scheduleList;
	}

	public void setExternal(String external) {
		this.external = external;
	}

	public String getExternal() {
		return external;
	}
}
