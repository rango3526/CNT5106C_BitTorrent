import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/*

This entire file is just for reference; we're not using it

*/

public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running."); 
		ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
		try {
			while(true) {
				new Handler(listener.accept(),clientNum).start();
				System.out.println("Client "  + clientNum + " is connected!");
				clientNum++;
			}
		} finally {
			listener.close();
		} 
	}

	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for dealing with a single client's requests.
	 */
	private static class Handler extends Thread {
		private byte[] messageFromClient;    //message received from the client
		private byte[] messageToSend;    //uppercase message send to the client
		private Socket connection;
		private ObjectInputStream in;	//stream read from the socket
		private ObjectOutputStream out;    //stream write to the socket
		private int no;		//The index number of the client

		public Handler(Socket connection, int no) {
			this.connection = connection;
			this.no = no;
		}

		public void run() {
			try{
				//initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				try{
					while(true)
					{
						messageFromClient = receiveMessage();
						handleHandshake(messageFromClient);
					}
				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Client " + no);
			}
			finally{
				//Close connections
				try{
					in.close();
					out.close();
					connection.close();
				}
				catch(IOException ioException){
					System.out.println("Disconnect with Client " + no);
				}
			}
		}

		//send a message to the output stream
		public void sendMessage(byte[] msg)
		{
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + no);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

		public byte[] receiveMessage() throws IOException, ClassNotFoundException {
			return (byte[])in.readObject();
		}

		public void handleHandshake(byte[] handshake) {
			// involves sending the handshake reply
			throw new UnsupportedOperationException();
		}

	}

}