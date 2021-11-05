import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/*

This entire file is just for reference; we're not using it

*/

public class ServerOLD {

	private static final int sPort = 8000;   //The server will be listening on this port number
	public static int selfClientID = -1;

	public static void startServer(int _selfClientID) {
		// Might need a thread on the outside here

		try {
			selfClientID = _selfClientID;
	
			System.out.println("The server is running."); 
			ServerSocket listener = new ServerSocket(sPort);
			int clientNum = 1;
			try {
				while(true) {
					new Handler(listener.accept(),clientNum, selfClientID).start();
					System.out.println("Client "  + clientNum + " is connected!");
					clientNum++;
				}
			} finally {
				listener.close();
			} 
		}
		catch (Exception e) {
			System.out.println("Server crashed: ");
			e.printStackTrace();
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
		private int selfClientID = -1;
		private int peerID = -1;

		public Handler(Socket connection, int no, int selfClientID) {
			this.connection = connection;
			this.no = no;
			this.selfClientID = selfClientID;
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

						messageFromClient = receiveMessage();
						handleBitfieldMessage(messageFromClient);
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

		// TODO: David
		public void handleHandshake(byte[] handshake) {
			// involves sending the handshake reply to the client
			// also set the peerID variable (currently declared on line 55)
			ByteBuffer bytearray = ByteBuffer.wrap(handshake);
			byte[] headerbytes = new byte[18];
        	byte[] zerobitsbytes = new byte[10];
        	byte[] peerIdbytes = new byte[4];
        	bytearray.get(headerbytes, 0, headerbytes.length);
        	bytearray.get(zerobitsbytes, 0, zerobitsbytes.length);
        	bytearray.get(peerIdbytes, 0, peerIdbytes.length);
			String headerString = new String(headerbytes);
			String zerobitString = new String(zerobitsbytes);
			String peerIdString = new String(peerIdbytes);
			int peerIdInt = Integer.parseInt(peerIdString);
			byte [] handshakeMsg = Handshake.getHandshakeMessage(peerIdInt);
			sendMessage(handshakeMsg);
		}

		public void handleBitfieldMessage(byte[] bitfieldMessage) {
			Bitfield.setPeerBitfield(peerID, Bitfield.byteArrayToBitfield(ActualMessageHandler.extractPayload(bitfieldMessage)));

			// TODO: Ranger, find out what to do after receiving bitfield
		}

	}

}