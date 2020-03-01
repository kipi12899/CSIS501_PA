package PAClient;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class ClientListener extends Thread {
	
	public int FT_PORT = 5001;
	public String FILE_NAME = "Client1.txt";
	public final int FILE_SIZE = 6022386; //5.743 MB
	
	public int clientPORT = 9001; // default port to be used for listener
	
	

	
	public ClientListener(String file) {
		if(file != null)
			this.FILE_NAME = file;
	}
	
	
	public void run() {
		
	    ServerSocket serverSocket=null;
		try {
			serverSocket = new ServerSocket(FT_PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	     while(true) 
	     {
		   int byteRead = 0;
	        try {
	        
	         
	          //  else
	           //     System.out.println("Server Socket bounded to Port : " + serverSocket.getLocalPort());
	
	            Socket clientSocket = serverSocket.accept();
	            
	            
	            
	            if (!clientSocket.isConnected())
	                System.out.println("Client Socket not Connected...");
	            else
	                System.out.println("Client Socket Connected : " + clientSocket.getInetAddress());
	
	       	            	
            	DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        		FileInputStream fis = new FileInputStream("c:\\CSIS501\\"+FILE_NAME);
        		byte[] buffer = new byte[4096];
        		
        		while (fis.read(buffer) > 0) {
        			dos.write(buffer);
        		}
        		
        		fis.close();
        		dos.close();	
	            	
	/*
	            	InputStream in = clientSocket.getInputStream();
	
	                OutputStream os = new FileOutputStream(FILE_NAME);
	                byte[] byteArray = new byte[FILE_SIZE];
	
	                while ((byteRead = in .read(byteArray, 0, byteArray.length)) != -1) {
	                    os.write(byteArray, 0, byteRead);
	                //    System.out.println("No. of Bytes Received : " + byteRead);
	                }
	                synchronized(os) {
	                    os.wait(100);
	                }
	                os.close();
	                serverSocket.close();
	                //System.out.println("File Received...");
	 */
	            
	
	        } catch (Exception e) {
	            System.out.println("Server Exception : " + e.getMessage());
	            e.printStackTrace();
	        }
	
		
	     }
	}

}
