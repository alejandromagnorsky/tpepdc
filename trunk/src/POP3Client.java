import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POP3Client extends Client {

	private static final int PORT = 995;

	public void connect(String host) throws IOException {
		connect(host, PORT);
		// To automatically read the welcome message
		readResponseLine();
	}

	protected String readResponseLine() throws IOException {
		String response = reader.readLine();		
		if (debug) 
			System.out.println("DEBUG [in] : " + response);
		return response;
	}

	protected String send(String command) throws IOException {
		if (debug) 
			System.out.println("DEBUG [out]: " + command);		
		writer.println(command);
		return readResponseLine();
	}

	public void login(String username, String password) throws IOException {
		send("USER " + username);
		send("PASS " + password);
	}

	public void logout() throws IOException {
		send("QUIT");
	}

	public int getQuantOfNewMessages() throws IOException {
		String response = send("STAT");
		return Integer.parseInt(response.split(" ")[1]);
	}

	protected Message getMessage(int i) throws IOException {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String headerName, headerValue, response;

		// Process headers
		while ((response = readResponseLine()).length() != 0) {
			if (response.startsWith("\t"))
				continue;
			
			int separator = response.indexOf(":");
			if(separator == -1)
				continue;
			
			headerName = response.substring(0, separator);
			if (response.length() > separator)
				headerValue = response.substring(separator + 2);
			else
				headerValue = "";

			List<String> headerValues = headers.get(headerName);
			if (headerValues == null) {
				headerValues = new ArrayList<String>();
				headers.put(headerName, headerValues);
			}
			headerValues.add(headerValue);
		}

		// Between the header and the body there is a \n
		// Process body
		StringBuilder bodyBuilder = new StringBuilder();
		while (!(response = readResponseLine()).equals("."))
			bodyBuilder.append(response + "\n");
		return new Message(headers, bodyBuilder.toString());
	}

	public List<Message> getMessages() throws IOException {
		int quant = getQuantOfNewMessages();
		List<Message> messages = new ArrayList<Message>();
		for (int i = 1; i <= quant; i++)
			messages.add(getMessage(i));
		
		return messages;
	}
}
