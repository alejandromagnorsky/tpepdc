package filter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import proxy.POP3Proxy;

import model.Message;
import model.User;

public class ExternalProgramFilter extends ResponseFilter {

	private String path;

	public ExternalProgramFilter(String path) {
		this.path = path;
	}

	public void apply(Message message, User user, ResponseFilter chain) {
		try {
			Process process = Runtime.getRuntime().exec(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(process
					.getOutputStream()), true);
			writer.println(message.getBody() + "\n.");

			String line;
			StringBuilder ans = new StringBuilder();
			while ((line = reader.readLine()) != null)
				ans.append(line + "\n");

			message.setBody(ans.toString());

			int code = process.waitFor();
			if (code != 0)
				POP3Proxy.logger.fatal("Error executing the external program "
						+ path);

			reader.close();
			writer.close();
			process.destroy();

		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error executing the external program "
					+ path);
		}

	}
}
