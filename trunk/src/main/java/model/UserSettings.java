package model;

public class UserSettings {

	private String server;
	private Integer maxLogins;

	// In minutes of day, maximum 60*24=1440
	private Range<Integer> schedule = new Range<Integer>();
	private Boolean rotate, leet;
	private EraseSettings eraseSettings;


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

	public Range<Integer> getSchedule() {
		return schedule;
	}
}
