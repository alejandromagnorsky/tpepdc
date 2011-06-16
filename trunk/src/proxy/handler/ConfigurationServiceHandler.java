package proxy.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.Locale;

import model.EraseSettings;
import model.Range;
import model.User;
import model.UserSettings;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dao.XMLSettingsDAO;

public class ConfigurationServiceHandler extends ServiceConnectionHandler {

	private XMLSettingsDAO loader = XMLSettingsDAO.getInstance();
	boolean changed = false;

	public ConfigurationServiceHandler(Socket socket) {
		super(socket);
	}

	public void run() {
		try {
			String request, response = "";
			User user = null;

			if (validateLogin()) {
				writer.println("OK. Welcome to the configuration service.");
				do {
					response = "";
					request = reader.readLine();
					if (request.toUpperCase().equals("HELP")) {
						showHelp();
					} else if (request.toUpperCase().equals("COMMIT")) {
						if (changed) {
							loader.commit();
							response = "OK. Commit successful.";
							changed = false;
						} else
							response = "OK. No new changes.";
					} else if (request.toUpperCase().startsWith("BLACKLIST")) {
						String[] args = request.split(" ");

						if (args[1] != null
								&& args[1]
										.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(\\\\/[0-9]{2})?")) {
							loader.saveBlacklistedIP(args[1]);
							changed = true;
							response = "OK. Added " + args[1]
									+ " to IP blacklist.";
						} else
							response = "Error. Please enter a valid IP value.";
					} else if (request.toUpperCase().startsWith("USER ")) {
						String username = request.substring(request
								.lastIndexOf(' ') + 1);
						user = loader.getUser(username);
						if (user == null)
							response = "ERROR. User " + username
									+ " does not exist.";
						else {
							response = "OK. Setting " + user.getName()
									+ " as configuration target.";
						}
						// Commands need a user
					} else if (user != null) {
						if (request.toUpperCase().equals("GET USERSETTINGS")) {
							response = printSettings(user.getSettings());
						} else if (request.toUpperCase().equals(
								"GET ERASESETTINGS")) {
							response = printEraseSettings(user.getSettings()
									.getEraseSettings());
						} else if (request.toUpperCase().startsWith(
								"SET ROTATE")) {

							String[] args = request.split(" ");

							if (args[2] != null
									&& Boolean.valueOf(args[2]) != null) {
								Boolean rotate = Boolean.valueOf(args[2]);
								user.getSettings().setRotate(rotate);
								changed = true;
								response = "OK. Rotate set to " + rotate
										+ " for user " + user.getName();
							} else
								response = "Error. Please enter a valid boolean value.";

						} else if (request.toUpperCase().startsWith("SET LEET")) {
							String[] args = request.split(" ");

							if (args[2] != null
									&& Boolean.valueOf(args[2]) != null) {
								Boolean leet = Boolean.valueOf(args[2]);
								changed = true;
								user.getSettings().setLeet(leet);
								response = "OK. Leet set to " + leet
										+ " for user " + user.getName();
							} else
								response = "Error. Please enter a valid boolean value.";

						} else if (request.toUpperCase().startsWith(
								"SET SERVER")) {

							String[] args = request.split(" ");

							if (args[2] != null) {
								String server = args[2].toLowerCase();
								user.getSettings().setServer(server);
								changed = true;
								response = "OK. Server set to " + server
										+ " for user " + user.getName();
							} else
								response = "Error. Please enter a valid string value.";

						} else if (request.toUpperCase().startsWith(
								"SET MAXLOGINS")) {

							String[] args = request.split(" ");

							if (args[2] != null
									&& Integer.valueOf(args[2]) != null) {
								Integer maxLogins = Integer.valueOf(args[2]);
								changed = true;
								user.getSettings().setMaxLogins(maxLogins);
								response = "OK. Max logins set to " + maxLogins
										+ " for user " + user.getName();
							} else
								response = "Error. Please enter a valid integer value.";

						} else if (request.toUpperCase().startsWith(
								"SET SCHEDULE_MIN")) {
							response = setScheduleMinimum(user, request);
						} else if (request.toUpperCase().startsWith(
								"SET SCHEDULE_MAX")) {
							response = setScheduleMaximum(user, request);
						} else if (request.toUpperCase().startsWith(
								"SET DATE_MIN")) {
							response = setDateMinimum(user, request);
						} else if (request.toUpperCase().startsWith(
								"SET DATE_MAX")) {
							response = setDateMaximum(user, request);
						} else if (request.toUpperCase().startsWith(
								"SET SIZE_MIN")) {
							response = setSizeMinimum(user, request);
						} else if (request.toUpperCase().startsWith(
								"SET SIZE_MAX")) {
							response = setSizeMaximum(user, request);
						} else if (request.toUpperCase().startsWith(
								"SET STRUCTURE")) {
							String[] args = request.split(" ");
							if (args[2] != null) {
								EraseSettings erase = user.getSettings()
										.getEraseSettings();
								erase.setStructure(args[2].toUpperCase());
								changed = true;
								response = "OK. Delete restriction by structure ["
										+ args[2]
										+ "] set for user "
										+ user.getName() + ". ";
							} else
								response = "Error. Please enter a valid string value.";
						} else if (request.toUpperCase().startsWith(
								"ADD CONTENT")) {
							String[] args = request.split(" ");

							if (args[2] != null) {
								EraseSettings erase = user.getSettings()
										.getEraseSettings();
								erase.addContentHeader(args[2].toLowerCase());
								changed = true;
								response = "OK. Delete restriction by content ["
										+ args[2]
										+ "] added to user "
										+ user.getName() + ".";
							} else
								response = "Error. Please enter a valid string value.";
						} else if (request.toUpperCase().startsWith(
								"ADD HEADER")) {
							String[] args = request.split(" ");

							if (args[2] != null) {
								EraseSettings erase = user.getSettings()
										.getEraseSettings();
								erase.addHeaderPattern(args[2].toLowerCase());
								changed = true;
								response = "OK. Delete restriction by header pattern ["
										+ args[2]
										+ "] added to user "
										+ user.getName() + ".";
							} else
								response = "Error. Please enter a valid string value.";
						} else if (request.toUpperCase().startsWith(
								"ADD SENDER")) {
							String[] args = request.split(" ");

							if (args[2] != null) {
								EraseSettings erase = user.getSettings()
										.getEraseSettings();
								erase.addSender(args[2].toLowerCase());
								changed = true;
								response = "OK. Delete restriction by sender ["
										+ args[2] + "] added to user "
										+ user.getName() + ".";
							} else
								response = "Error. Please enter a valid string value.";
						} else if (request.toUpperCase().equals("EXIT"))
							response = "OK.";
					} else
						response = "Error. Invalid command, or user not set.";

					if (user != null) {
						loader.saveUser(user);
					}

					writer.println(response);
				} while (isConnected() && !request.toUpperCase().equals("EXIT"));
			} else {
				writer.println("ERROR. User or password incorrect");
			}
			disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String minutesToString(int mins) {
		String str = "";
		str += (mins - mins % 60) / 60;
		str += ":";
		str += mins % 60 + "hs";
		return str;
	}

	private boolean validMinuteRange(int from, int to) {
		return from >= 0 && from <= 1440 && to >= 0 && to <= 1440 && from <= to;
	}

	private String setScheduleMinimum(User user, String request) {
		String response = "";
		String[] args = request.split(" ");

		if (args[2] != null && Integer.valueOf(args[2]) != null) {
			UserSettings settings = user.getSettings();

			Integer from = Integer.valueOf(args[2]);
			Integer to = settings.getSchedule().getTo();

			if (to == null || validMinuteRange(from, to)) {
				settings.getSchedule().setFrom(from);
				changed = true;
				response = "OK. Minimum schedule restriction for user "
						+ user.getName() + " is " + minutesToString(from);
			} else if (to != null)
				response = "Error. Range is invalid: " + minutesToString(from)
						+ "-" + minutesToString(to);
		} else
			response = "Error. Please enter a valid value. (expected integer)";
		return response;
	}

	private String setScheduleMaximum(User user, String request) {
		String response = "";
		String[] args = request.split(" ");

		if (args[2] != null && Integer.valueOf(args[2]) != null) {
			UserSettings settings = user.getSettings();

			Integer from = settings.getSchedule().getFrom();
			Integer to = Integer.valueOf(args[2]);

			if (from == null || validMinuteRange(from, to)) {
				settings.getSchedule().setTo(to);
				changed = true;
				response = "OK. Maximum schedule restriction for user "
						+ user.getName() + " is " + minutesToString(to);
			} else if (from != null)
				response = "Error. Range is invalid: " + minutesToString(from)
						+ "-" + minutesToString(to);
		} else
			response = "Error. Please enter a valid value. (expected integer)";
		return response;
	}

	private String setSizeMinimum(User user, String request) {
		String response = "";
		String[] args = request.split(" ");

		if (args[2] != null && Integer.valueOf(args[2]) != null) {
			EraseSettings erase = user.getSettings().getEraseSettings();

			Integer from = Integer.valueOf(args[2]);
			Integer to = erase.getSize().getTo();

			if (to == null || from < to) {
				erase.getSize().setFrom(from);
				changed = true;
				response = "OK. Minimum size restriction for user "
						+ user.getName() + " is " + from;
			} else if (to != null)
				response = "Error. Range is invalid: " + from + "-" + to;
		} else
			response = "Error. Please enter a valid size.";
		return response;
	}

	private String setSizeMaximum(User user, String request) {
		String response = "";
		String[] args = request.split(" ");

		if (args[2] != null && Integer.valueOf(args[2]) != null) {
			EraseSettings erase = user.getSettings().getEraseSettings();

			Integer from = erase.getSize().getFrom();
			Integer to = Integer.valueOf(args[2]);

			if (from == null || from < to) {
				erase.getSize().setTo(to);
				changed = true;
				response = "OK. Maximum size restriction for user "
						+ user.getName() + " is " + to;
			} else if (from != null)
				response = "Error. Range is invalid: " + from + "-" + to;
		} else
			response = "Error. Please enter a valid size.";
		return response;
	}

	private String setDateMinimum(User user, String request) {
		String response = "";
		String[] args = request.split(" ");
		if (args[2] != null) {

			EraseSettings erase = user.getSettings().getEraseSettings();

			DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy")
					.withLocale(new Locale("es"));

			DateTime fromDate = f.parseDateTime(args[2]);
			DateTime toDate = erase.getDate().getTo();

			if (toDate == null || fromDate.isBefore(toDate)) {
				erase.getDate().setFrom(fromDate);

				response = "OK. Minimum date restriction for user "
						+ user.getName() + " is " + f.print(fromDate);
			} else if (toDate != null)
				response = "Error. Range is invalid: " + f.print(fromDate)
						+ "-" + f.print(toDate);
		} else
			response = "Error. Please enter a valid date.";
		return response;
	}

	private String setDateMaximum(User user, String request) {
		String response = "";
		String[] args = request.split(" ");
		if (args[2] != null) {

			EraseSettings erase = user.getSettings().getEraseSettings();

			DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy")
					.withLocale(new Locale("es"));

			DateTime fromDate = erase.getDate().getFrom();
			DateTime toDate = f.parseDateTime(args[2]);

			if (fromDate == null || fromDate.isBefore(toDate)) {
				erase.getDate().setTo(toDate);
				changed = true;

				response = "OK. Maximum date restriction for user "
						+ user.getName() + " is " + f.print(toDate);
			} else if (fromDate != null)
				response = "Error. Range is invalid: " + f.print(fromDate)
						+ "-" + f.print(toDate);
		} else
			response = "Error. Please enter a valid date.";
		return response;
	}

	private void showHelp() {
		writer.println("Hello, Dave.");
		writer.println("Welcome to Configuration Manager 9999! Here you will find very [un]useful commands to restrict your worker ant.");
		writer.println("-----------------------------------------------------------------------------------------------------------------");
		writer.println("Commands list:");
		writer.println("(not case sensitive)");
		writer.println("blacklist <ip> \t\t Add a well formed ip to the blacklist.");
		writer.println("user <username> \t\t Set <username> as target for configuration");
		writer.println("commit \t\t Commit changes, if there are.");
		writer.println("exit \t\t Return to human life.");
		writer.println("set rotate|leet <value> \t\t Set transformation values. Expected: boolean");
		writer.println("set maxlogins <value> \t\t Set maximum logins per day for a user. Expected: integer");
		writer.println("set server <value> \t\t Set user default server. Expected: string");
		writer.println("set schedule_min|schedule_max <value> \t\t Set user schedule restriction. Expected: integer in range 0-1440");
		writer.println("set date_min|date_max <value> \t\t Set delete date restriction. Expected: date with pattern dd/mm/yyyy");
		writer.println("set size_min|size_max <value> \t\t Set delete size restriction. Expected: integer");
		writer.println("set structure <value> \t\t Set a delete restriction by message structure. Expected: ATTACH, NOATTACH or SENDERCOUNT_G <min_qty_of_senders>");
		writer.println("add content <value> \t\t Add a delete restriction by content. Expected: string");
		writer.println("add header <value> \t\t Add a delete restriction by header pattern. Expected: string");
		writer.println("add sender <value> \t\t Add a delete restriction by sender. Expected: string");
		writer.println("That's it, that's all.");

	}

	private String printEraseSettings(EraseSettings e) {
		String out = "";

		DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy")
				.withLocale(new Locale("es"));

		Range<DateTime> date = e.getDate();
		Range<Integer> size = e.getSize();

		if (date.hasValues())
			out += "Date: ";
		if (date.getFrom() != null)
			out += "min " + f.print(date.getFrom()) + ", ";
		if (e.getDate().getTo() != null)
			out += "max " + f.print(date.getTo()) + ", ";

		if (size.hasValues())
			out += "Size: ";
		if (size.getFrom() != null)
			out += "min " + size.getFrom() + "bytes, ";
		if (size.getTo() != null)
			out += "max " + size.getTo() + "bytes, ";

		out += "Structure: " + e.getStructure() + ", ";

		out += "Senders: " + e.getSenders().toString() + ", ";
		out += "Content: " + e.getContentTypes().toString() + ", ";
		out += "Header pattern: " + e.getHeaderPattern().toString() + "";
		return out;
	}

	private String printSettings(UserSettings s) {
		String out = "";

		if (s.getSchedule() != null && s.getSchedule().getFrom() != null
				|| s.getSchedule().getTo() != null)
			out += "Schedule: ";
		if (s.getSchedule() != null && s.getSchedule().getFrom() != null)
			out += "min " + s.getSchedule().getFrom() / 60 + ":"
					+ s.getSchedule().getFrom() % 60 + "hs ";
		if (s.getSchedule() != null && s.getSchedule().getTo() != null)
			out += "max " + s.getSchedule().getTo() / 60 + ":"
					+ s.getSchedule().getTo() % 60 + "hs, ";

		out += "Max logins: " + s.getMaxLogins() + ", ";
		out += "Leet:" + s.isLeet() + ", Rotate:" + s.isRotate() + ", ";
		out += "Server: " + s.getServer();

		return out;
	}
}
