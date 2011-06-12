package filter;
import proxy.Message;


public interface Filter {

	public void apply(Message message);
	
}
