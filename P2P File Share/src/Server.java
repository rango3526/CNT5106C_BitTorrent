import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number
	private static int selfClientID = -1;

	public static void startServer(int selfClientID) throws Exception {
		System.out.println("The server is running.");
		Server.selfClientID = selfClientID;
		ServerSocket listener = new ServerSocket(sPort);
		try {
			while(true) {
				Client c = new Client(listener.accept(), false);
				c.start();
				System.out.println("Client connected!");
			}
		} finally {
			listener.close();
			System.out.println("The server stopped.");
		} 
	}
}