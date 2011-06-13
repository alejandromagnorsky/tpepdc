package filter;
import model.Message;


public interface Filter {

	public void apply(Message message);
	
}
