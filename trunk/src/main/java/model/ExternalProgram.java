package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;

import proxy.POP3Proxy;

public class ExternalProgram {
	
	private String path;
	private BufferedReader input;
	
	private class EnhancedBufferedReader extends BufferedReader {

		public EnhancedBufferedReader(Reader in) {
			super(in);
			// TODO Auto-generated constructor stub
		}
		
		public String readLine() throws IOException {
			String response = super.readLine();
			if(response == null)
				return ".";
			return response;
		}
		
	}
	
	public ExternalProgram(String path, BufferedReader input){
		this.path = path;
		this.input = input;
	}

	public Process execute() {
		try {
			String response;
			Process process = Runtime.getRuntime().exec(path);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(process
					.getOutputStream()), true);
			
			// Send the message to the external program's input
			while(!(response = input.readLine()).equals("."))
				writer.println(response);
			writer.close();
			
			return process;
		} catch (Exception e) {
			POP3Proxy.logger.fatal("Error executing the external program " + path);
			return null;
		}
	}
	
	public BufferedReader getReader(Process process){
		return  new EnhancedBufferedReader(new InputStreamReader(process.getInputStream()));
	}
}
