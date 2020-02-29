package PAClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientHandler extends Thread {
	
	public Scanner input = new Scanner(System.in); // Used for taking users' input
	public int PORT = 9000; // default port to be used 
	public int FT_PORT = 5001;
	public String clientIP = null;
	public int clientPORT = 9001; // default port to be used for listener
	public final int FILE_SIZE = 6022386; //5.743 MB
	public String server = null;
	public String file = null;
	
	public enum Action 
	{ 
	    INSERT, 
	    SEARCH, 
	    DOWNLOAD; 
	     
	} 
	
	
	public ClientHandler()
	{
		try {
			 InetAddress InetIPAddress = InetAddress.getLocalHost();
			 clientIP = InetIPAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void run() {
	
				
		String choice = "";	
    	while (true)  
    	{
	      	Menu();
	    	choice = GetInput();

	    	switch(choice) 
	    	{
	    		case "1":
	    			GetDetail();
	    			Insert();
	    			break;
	    		case "2":
	    			GetDetail();
	    			Search();
	    			break;
	    		case "3":
	    			GetDetail();
	    			Download();
	    			break;
	    		default:
	    			System.out.println(choice +" is not an option");
	    	}
    	}
		
	}
	
	public void GetDetail()
	{
    	System.out.print ( "Enter server IP: ");
    	this.server = input.nextLine();
    	System.out.print ( "Enter file name to search: ");
    	this.file = input.nextLine();
	}
	
	
	
	public void Menu()
	{
	        System.out.println ( " ----------------------------------");
			System.out.println ( " (1) Insert File Name");
			System.out.println ( " (2) Search For File");
			System.out.println ( " (3) Download File");
			System.out.println ( " ----------------------------------");
	}
	
	  
	  
    public  String GetInput()	  
  	{
		System.out.print ("Please enter your selection  >>  ");
		String choice = input.nextLine();
	
		
		return choice;
    }
    

    public void Insert()
    {    	
    	String response = null;
    	String sendMsg = Action.INSERT.toString() + "," + file + "," + clientIP;    
    	response = SendMessage(server, sendMsg);
    	System.out.println (response);
    	
    }
    
    public void Search()
    {
    	String response = null;
    	String sendMsg = Action.SEARCH.toString() + "," + file + "," + clientIP;    
    	response = SendMessage(server, sendMsg);
	
		if(response.length() > 0) 
    		System.out.println ("File located at "+ response); 
		else
			System.out.println ("File not found");
    	
    }
    
    public void Download()
    {
    	String ip = null;
    	String sendMsg = Action.SEARCH.toString() + "," + file + "," + clientIP;    
    	ip = SendMessage(server, sendMsg);
    	
    	if(ip.length() > 0)
    	{
    		try {
				DownloadFile(file, ip);
				System.out.println("File download complete");
			} catch (IOException e) {			
				e.printStackTrace();
			}
    	}
    	else
    		System.out.println("File not found");
    	   
    	
    
    }
    
    public void DownloadFile(String fileName, String clientIP) throws UnknownHostException, IOException
    {
    
	 	int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    Socket socket = null;
	    try {
	    	socket = new Socket(clientIP, FT_PORT);
	      System.out.println("Connecting...");

	      // receive file
	      byte [] byteArray  = new byte [FILE_SIZE];
	      InputStream is = socket.getInputStream();
	      fos = new FileOutputStream(fileName);
	      bos = new BufferedOutputStream(fos);
	      bytesRead = is.read(byteArray,0,byteArray.length);
	      current = bytesRead;

	      do {
	         bytesRead = is.read(byteArray, current, (byteArray.length-current));
	         if(bytesRead >= 0) 
	        	 current += bytesRead;
	      } while(bytesRead > -1);

	      bos.write(byteArray, 0 , current);
	      bos.flush();
	      System.out.println("File " + fileName  + " downloaded (" + current + " bytes read)");
	    }
	    finally {
	      if (fos != null) fos.close();
	      if (bos != null) bos.close();
	      if (socket != null) socket.close();
	    }
    	  
    }
    
    
    public String SendMessage(String serverIP, String message)
    {
    	String response = "";
    	 try (Socket socket = new Socket(serverIP, PORT)) {
    		 
 	        OutputStream output = socket.getOutputStream();
 	        PrintWriter writer = new PrintWriter(output, true);
 	        
 	
 	        InputStream input = socket.getInputStream();
 	
 	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 	
 	        String line = null;
 	
 	        while ((line = reader.readLine()) != null) {
 	            response += line;
 	        }
 	    } catch (UnknownHostException ex) {
 	
 	        System.out.println("Server not found: " + ex.getMessage());
 	
 	    } catch (IOException ex) {
 	
 	        System.out.println("I/O error: " + ex.getMessage());
 	    }
 	    
    	return response;
    }

	

}
