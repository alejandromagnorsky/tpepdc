package settings;

import model.Range;

public class UserSettings {

	private String server;
	private Integer maxLogins;

	// In minutes of day, maximum 60*24=1440
	private Range<Integer> schedule = new Range<Integer>();
	private Boolean rotate, leet;
	private EraseSettings eraseSettings;

	public String toString() {
		String out = "";
		if (getSchedule().getFrom() != null)
			out += "ScheduleFrom: " + getSchedule().getFrom() / 60 + "hs, ";
		if (getSchedule().getTo() != null)
			out += "ScheduleTo: " + getSchedule().getTo() / 60 + "hs, ";
		out += "Max logins: " + maxLogins + ", ";
		out += "Transform: leet=" + leet + ", rotate=" + rotate + ", ";
		out += "Server: " + server;

		return out;
	}

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

	public boolean isRotate() {
		return rotate;
	}

	public void setLeet(Boolean leet) {
		this.leet = leet != null ? leet : false;
	}

	public boolean isLeet() {
		return leet;
	}

	public Range<Integer> getSchedule() {
		return schedule;
	}
}
