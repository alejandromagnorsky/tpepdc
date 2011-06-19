package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import proxy.POP3Proxy;

public class ExternalProgram {
	
	private String path;
	private BufferedReader input;
	
	public ExternalProgram(String path, BufferedReader input){
		this.path = path;
		this.input = input;
	}

	public BufferedReader execute() {
		try {
			String response;
			Process process = Runtime.getRuntime().exec(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(process
					.getOutputStream()), true);
			
			// Send the message to the external program's input
			while(!(response = readResponseLine()).equals("."))
				writer.println(response);
			writer.close();
			
			
			// Put the responde from the external program's output in a tmp file
			File tmp = File.createTempFile("msg", ".tmp");
			writer = new PrintWriter(new FileWriter(tmp));
			while ((response = reader.readLine()) != null)
				writer.println(response);
			writer.append(".");			
			writer.close();
			reader.close();	
			
			int code = process.waitFor();
			if (code != 0)
				POP3Proxy.logger.fatal("Error executing the external program " + path);					
			process.destroy();
			
			return new BufferedReader(new FileReader(tmp));
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error executing the external program " + path);
			return input;
		}
	}
	

	private String readResponseLine() throws IOException {
		String response = input.readLine();
		POP3Proxy.logger.info("[in]: " + response);
		return response;
	}
}
