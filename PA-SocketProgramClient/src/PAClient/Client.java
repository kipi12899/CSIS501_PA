package PAClient;


public class Client {

	

	public static void main(String[] args) 
	{
		String file = null;
		if (args.length > 0)
			file = args[0];
		
		Thread listener = new ClientListener(file);
		Thread handler = new ClientHandler();		
		listener.start();
		handler.start();
	
	}
		
}
