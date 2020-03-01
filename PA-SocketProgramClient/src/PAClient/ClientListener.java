package PAClient;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class ClientListener extends Thread {
	

	public String FILE_NAME = "Clinet1.txt";
	public final int FILE_SIZE = 6022386; //5.743 MB
	private ServerSocket serverSocket;
	public int clientPORT = 9001; // default port to be used for listener
	
	

	
	public ClientListener(String file) {
		if(file != null)
			this.FILE_NAME = file;
	}
	
	
	public void run() {
		
	//     System.out.println("**********Client Listening**************");
	     while(true) 
	     {
		   int byteRead = 0;
	        try {
	            ServerSocket serverSocket = new ServerSocket(9999);
	            if (!serverSocket.isBound())
	                System.out.println("Sever Socket not Bounded...");
	          //  else
	           //     System.out.println("Server Socket bounded to Port : " + serverSocket.getLocalPort());
	
	            Socket clientSocket = serverSocket.accept();
	            if (!clientSocket.isConnected())
	                System.out.println("Client Socket not Connected...");
	            else
	                System.out.println("Client Socket Connected : " + clientSocket.getInetAddress());
	
	            while (true) {
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
	            }
	
	        } catch (Exception e) {
	            System.out.println("Server Exception : " + e.getMessage());
	            e.printStackTrace();
	        }
		
	     }
	}

}
