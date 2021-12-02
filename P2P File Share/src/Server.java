import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


public class Server {

	public static final int sPort = 6032;   //The server will be listening on this port number
	private static int selfClientID = -1;
	public static volatile ServerSocket listener;

	public static void startServer(int selfClientID) throws Exception {
		System.out.println("The server is running.");
		Server.selfClientID = selfClientID;
		listener = new ServerSocket(sPort);

		long startTime = System.currentTimeMillis();

		try {
			while(PeerProcess.isRunning) {
				Client c = new Client(listener.accept(), false);
				c.start();
				System.out.println("Client connected!");
				if (System.currentTimeMillis() > startTime + 30000) {
					System.out.println("Stopping server on time.");
					break;
				}
			}
		} finally {
			listener.close();
			System.out.println("The server stopped.");
		} 
	}
}