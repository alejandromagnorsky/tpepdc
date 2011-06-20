package proxy.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;

import model.EraseSettings;
import model.Range;
import model.User;
import model.UserSettings;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dao.XMLSettingsDAO;

public class ConfigurationServiceHandler extends ServiceConnectionHandler {

	private XMLSettingsDAO loader = XMLSettingsDAO.getInstance();
	private static Logger logger = Logger.getLogger("logger");
	boolean changed = false;

	public ConfigurationServiceHandler(Socket socket) {
		super(socket);
	}

	public void run() {
		try {
			String request, response = "";
			User user = null;

			if (validateLogin()) {
				writer.println("+OK. Welcome to the configuration service.");

				// While user is logged
				do {
					response = "";
					request = reader.readLine();
					String args[] = request.split(" ");
					int argc = args.length;

					if (request != null) {
						if (request.toUpperCase().equals("RELOAD")) {
							writer.println("WARNING: Reloading from xml will discard every change not commited. Are you sure? (y/n)");

							String answer = reader.readLine();

							while (!answer.equals("y") && !answer.equals("n")) {
								writer.println("Expected: (y/n)");
								answer = reader.readLine();
							}

							if (answer.equals("y")) {
								loader.load();

								if (user != null)
									user = loader.getUser(user.getName());

								response = "Settings reloaded from XML file.";
							} else
								response = "Reload cancelled.";
						} else if (request.toUpperCase().equals("COMMIT")) {
							if (changed) {
								loader.commit();
								response = "+OK. Commit successful.";
								changed = false;
							} else
								response = "+OK. No new changes.";

							// BLACKLIST
						} else if (request.toUpperCase()
								.startsWith("BLACKLIST")) {
							try {
								if (argc > 1
										&& args[1] != null
										&& args[1]
												.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(\\\\/[0-9]{2})?")) {
									loader.saveBlacklistedIP(args[1]);
									changed = true;
									response = "+OK. Added " + args[1]
											+ " to IP blacklist.";
								} else
									response = "-ERR. Please enter a valid IP value.";
							} catch (PatternSyntaxException e) {
								logger.fatal("Unexpected parsing error: IP regular expresion is invalid.");
							}

							// USER SET
						} else if (request.toUpperCase().startsWith("USER ")) {

							if (argc > 1) {
								String username = args[1];

								user = loader.getUser(username);
								if (user == null)
									response = "-ERR. User " + username
											+ " does not exist.";
								else {
									response = "+OK. Setting " + user.getName()
											+ " as configuration target.";
								}
							} else
								response = "-ERR. Enter a username.";

							// Commands need a user
						} else if (user != null) {

							if (request.toUpperCase()
									.equals("GET USERSETTINGS")) {
								response = printSettings(user.getSettings());

							} else if (request.toUpperCase().equals(
									"GET ERASESETTINGS")) {
								response = printEraseSettings(user
										.getSettings().getEraseSettings());

							} else if (request.toUpperCase().startsWith(
									"SET ROTATE")) {

								if (argc > 2 && args[2] != null
										&& Boolean.valueOf(args[2]) != null) {
									Boolean rotate = Boolean.valueOf(args[2]);
									user.getSettings().setRotate(rotate);
									changed = true;
									response = "+OK. Rotate set to " + rotate
											+ " for user " + user.getName();
								} else
									response = "-ERR. Please enter a valid boolean value.";

							} else if (request.toUpperCase().startsWith(
									"SET LEET")) {

								if (argc > 2 && args[2] != null
										&& Boolean.valueOf(args[2]) != null) {
									Boolean leet = Boolean.valueOf(args[2]);
									changed = true;
									user.getSettings().setLeet(leet);
									response = "+OK. Leet set to " + leet
											+ " for user " + user.getName();
								} else
									response = "-ERR. Please enter a valid boolean value.";

							} else if (request.toUpperCase().startsWith(
									"SET SERVER")) {

								if (argc > 2 && args[2] != null) {
									String server = args[2];
									user.getSettings().setServer(server);
									changed = true;
									response = "+OK. Server set to " + server
											+ " for user " + user.getName();
								} else
									response = "-ERR. Please enter a valid string value.";

							} else if (request.toUpperCase().startsWith(
									"SET MAXLOGINS")) {

								try {
									if (argc > 2 && args[2] != null
											&& Integer.valueOf(args[2]) != null) {
										Integer maxLogins = Integer
												.valueOf(args[2]);
										changed = true;
										user.getSettings().setMaxLogins(
												maxLogins);
										response = "+OK. Max logins set to "
												+ maxLogins + " for user "
												+ user.getName();
									} else
										response = "-ERR. Please enter a valid integer value.";
								} catch (NumberFormatException e) {
									response = "-ERR. Please enter a valid integer value.";
								}

							} else if (request.toUpperCase().startsWith(
									"ADD SCHEDULE_RANGE")) {
								response = addScheduleRange(user, request);

							} else if (request.toUpperCase().startsWith(
									"ADD DATE_RANGE")) {
								response = addDateRestriction(user, request);
							} else if (request.toUpperCase().startsWith(
									"ADD SIZE_RANGE")) {
								response = addSizeRestriction(user, request);

							} else if (request.toUpperCase().startsWith(
									"SET STRUCTURE")) {

								if (argc > 2 && args[2] != null) {
									EraseSettings erase = user.getSettings()
											.getEraseSettings();
									erase.setStructure(args[2].toUpperCase());
									changed = true;
									response = "+OK. Delete restriction by structure ["
											+ args[2]
											+ "] set for user "
											+ user.getName() + ". ";
								} else
									response = "-ERR. Please enter a valid string value.";

							} else if (request.toUpperCase().startsWith(
									"ADD CONTENT")) {

								if (argc > 2 && args[2] != null) {
									EraseSettings erase = user.getSettings()
											.getEraseSettings();
									erase.addContentHeader(args[2]);
									changed = true;
									response = "+OK. Delete restriction by content ["
											+ args[2]
											+ "] added to user "
											+ user.getName() + ".";
								} else
									response = "-ERR. Please enter a valid string value.";
							} else if (request.toUpperCase().startsWith(
									"ADD HEADER")) {
								if (argc > 2 && args[2] != null) {
									String tmp = "ADD HEADER";
									String value = request.substring(
											tmp.length() + 1).trim();
									EraseSettings erase = user.getSettings()
											.getEraseSettings();
									erase.addHeaderPattern(value);

									changed = true;
									response = "+OK. Delete restriction by header pattern ["
											+ value
											+ "] added to user "
											+ user.getName() + ".";
								} else
									response = "-ERR. Please enter a valid <header-field:regex> pattern.";

							} else if (request.toUpperCase().startsWith(
									"ADD SENDER")) {

								if (argc > 2 && args[2] != null) {
									EraseSettings erase = user.getSettings()
											.getEraseSettings();
									erase.addSender(args[2]);
									changed = true;
									response = "+OK. Delete restriction by sender ["
											+ args[2]
											+ "] added to user "
											+ user.getName() + ".";
								} else
									response = "-ERR. Please enter a valid string value.";
							} else if (request.toUpperCase().equals("EXIT"))
								response = "+OK. Goodbye.";
							else
								response = "Error. Invalid command, or user not set.";
						} else if (!request.toUpperCase().equals("EXIT")
								&& !request.toUpperCase().equals("QUIT"))
							response = "Error. Invalid command, or user not set.";

						if (user != null)
							loader.saveUser(user);

						writer.println(response);
					}
				} while (isConnected() && request != null
						&& !request.toUpperCase().equals("QUIT")
						&& !request.toUpperCase().equals("EXIT"));
			} else {
				writer.println("ERROR. User or password incorrect");
			}
			disconnect();
		} catch (IOException e) {
			logger.fatal("Error with connection. Disconnecting client...");
			try {
				disconnect();
			} catch (Exception e1) {
				logger.fatal("Error disconnecting");
			}
		}
	}

	private String minutesToString(Integer mins) {
		if (mins == null)
			return "unbounded";
		String str = "";
		str += (mins - mins % 60) / 60;
		str += ":";
		str += mins % 60 + "hs";
		return str;
	}

	private boolean validMinuteRange(int from, int to) {
		return from >= 0 && from <= 1440 && to >= 0 && to <= 1440 && from <= to;
	}

	private Integer getMinutsFromString(String str)
			throws NumberFormatException {
		int low = str.indexOf(":");

		if (low != -1) {
			Integer hour = Integer.valueOf(str.substring(0, low));
			Integer minutes = Integer.valueOf(str.substring(low + 1));
			Integer out = hour * 60 + minutes;
			return out;
		}

		return null;
	}

	private String addScheduleRange(User user, String request) {
		String response = "";
		String[] args = request.split(" ");

		if (args.length > 3 && args[3] != null && args[2] != null) {
			UserSettings settings = user.getSettings();
			Integer fromSchedule = null, toSchedule = null;

			try {
				if (!args[2].equals("N"))
					fromSchedule = getMinutsFromString(args[2]);
				if (!args[3].equals("N"))
					toSchedule = getMinutsFromString(args[3]);
			} catch (Exception e) {
				return "Please enter a valid schedule range 0-1440.";
			}

			// If range is valid or one value is unbounded, continue
			if ((fromSchedule != null && toSchedule != null && validMinuteRange(
					fromSchedule, toSchedule))
					|| (fromSchedule == null && toSchedule != null && validMinuteRange(
							0, toSchedule))
					|| (toSchedule == null && fromSchedule != null && validMinuteRange(
							fromSchedule, 1440))) {

				Range<Integer> range = new Range<Integer>();
				range.setFrom(fromSchedule);
				range.setTo(toSchedule);
				settings.addScheduleRestriction(range);

				changed = true;
				response = "OK. Schedule range added for user "
						+ user.getName() + " ";
			} else
				response = "Error. Range is invalid: "
						+ minutesToString(fromSchedule) + "-"
						+ minutesToString(toSchedule);
		} else
			response = "Error. Please enter a valid value. (expected integer)";
		return response;
	}

	private String addSizeRestriction(User user, String request) {
		String response = "";
		String[] args = request.split(" ");

		try {
			if (args.length > 3 && args[3] != null && args[2] != null) {
				EraseSettings erase = user.getSettings().getEraseSettings();
				Integer fromSize = null, toSize = null;

				try {
					if (!args[2].equals("N"))
						fromSize = Integer.valueOf(args[2]);
					if (!args[3].equals("N"))
						toSize = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					return "Please enter a valid size range.";
				}

				// If range is valid or one value is unbounded, continue
				if ((fromSize != null && toSize != null && fromSize < toSize)
						|| (fromSize == null && toSize != null)
						|| (toSize == null && fromSize != null)) {

					Range<Integer> range = new Range<Integer>();
					range.setFrom(fromSize);
					range.setTo(toSize);

					erase.addSizeRestriction(range);
					changed = true;
					response = "OK. Size restriction range added for user "
							+ user.getName() + " ";
				} else
					response = "Error. Range is invalid: " + fromSize + "-"
							+ toSize;
			} else
				response = "Error. Please enter a valid size range.";
		} catch (NumberFormatException e) {
			response = "-ERR. Please enter a valid size range.";
		}
		return response;
	}

	private String addDateRestriction(User user, String request) {
		String response = "";
		String[] args = request.split(" ");
		if (args.length > 3 && args[2] != null && args[3] != null) {

			EraseSettings erase = user.getSettings().getEraseSettings();

			DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy")
					.withLocale(new Locale("es"));

			DateTime fromDate = null, toDate = null;

			try {
				if (!args[2].equals("N"))
					fromDate = f.parseDateTime(args[2]);
				if (!args[3].equals("N"))
					toDate = f.parseDateTime(args[3]);
			} catch (Exception e) {
				return "Please enter a valid date";
			}

			// If range is valid or one value is unbounded, continue
			if ((fromDate != null && toDate != null && fromDate
					.isBefore(toDate))
					|| (fromDate == null && toDate != null)
					|| (toDate == null && fromDate != null)) {
				Range<DateTime> range = new Range<DateTime>();
				range.setFrom(fromDate);
				range.setTo(toDate);
				erase.addDateRestriction(range);
				changed = true;

				response = "OK. Date restriction added for user "
						+ user.getName() + " ";

				if (fromDate != null)
					response += "min: " + f.print(fromDate);
				else
					response += "min: unbounded ";
				if (toDate != null)
					response += "max: " + f.print(toDate);
				else
					response += "max: unbounded ";

			} else
				response = "Error. Range is invalid: "
						+ (fromDate != null ? f.print(fromDate) : "unbounded")
						+ "-"
						+ (toDate != null ? f.print(toDate) : "unbounded");
		} else
			response = "Error. Please enter a valid date.";
		return response;
	}

	private String printEraseSettings(EraseSettings e) {
		String out = "";

		DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy")
				.withLocale(new Locale("es"));

		if (!e.getDateRestrictions().isEmpty())
			out += "Date restrictions: \n";

		for (Range<DateTime> date : e.getDateRestrictions()) {
			out += " ";
			if (date.getFrom() != null)
				out += "min " + f.print(date.getFrom()) + ", ";
			else
				out += "unbounded, ";
			if (date.getTo() != null)
				out += "max " + f.print(date.getTo()) + " ";
			else
				out += "unbounded ";
			out += "\n";
		}

		if (!e.getSizeRestrictions().isEmpty())
			out += "Size restrictions: \n";

		for (Range<Integer> size : e.getSizeRestrictions()) {
			out += " ";
			if (size.getFrom() != null)
				out += "min " + size.getFrom() + "bytes, ";
			else
				out += "unbounded, ";
			if (size.getTo() != null)
				out += "max " + size.getTo() + "bytes ";
			else
				out += "unbounded ";
			out += "\n";
		}

		out += "Structure: " + e.getStructure() + "\n";
		out += "Senders: " + e.getSenders().toString() + "\n";
		out += "Content: " + e.getContentTypes().toString() + "\n";
		out += "Header pattern: " + e.getHeaderPattern().toString() + "\n";
		return out;
	}

	private String printSettings(UserSettings s) {
		String out = "";

		// if (s.getSchedule() != null && s.getSchedule().getFrom() != null
		// || s.getSchedule().getTo() != null)
		// out += "Schedule: ";
		// if (s.getSchedule() != null && s.getSchedule().getFrom() != null)
		// out += "min " + s.getSchedule().getFrom() / 60 + ":"
		// + s.getSchedule().getFrom() % 60 + "hs ";
		// if (s.getSchedule() != null && s.getSchedule().getTo() != null)
		// out += "max " + s.getSchedule().getTo() / 60 + ":"
		// + s.getSchedule().getTo() % 60 + "hs, ";

		out += "Max logins: " + s.getMaxLogins() + ", ";
		out += "Leet:" + s.isLeet() + ", Rotate:" + s.isRotate() + ", ";
		out += "Server: " + s.getServer();

		return out;
	}
}
