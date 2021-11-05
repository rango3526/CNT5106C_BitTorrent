import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client extends Thread {
	Socket connection; // socket connect to the server
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	byte[] messageToSend; // message send to the server
	byte[] messageFromServer; // capitalized message read from the server

	int selfClientID = -1; // This own client's ID
	int otherPeerID = -1; // ID of peer we're connecting to

	long cumulativeDownloadTimeNanoseconds = 0;
	long cumulativeBytesDownloaded = 0;

	boolean shouldInitiateHandshake = false;

	public Client(Socket connection, boolean shouldInitiateHandshake) {
		this.selfClientID = PeerProcess.selfClientID;
		this.shouldInitiateHandshake = shouldInitiateHandshake;
	}

	public void run() {
		try {
			// initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());

			try {
				if (shouldInitiateHandshake) {
					sendMessage(Handshake.getHandshakeMessage(selfClientID));
					handleHandshakeResponse(receiveMessage());
				} else {
					handleHandshakeResponse(receiveMessage());
					sendMessage(Handshake.getHandshakeMessage(selfClientID));
				}

				while (true) {
					handleAnyMessage(receiveMessage());
				}
			} catch (ClassNotFoundException classnot) {
				System.err.println("Data received in unknown format");
			}
		} catch (IOException ioException) {
			System.out.println("Disconnect with Client " + otherPeerID);
		} finally {
			// Close connections
			try {
				in.close();
				out.close();
				connection.close();
			} catch (IOException ioException) {
				System.out.println("Disconnect with Client " + otherPeerID);
			}
		}
	}

	// send a message to the output stream
	void sendMessage(byte[] msg) {
		try {
			// stream write the message
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	void handleAnyMessage(byte[] msg) {
		int messageType = ActualMessageHandler.getMsgType(msg);

		switch (messageType) {
		case 0:
			ChokeHandler.receivedChokeMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 1:
			ChokeHandler.receivedUnchokeMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 2:
			InterestHandler.receivedInterestedMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 3:
			InterestHandler.receivedUninterestedMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 4:
			HaveHandler.receivedHaveMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 5:
			Bitfield.receivedBitfieldMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 6:
			RequestHandler.receivedRequestMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 8:
			PieceHandler.receivedPieceMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		default:
			throw new RuntimeException("Message received has invalid type");
		}
	}

	byte[] receiveMessage() throws IOException, ClassNotFoundException {
		long startTime = System.nanoTime();
		byte[] obj = (byte[]) in.readObject();
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

		this.otherPeerID = peerIdInt;
	}

	void sendBitfieldMessage() {
		Bitfield.constructBitfieldMessage(Bitfield.getBitfieldMessagePayload());
	}

	public double getDownloadRateInKBps() { // KiloBytes per second
		double downloadRate = ((double) cumulativeBytesDownloaded / 1000.0 * 1000000000
				/ (double) cumulativeDownloadTimeNanoseconds);
		return downloadRate;
	}

	public void unchokePeer() {
		throw new UnsupportedOperationException();
	}

	public void chokePeer() {
		throw new UnsupportedOperationException();
	}

	public void handleBitfieldMessage(byte[] bitfieldMessage) {
		BitSet peerBitfield = Bitfield.byteArrayToBitfield(ActualMessageHandler.extractPayload(bitfieldMessage));
		Bitfield.setPeerBitfield(otherPeerID, peerBitfield);
		if (Bitfield.clientNeedsPiecesFromPeer(otherPeerID)) {
			sendMessage(InterestHandler.GetInterestMessage());
		}
	}

	public void handleUnchokeMessage(byte[] unchokeMessage) {
		if (Bitfield.clientNeedsPiecesFromPeer(otherPeerID)) {
			byte[] requestMessage;

			int neededPieceIndex = Bitfield.getFirstPieceIndexNeedFromPeer(otherPeerID);

			requestMessage = RequestHandler.constructRequestMessage(neededPieceIndex);
		}
	}
}