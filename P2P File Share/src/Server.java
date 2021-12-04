import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


public class Server extends Thread {

	public static final int sPort = 6039;   //The server will be listening on this port number
	public static volatile int selfClientID = -1;
	public static volatile ServerSocket listener;

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		System.out.println(Logger.getTimestamp() + ": " + startTime + ": The server is running.");
		try {
			listener = new ServerSocket(sPort);
		} catch (IOException e) {
			e.printStackTrace();
		}


		try {
			while(PeerProcess.isRunning) {
				System.out.println(Logger.getTimestamp() + ": Waiting to accept peer connections...");
				Client c;
				try {
					c = new Client(listener.accept(), false);
					System.out.println(Logger.getTimestamp() + ": Peer connected! Starting handshake.");
					c.start();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}

				// try {
				// 	Thread.sleep(3000);
				// } catch (InterruptedException e) {
				// 	e.printStackTrace();
				// 	break;
				// }

				// PeerProcess.connectionFromNewPeer(c.otherPeerID, c);
				if (System.currentTimeMillis() > startTime + 120000) {
					System.out.println(Logger.getTimestamp() + ": " + System.currentTimeMillis() + ": Stopping server on time.");
					break;
				}
			}
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(Logger.getTimestamp() + ": The server stopped.");
		} 
	}
}