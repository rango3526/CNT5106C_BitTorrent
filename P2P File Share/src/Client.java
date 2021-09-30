import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
	// Socket requestSocket;           //socket connect to the server
	// ObjectOutputStream out;         //stream write to the socket
	// ObjectInputStream in;          //stream read from the socket
	// String message;                //message send to the server
	// String MESSAGE;                //capitalized message read from the server

    List<ObjectOutputStream> peerOuts;
    List<ObjectInputStream> peerIns;

	public void SampleClient() {}

    /*
        PeerID is are the ones listed in the config file (1001, 1002, 1003, etc)
    */

    int getPortFromPeerID(int id) { // use the config file
        throw new UnsupportedOperationException();
    }

    String getIPFromPeerID(int id) { // use the config file
        throw new UnsupportedOperationException();
    }

    int peerIDToIndex(int id) { // Index in the peerOuts and peerIns
        throw new UnsupportedOperationException();
    }

    List<Integer> getAllPeerIDs() {
        throw new UnsupportedOperationException();
    }

    void connectToPeer(int id) {
        String ip = getIPFromPeerID(id);
        int port = getPortFromPeerID(id);

        Socket requestSocket = null;           //socket connect to the server
        ObjectOutputStream out = null;         //stream write to the socket
        ObjectInputStream in = null;          //stream read from the socket

        try {
            requestSocket = new Socket(ip, port);
			System.out.println("Connected to " + ip + " in port " + port);
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				// Send messages to this peer here

                // Send a handshake

                // Look at the peer's bitfield

                // Decide if we want their pieces

                // And so on

                throw new UnsupportedOperationException();
			}
        }
        catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		// catch (ClassNotFoundException e ) {
		// 	System.err.println("Class not found");
		// } 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
    }

    void connectToAllPeers() {
        for (int id : getAllPeerIDs()) {
            connectToPeer(id);
        }
    }

	// //send a message to the output stream
	// void sendMessage(int peerID)
	// {
	// 	try{
	// 		//stream write the message
	// 		out.writeObject(msg);
	// 		out.flush();
	// 	}
	// 	catch(IOException ioException){
	// 		ioException.printStackTrace();
	// 	}
	// }

    public static void main(String args[])
	{
		SampleClient client = new SampleClient();
		client.run();
	}
}
