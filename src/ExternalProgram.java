import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class ExternalProgram implements Filter{

	private String path; 
	
	public ExternalProgram(String path){
		this.path = path;
	}
	
	public void apply(Message message) {
		try {
			Process process = Runtime.getRuntime().exec(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream()),true);
			writer.println(message.getBody()+"\n.");
			
			String line;
			StringBuilder ans = new StringBuilder();
			while((line = reader.readLine()) != null)
				ans.append(line+"\n");
			
			message.setBody(ans.toString());
			
			int code = process.waitFor();
			if(code != 0)
				throw new RuntimeException("Error ejecutando la aplicacion "+path);
			
			reader.close();
			writer.close();
			process.destroy();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
