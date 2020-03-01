package PAServer;

import java.util.Scanner;

public class Server {

	public static Scanner input = new Scanner(System.in); // Used for taking users' input
	
	public static void main(String[] args) 
	{
		
			
			Peer p = new Peer();
			p.start();
			
			
			String choice = "";	
	    	while (true)  
	    	{
		      	Menu();
		    	choice = GetInput();

		    	switch(choice) 
		    	{
		    		case "1":
		    			System.out.print ( "Enter Index (number): ");
		    			String index = input.nextLine();
		    			
		    			System.out.print ( "Enter Successor Index (number): ");
		    			String nextIndex = input.nextLine();
		    			
		    			System.out.print ( "Enter Succcessor IP: ");
		    			String nextIP = input.nextLine();
		    			
		    			System.out.print ( "Enter Predecessor Index (number): ");
		    			String prevIndex = input.nextLine();
		    			
		    			System.out.print ( "Enter Predecessor IP: ");
		    			String prevIP = input.nextLine();
		    			
		    			p.Setup( index, nextIndex, nextIP, prevIndex, prevIP);
		    			break;
		    		case "2":
		    			if(p.GetPeerIndex() > 0)
		    			{
		    				System.out.print ( "Enter successor IP: ");
		    				p.FindSuccessors(input.nextLine());
		    			}
		    			else 
		    			{
		    				System.out.println ( "Identity is not set ");
		    			}
		    			break;
		    		case "Q": 
		    			break;
		    		default:
		    			System.out.println(choice +" is not an option");
		    	}
	    	}

		
	}
	
	
	
	
	public static void Menu()
	{
	        System.out.println ( " ----------------------------------");
			System.out.println ( " (1) Initial Setup");
			System.out.println ( " (2) Connect to successor");
		//	System.out.println ( " (3) ");
			System.out.println ( " ----------------------------------");
	}
	
	  
	  
    public static  String GetInput()	  
  	{
		System.out.print ("Please enter your selection  >>  ");
		String choice = input.nextLine();
	
		
		return choice;
    }
}
