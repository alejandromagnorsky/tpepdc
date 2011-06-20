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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import proxy.POP3Client;

public class EraseRequestFilter extends RequestFilter {

	private class DummyWriter extends Writer {

		int size = 0;

		public int getSize() {
			return size;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			size += len;
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
		String request = "RETR " + number;

		try {
			String response = client.send(request);

			if (response.contains("+OK")) {
				DummyWriter dummy = new DummyWriter();
				PrintWriter dummyWriter = new PrintWriter(dummy);

				Message message = client.getMessage(dummyWriter);
				int size = dummy.getSize();

				Map<String, List<String>> headers = message.getHeaders();

				if (structureMatches(user, message)) {
					writer.println("-ERR. You are not allowed to delete this message due to message structure restrictions.");
					return false;
				} else if (containsRestrictedSenders(user, headers.get("From"))) {
					writer.println("-ERR. You are not allowed to delete this message due to restricted senders.");
					return false;
				} else if (dateOutOfRange(user, headers.get("Date").get(0))) {
					writer.println("-ERR. You are not allowed to delete this message due to date restrictions.");
					return false;
				} else if (sizeOutOfRange(user, size)) {
					writer.println("-ERR. You are not allowed to delete this message due to size restrictions.");
					return false;
				} else if (containsRestrictedContent(user,
						message.getContents())) {
					writer.println("-ERR. You are not allowed to delete this message due to content restrictions.");
					return false;
				} else if (headerMatches(user, headers)) {
					writer.println("-ERR. You are not allowed to delete this message due to header pattern restrictions.");
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean structureMatches(User user, Message message) {
		String structure = user.getSettings().getEraseSettings().getStructure();

		if (structure.equals("NOATTACH")) {
			SortedSet<Content> contentSet = message.getContents();
			for (Content c : contentSet)
				if (c.getType() != Content.Type.TEXT)
					return false;
			return true;
		} else if (structure.equals("ATTACH")) {
			SortedSet<Content> contentSet = message.getContents();
			for (Content c : contentSet)
				if (c.getType() != Content.Type.TEXT)
					return true;
		} else if (structure.contains("SENDERCOUNT_G")) {
			String tmp = structure.substring(structure.indexOf(" ") + 1);
			Integer count = Integer.valueOf(tmp);
			if (count != null) {
				List<String> senders = message.getHeaders().get("From");
				if (senders.size() > count)
					return true;
			}
		}
		return false;
	}

	private boolean headerMatches(User user, Map<String, List<String>> headers) {
		List<String> patternList = user.getSettings().getEraseSettings()
				.getHeaderPattern();

		for (String pattern : patternList) {
			int low = pattern.indexOf(":");
			if (low > 0) {
				String header = pattern.substring(0, low);
				String regex = pattern.substring(low + 1);

				String headerBody = "";
				for (String str : headers.get(header))
					headerBody += str;

				if (headerBody.matches(regex) || headerBody.contains(regex))
					return true;
			}
		}
		return false;
	}

	private boolean containsRestrictedContent(User user,
			SortedSet<Content> contentSet) {
		List<String> contentList = user.getSettings().getEraseSettings()
				.getContentTypes();

		for (Content c : contentSet) {
			String header = c.getContentTypeHeader();
			if (contentList.contains(header))
				return true;
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
	private boolean dateOutOfRange(User user, String raw) {
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

	private boolean sizeOutOfRange(User user, int size) {
		try {
			// If list is empty, user can delete
			boolean inRange = user.getSettings().getEraseSettings()
					.getSizeRestrictions().isEmpty();

			// Do the union of all ranges, only return false if date is out of
			// EVERY range
			for (Range<Integer> range : user.getSettings().getEraseSettings()
					.getSizeRestrictions())
				inRange = inRange || rangeContainsSize(range, size);

			return !inRange;
		} catch (NumberFormatException e) {
		}

		return false;
	}

	private boolean containsRestrictedSenders(User user, List<String> raw) {
		EraseSettings e = user.getSettings().getEraseSettings();
		for (String r : raw) {
			int low = r.indexOf("<") + 1;
			int high = r.indexOf(">");
			String sender = r.substring(low, high);
			if (e.getSenders().contains(sender))
				return false;
		}
		return false;
	}

}
