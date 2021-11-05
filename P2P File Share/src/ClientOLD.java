import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/*

This entire file is just for reference; we're not using it

*/
public class ClientOLD {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
	ObjectInputStream in;          //stream read from the socket
	byte[] messageToSend;                //message send to the server
	byte[] messageFromServer;                //capitalized message read from the server

	int clientID = -1; 		// This own client's ID
	int peerID = -1; 		// ID we're trying to connect to
	int port = -1;
	String ip = "";

	long cumulativeDownloadTimeNanoseconds = 0;
	long cumulativeBytesDownloaded = 0;

	boolean createdFromServer = false;

	public ClientOLD(int _clientID, int _peerID) {
		peerID = _peerID;
		clientID = _clientID;

		port = getPortFromPeerID(peerID);
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
				messageToSend = Handshake.getHandshakeMessage(clientID);
				sendMessage(messageToSend);
				messageFromServer = receiveMessage();
				handleHandshakeResponse(messageFromServer);

				sendBitfieldMessage();

				messageFromServer = receiveMessage();
				handleBitfieldMessage(messageFromServer);

				messageFromServer = receiveMessage();
				handleUnchokeMessage(messageFromServer);
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
		long startTime = System.nanoTime();
		byte[] obj = (byte[])in.readObject();
		long endTime = System.nanoTime();

		cumulativeDownloadTimeNanoseconds += endTime - startTime;
		cumulativeBytesDownloaded += obj.length;

		return obj;
	}

	int getPortFromPeerID(int id) { // use the config file
        return ConfigReader.getPortFromPeerID(id);
    }

    String getIPFromPeerID(int id) { // use the config file
        return ConfigReader.getIPFromPeerID(id).toString();
    }

	// TODO: David
	void handleHandshakeResponse(byte[] handshakeResponse) {
		ByteBuffer bytearray = ByteBuffer.wrap(handshakeResponse);
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
	}

	void sendBitfieldMessage() {
		Bitfield.constructBitfieldMessage(Bitfield.getBitfieldMessagePayload());
	}

	public double getDownloadRateInKBps() {		// KiloBytes per second
		double downloadRate = ((double)cumulativeBytesDownloaded/1000.0*1000000000/(double)cumulativeDownloadTimeNanoseconds);
		return downloadRate;
	}

	public void UnchokePeer() {
		throw new UnsupportedOperationException();
	}

	public void ChokePeer() {
		throw new UnsupportedOperationException();
	}

	public void handleBitfieldMessage(byte[] bitfieldMessage) {
		BitSet peerBitfield = Bitfield.byteArrayToBitfield(ActualMessageHandler.extractPayload(bitfieldMessage));
		Bitfield.setPeerBitfield(peerID, peerBitfield);
		if (Bitfield.clientNeedsPiecesFromPeer(peerID)) {
			sendMessage(InterestHandler.GetInterestMessage());
		}
	}

	public void handleUnchokeMessage(byte[] unchokeMessage) {
		if (Bitfield.clientNeedsPiecesFromPeer(peerID)) {
			byte[] requestMessage;

			int neededPieceIndex = Bitfield.getFirstPieceIndexNeedFromPeer(peerID);

			requestMessage = RequestHandler.constructRequestMessage(neededPieceIndex);
		}
	}
}