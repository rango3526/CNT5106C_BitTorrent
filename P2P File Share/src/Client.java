import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/*

This entire file is just for reference; we're not using it

*/
public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
	ObjectInputStream in;          //stream read from the socket
	byte[] messageToSend;                //message send to the server
	byte[] messageFromServer;                //capitalized message read from the server

	int clientID = -1; 		// This own client's ID
	int peerID = -1; 		// ID we're trying to connect to
	int port = -1;
	String ip = "";

	public Client(int _clientID, int _peerID) {
		peerID = _peerID;
		clientID = _clientID;

		peerID = getPortFromPeerID(peerID);
		ip = getIPFromPeerID(peerID);

		this.run();
	}

	void run() {
		try {
			requestSocket = new Socket(ip, port);

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			while(true)
			{
				messageToSend = Handshake.getHandshakeMessage(clientID, peerID);
				sendMessage(messageToSend);
				messageFromServer = receiveMessage();
			}
		}
		catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
			System.err.println("Class not found");
		} 
		catch(UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
		}
		finally {
			//Close connections
			try {
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	//send a message to the output stream
	void sendMessage(byte[] msg) {
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	byte[] receiveMessage() throws IOException, ClassNotFoundException {
		return (byte[])in.readObject();
	}

	int getPortFromPeerID(int id) { // use the config file
        return ConfigReader.getPortFromPeerID(id);
    }

    String getIPFromPeerID(int id) { // use the config file
        return ConfigReader.getIPFromPeerID(id);
    }

	
}