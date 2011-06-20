package filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import model.Content;
import model.EraseSettings;
import model.Message;
import model.Range;
import model.User;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import proxy.POP3Client;

public class EraseRequestFilter extends RequestFilter {

	private static Logger logger = Logger.getLogger("logger");

	private class DummyWriter extends Writer {

		@Override
		public void close() throws IOException {
		}

		public void flush() throws IOException {
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
		}

	};

	@Override
	protected Response apply(Request r, PrintWriter responseWriter,
			POP3Client client, RequestFilter chain) {

		String request = r.getRequestString();
		User user = r.getUser();

		String trimmed = request.trim();

		if (user != null && user.getSettings() != null
				&& user.getSettings().getEraseSettings() != null
				&& trimmed.toUpperCase().contains("DELE")
				&& trimmed.length() > 5 && client.isConnected()) {
			EraseSettings erase = user.getSettings().getEraseSettings();

			// If there are no erase settings, simply do not get message
			if (erase.getContentTypes().isEmpty()
					&& erase.getDateRestrictions().isEmpty()
					&& erase.getSizeRestrictions().isEmpty()
					&& erase.getHeaderPattern().isEmpty()
					&& erase.getSenders().isEmpty()
					&& erase.getStructure().equals(""))
				return chain.doFilter(r, responseWriter, client);

			String msgStr = trimmed.substring(5);
			msgStr = msgStr.trim(); // Trim number
			int number = 0;
			try {
				number = Integer.valueOf(msgStr);
			} catch (NumberFormatException e) {
				return new Response(user, "-ERR invalid argument");
			}

			if (!canDeleteMail(user, number, client, responseWriter))
				return new Response(user, "");
		}
		return chain.doFilter(r, responseWriter, client);
	}

	private boolean canDeleteMail(User user, int number, POP3Client client,
			PrintWriter writer) {

		try {
			DummyWriter dummy = new DummyWriter();
			PrintWriter dummyWriter = new PrintWriter(dummy);

			String request = "LIST " + number;
			String listResponse;

			int size = -1;

			listResponse = client.send(request);
			String args[] = listResponse.split(" ");

			if (args.length > 2 && args[2] != null) {
				try {
					Integer octets = Integer.valueOf(args[2]);
					size = octets;
				} catch (NumberFormatException e) {
					logger.fatal("Error reading size of message.");
				}
			} else
				logger.fatal("Server does not support LIST arg command.");

			// Already know size, so check before getting message
			if (size >= 0 && sizeOutOfRange(user, size, writer))
				return false;

			request = "RETR " + number;
			String response = client.send(request);

			if (response.contains("+OK")) {

				Message message = client.getMessage(dummyWriter);
				Map<String, List<String>> headers = message.getHeaders();

				if (structureMatches(user, message, writer)) {
					return false;
				} else if (containsRestrictedSenders(user, headers.get("From"),
						writer)) {
					return false;
				} else if (dateOutOfRange(user, headers.get("Date"), writer)) {
					return false;
				} else if (containsRestrictedContent(user, message
						.getContents(), writer)) {
					return false;
				} else if (headerMatches(user, headers, writer)) {
					return false;
				}
			}
		} catch (IOException e) {
			logger.fatal("Error trying to delete message.");
			try {
				client.disconnect();
			} catch (IOException e1) {
				logger.fatal("Error disconnecting from server");
			}
		}

		return true;
	}

	private boolean structureMatches(User user, Message message,
			PrintWriter writer) {
		String structure = user.getSettings().getEraseSettings().getStructure();

		if (structure.equals("NOATTACH")) {
			SortedSet<Content> contentSet = message.getContents();
			for (Content c : contentSet)
				if (c.getType() != Content.Type.TEXT)
					return false;

			writer
					.println("-ERR Could not delete message due to restricted structure");
			writer
					.println("You are not allowed to delete messages with no attachments");
			return true;
		} else if (structure.equals("ATTACH")) {
			SortedSet<Content> contentSet = message.getContents();
			for (Content c : contentSet)
				if (c.getType() != Content.Type.TEXT) {
					writer
							.println("-ERR Could not delete message due to restricted structure");
					writer
							.println("You are not allowed to delete messages with attachments");
					return true;
				}
		}
		return false;
	}

	private boolean headerMatches(User user, Map<String, List<String>> headers,
			PrintWriter writer) {

		if (user != null && user.getSettings() != null
				&& user.getSettings().getEraseSettings() != null) {

			List<String> patternList = user.getSettings().getEraseSettings()
					.getHeaderPattern();

			for (String pattern : patternList) {
				int low = pattern.indexOf(":");
				if (low > 0) {
					String header = pattern.substring(0, low);
					String regex = pattern.substring(low + 1);

					String headerBody = "";
					if (headers.get(header) != null) {
						for (String str : headers.get(header))
							headerBody += str;

						if (headerBody.matches(regex)
								|| headerBody.contains(regex)) {
							writer
									.println("-ERR Could not delete message due to restricted headers");
							writer.println("Header " + header + ":"
									+ headerBody);
							writer.println("matches with pattern " + regex);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean containsRestrictedContent(User user,
			SortedSet<Content> contentSet, PrintWriter writer) {
		List<String> contentList = user.getSettings().getEraseSettings()
				.getContentTypes();

		for (Content c : contentSet) {
			String header = c.getContentTypeHeader();

			for (String restricted : contentList) {
				if (header.contains(restricted)) {
					writer
							.println("-ERR Could not delete message due to restricted content type");
					writer.println("Content: " + header + " matched with "
							+ restricted);
					return true;
				}
			}
		}
		return false;
	}

	private boolean rangeContainsDate(Range<DateTime> range, DateTime date) {

		// If unbounded range, return true (infinte range)
		if (range.getFrom() == null && range.getTo() == null)
			return true;

		// If valid, non null range, return true
		if (range.getFrom() != null && range.getTo() != null
				&& range.getFrom().isBefore(date)
				&& range.getTo().isAfter(date))
			return true;

		// If unbounded from left and valid, return true
		if (range.getFrom() == null && range.getTo().isAfter(date))
			return true;

		// If unbounded from right and valid, return true
		if (range.getTo() == null && range.getFrom().isBefore(date))
			return true;

		return false;
	}

	// [Mon, 30 May 2011 13:10:48 -0700 (PDT)]
	private boolean dateOutOfRange(User user, List<String> headers,
			PrintWriter writer) {

		if (headers == null)
			return false;

		String raw = headers.get(0);

		if (raw == null)
			return false;

		raw = raw.toLowerCase();
		int index = raw.indexOf("-") - 1;
		raw = raw.substring(0, index);

		DateTimeFormatter f = DateTimeFormat.forPattern(
				"EEEE, dd MMMM yyyy HH:mm:ss").withLocale(new Locale("en"));
		DateTime date = f.parseDateTime(raw);

		// If list is empty, user can delete
		boolean inRange = user.getSettings().getEraseSettings()
				.getDateRestrictions().isEmpty();

		// Do the union of all ranges, only return false if date is out of
		// EVERY range
		for (Range<DateTime> range : user.getSettings().getEraseSettings()
				.getDateRestrictions())
			inRange = inRange || rangeContainsDate(range, date);

		if (!inRange) {
			writer
					.println("-ERR Could not delete message due to restricted date range");
			writer.println("Message date: " + f.print(date));
			writer.println("Approved date ranges:");

			for (Range<DateTime> range : user.getSettings().getEraseSettings()
					.getDateRestrictions()) {
				String out = " ";
				if (range.getFrom() != null)
					out += "min " + f.print(range.getFrom()) + ", ";
				else
					out += "unbounded, ";
				if (range.getTo() != null)
					out += "max " + f.print(range.getTo()) + " ";
				else
					out += "unbounded ";
				writer.println(out);
			}
		}

		return !inRange;
	}

	private boolean rangeContainsSize(Range<Integer> range, Integer size) {

		// If unbounded range, return true (infinite range)
		if (range.getFrom() == null && range.getTo() == null)
			return true;

		// If valid, non null range, return true
		if (range.getFrom() != null && range.getTo() != null
				&& range.getFrom() <= size && range.getTo() >= size)
			return true;

		// If unbounded from left and valid, return true
		if (range.getFrom() == null && range.getTo() >= size)
			return true;

		// If unbounded from right and valid, return true
		if (range.getTo() == null && range.getFrom() <= size)
			return true;

		return false;
	}

	private boolean sizeOutOfRange(User user, int size, PrintWriter writer) {
		try {
			// If list is empty, user can delete
			boolean inRange = user.getSettings().getEraseSettings()
					.getSizeRestrictions().isEmpty();

			System.out.println("SIZE: " + size);

			// Do the union of all ranges, only return false if date is out of
			// EVERY range
			for (Range<Integer> range : user.getSettings().getEraseSettings()
					.getSizeRestrictions())
				inRange = inRange || rangeContainsSize(range, size);

			if (!inRange) {
				writer
						.println("-ERR Could not delete message due to restricted size range");
				writer.println("Message size: " + size + " bytes");
				writer.println("Approved size ranges:");

				String out = "";
				for (Range<Integer> range : user.getSettings()
						.getEraseSettings().getSizeRestrictions()) {
					out += " ";
					if (range.getFrom() != null)
						out += "min " + range.getFrom() + " bytes, ";
					else
						out += "unbounded, ";
					if (range.getTo() != null)
						out += "max " + range.getTo() + " bytes ";
					else
						out += "unbounded ";
					out += "\n";
				}

				writer.println(out);
			}

			return !inRange;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private boolean containsRestrictedSenders(User user, List<String> raw,
			PrintWriter writer) {
		EraseSettings e = user.getSettings().getEraseSettings();
		if (raw != null)
			for (String r : raw) {
				if (r != null) {

					int low = r.indexOf("<") + 1;
					int high = r.indexOf(">");

					if (low != -1 && high != -1) {
						String sender = r.substring(low, high);

						if (e.getSenders().contains(sender)) {
							writer
									.println("-ERR Could not delete message due to restricted sender");
							writer.println("Mails from: " + sender
									+ " cannot be deleted");
							return true;
						}
					}
				}
			}
		return false;
	}

}
