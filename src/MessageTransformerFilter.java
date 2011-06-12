
public class MessageTransformerFilter implements Filter {

	public void apply(Message message) {
		
		//TODO falta filtrar por tipo de body, depende de como dejemos el message
		l33t(message.getBody());
	}
	
	public String l33t(String message) {
		char[] msg = message.toCharArray();
		for(int i = 0; i < message.length(); i++) {
			switch(msg[i]) {
			case 'a': 
				msg[i] = '4';
				break;
			case 'e':
				msg[i] = '3';
				break;
			case 'i':
				msg[i] = '1';
				break;
			case 'o':
				msg[i] = '0';
				break;
			}
		}
		return String.valueOf(msg);
	}
}
