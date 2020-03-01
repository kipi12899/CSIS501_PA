package PAServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class Peer extends Thread {

	private String Peer;
	private String nextPeer;
	private String nextPeer2;
	private String prevPeer;
	private int Index = 0;
	private int nextIndex;
	private int nextIndex2;
	private int prevIndex;
	private ServerSocket serverSocket;
	private final int PORT = 9000;
	Map<String,String> data = null;
	private final int MOD = 16;
	
	public Peer(String index, String nextIndex, String nextIP, String prevIndex, String prevIP)
	{
		this.Index = Integer.parseInt(index);
		this.nextIndex = Integer.parseInt(nextIndex);
		this.nextPeer = nextIP;
		this.prevIndex = Integer.parseInt(prevIndex);
		this.prevPeer = prevIP;
		
		Initilize();
	}
	
	
	public Peer()
	{
		
		Initilize();

			
		
	}
	
	private void Initilize()
	{
		data = new HashMap<String,String>(); 
		
		// Setup
		try {
			serverSocket = new ServerSocket(PORT);
			InetAddress InetIPAddress = InetAddress.getLocalHost();
			 this.Peer = InetIPAddress.getHostAddress();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Timer().schedule( 
		        new TimerTask() {
		            @Override
		            public void run() {
		                SendPing();
		                System.out.println("Ping "+nextPeer);
		            }
		        }, 
		        30000, 30000  //every 5 seconds
		);
		
	
	}
	
	
	public void run() 
	{
		
		while(true) {
	        try {
	           
	           System.out.println("Waiting for client on port " + 
	           serverSocket.getLocalPort() + "...");
	           Socket server = serverSocket.accept();
	           
	           System.out.println("Connected to " + server.getRemoteSocketAddress());
	           BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

//	           DataInputStream in = new DataInputStream(server.getInputStream());
	           String input = in.readLine();
	     //   		   System.out.println("Received: "+input);
	           String reply = ProcessRequest(input);
	   //        System.out.println("reply: "+reply);
	          //Send reply from server
	          // DataOutputStream out = new DataOutputStream(server.getOutputStream());	          
	           
	           PrintWriter out = new PrintWriter(server.getOutputStream(), true);
	           out.println(reply);
	           server.close();
	           
	        } catch (SocketTimeoutException s) {
	           System.out.println("Socket timed out!");
	           break;
	        } catch (IOException e) {
	           e.printStackTrace();
	           break;
	        }
		}	
		
	}
	

	
	public void FindSuccessors(String ip)
	{
		String SuccessorPredecessor=null;
		String sendMsg = "IsSuccessor," + this.Index + "," + this.Peer;  
		
		SuccessorPredecessor = SendMessage(ip,sendMsg);
		
		String[] result = new String[5];
		result =	SuccessorPredecessor.split(",");
		this.nextIndex = Integer.parseInt(result[1]);
		this.nextPeer = result[2];
		this.prevIndex = Integer.parseInt(result[3]);
		this.prevPeer = result[4];
		
		GetNextSuccessor();
		
	}
	
	
	public String CheckIfSuccessor(String iStr, String ip)
	{
		
		int i = Integer.parseInt(iStr);
		if(i < this.nextIndex && i > this.Index)
		{
			String sendMsg = "SetSuccessor,i," + ip;  
			SendMessage(this.prevPeer,sendMsg);
			return "SuccessorsInfo," + this.Index + "," + this.Peer + "," + this.prevIndex + "," + this.prevPeer; 
		}
		else
		{
	    	String sendMsg = "IsSuccessor,i," + ip;  
			return SendMessage(nextPeer,sendMsg);
		}
	}
	
	public void SetIdentity(int index)
	{
		this.Index = index;
	}
	
	private String ProcessRequest(String request)
	{
		String result = null;
		String[] req = new String[2];
		
		try {	
			if(request != null)
			{
				req = request.split(",");
				if(req.length > 0)
				{
					String type = req[0];
					switch(type) {
						case "INSERT":
							result = InsertKey(req[1],req[2]);
							break;
						case "SEARCH":
							result = SearchKey(req[1]);
							break;
						case "SUCCESSOR":
							result = GetSuccessor();
							break;
						case "PING":
							result = Ping(req[2]);
							break;
						case "IsSuccessor":
							result = CheckIfSuccessor(req[1],req[2]);
							break;
						case "SetSuccessor":
							SetSuccessor(req[1],req[2]);
							break;
					}
				}
			}
			
		}catch(Exception ex) {
			System.out.println("I/O error: " + ex.getMessage());
			}
		
		return result;
	
	}
	
	
	private String InsertKey(String key, String value)
	{
		int index = key.length()%MOD;
		if(index < this.nextIndex && index >=this.Index)
		{
			data.put(key,value);
			return "Insert executed Successfully";
		}
		else
		{
	    	String sendMsg = "INSERT," + key + "," + value;  
			return SendMessage(nextPeer,sendMsg);
		}
		
		
	}
	
	private String SearchKey(String key)
	{
		String response=null;
		int index = key.length()%MOD;
		if(index < this.nextIndex && index >=this.Index)
		{
			response = data.get(key);
			if(response != null)
				return response;
			else
				return key + " not found";
		}
		else
		{
	    	String sendMsg = "SEARCH," + key + ",PLACEHOLDER";  
			return SendMessage(nextPeer,sendMsg);
		}
		
	}
	
	public int GetPeerIndex()
	{
		return this.Index;
	}
	
	private void SetSuccessor(String i, String ip)
	{
		this.nextIndex = Integer.parseInt(i);
		this.nextPeer = ip;
	}
	
	public void Setup(String index, String nextIndex, String nextIP, String prevIndex, String prevIP)
	{
		this.Index = Integer.parseInt(index);
		this.nextIndex = Integer.parseInt(nextIndex);
		this.nextPeer = nextIP;
		this.nextIndex = Integer.parseInt(prevIndex);
		this.nextPeer = prevIP;
	}
	
	
	private String GetSuccessor()
	{
		return "SUCCESSOR," + this.nextIndex + "," + this.nextPeer;
	}
	
	private String Ping(String IP)
	{
		// save requester as predecessor 
		this.prevPeer = IP;
		//return message
		return "PONG, Pong, Pong";
	}
	
	private void SendPing()
	{
	
		if(this.nextPeer != null)
		{
			String sendMsg = "PING, Ping, "+this.Peer;  
			if(SendMessage(nextPeer,sendMsg) == null)
			{
				System.out.println(nextPeer + " failed to reply, replcaing with its successor "+ nextPeer2);
				this.nextIndex = nextIndex2;
				this.nextPeer = nextPeer2;
				GetNextSuccessor();	
			}
			else
				System.out.println("Pong reply from " + nextPeer);
			}
	}
	
	private void GetNextSuccessor()
	{
		
		String sendMsg = "Successor,,";
		String response = SendMessage(this.nextPeer,sendMsg);
		
		if(response != null)
		{
			String[] result = new String[2];
			result = response.split(",");
			this.nextIndex2 = Integer.parseInt(result[1]);
			this.nextPeer2 = result[2];
		}
		
	}
	
	private String JoinRequest()
	{
		
		
		
		return "";
	}
	
	private String SendMessage(String IP, String message)
    {
    	String response="";
    	 try (Socket socket = new Socket(IP, PORT)) {
    		 
 	        OutputStream output = socket.getOutputStream();
 	        PrintWriter writer = new PrintWriter(output, true);
 	        
 	        writer.println(message);
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