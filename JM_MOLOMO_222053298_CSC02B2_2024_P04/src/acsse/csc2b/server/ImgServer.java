package acsse.csc2b.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/*
 * @author Jm Molomo
 * @version Practical 04
 */
public class ImgServer{
	
	
	private int Port = 5431;
	//Create the Client & Server Socket
	
	private ServerSocket sSocket; //Server Socket
	
	public ImgServer() {
		//Open Connection
		try {
			sSocket = new ServerSocket(Port);
			
			connectClient();	
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
public void connectClient() {
	
		//keep connection alive
	System.out.println("Starting...");
	while(true) {
		try {
			//accept connection
			Socket cClient =  sSocket.accept();
			 
				//create Thread
				ClientHandler cH = new ClientHandler(cClient);
				Thread SClient = new Thread(cH);
			     SClient.start();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}



public static void main(String[] args) {	
	ImgServer is = new ImgServer();	    

}

}
