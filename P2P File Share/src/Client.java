import java.net.*;
import java.util.concurrent.atomic.AtomicLong;
import java.io.*;

public class Client extends Thread {
	volatile Socket connection; // socket connect to the server
	volatile ObjectOutputStream out; // stream write to the socket
	volatile ObjectInputStream in; // stream read from the socket

	volatile int selfClientID = -1; // This own client's ID
	volatile int otherPeerID = -1; // ID of peer we're connecting to

	volatile AtomicLong cumulativeDownloadTimeNanoseconds = new AtomicLong(0);
	volatile AtomicLong cumulativeBytesDownloaded = new AtomicLong(0);

	volatile boolean shouldInitiateHandshake = false;

	public Client(Socket connection, boolean shouldInitiateHandshake) {
		this.selfClientID = PeerProcess.selfClientID;
		this.connection = connection;
		this.shouldInitiateHandshake = shouldInitiateHandshake;
	}

	@Override
	public void run() {
		try {
			// initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());

			try {
				if (shouldInitiateHandshake) {
					sendMessage(Handshake.constructHandshakeMessage(selfClientID));
					this.otherPeerID = Handshake.receivedHandshakeResponseMessage(receiveMessage());
					Logger.logTcpConnectionTo(this.otherPeerID);
					// TODO: make sure this looks right
				} else {
					this.otherPeerID = Handshake.receivedHandshakeResponseMessage(receiveMessage());
					sendMessage(Handshake.constructHandshakeMessage(selfClientID));
					Logger.logTcpConnectionFrom(this.otherPeerID);
					PeerProcess.connectionFromNewPeer(otherPeerID, this);
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

	synchronized void handleAnyMessage(byte[] msg) {
		int messageType = ActualMessageHandler.getMsgType(msg);

		switch (messageType) {
		case 0:
			ChokeHandler.receivedChokeMessage(otherPeerID);
			break;
		case 1:
			ChokeHandler.receivedUnchokeMessage(otherPeerID);
			break;
		case 2:
			InterestHandler.receivedInterestedMessage(otherPeerID);
			break;
		case 3:
			InterestHandler.receivedUninterestedMessage(otherPeerID);
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

		cumulativeDownloadTimeNanoseconds = new AtomicLong(cumulativeDownloadTimeNanoseconds.get() + endTime - startTime);
		cumulativeBytesDownloaded = new AtomicLong(cumulativeBytesDownloaded.get() + obj.length);

		return obj;
	}

	synchronized int getPortFromPeerID(int id) { // use the config file
		return ConfigReader.getPortFromPeerID(id);
	}

	synchronized String getIPFromPeerID(int id) { // use the config file
		return ConfigReader.getIPFromPeerID(id).toString();
	}

	synchronized void sendBitfieldMessage() {
		Bitfield.constructBitfieldMessage(Bitfield.getBitfieldMessagePayload());
	}

	public synchronized double getDownloadRateInKBps() { // KiloBytes per second
		return ((double) cumulativeBytesDownloaded.get() / 1000.0 * 1000000000
				/ (double) cumulativeDownloadTimeNanoseconds.get());
	}

	public synchronized void unchokePeer() {
		byte[] unchokeMsg = ChokeHandler.constructChokeMessage(otherPeerID, false);
		ChokeHandler.unchokePeer(otherPeerID);
		sendMessage(unchokeMsg);
	}

	public synchronized void chokePeer() {
		byte[] chokeMsg = ChokeHandler.constructChokeMessage(otherPeerID, true);
		ChokeHandler.chokePeer(otherPeerID);
		sendMessage(chokeMsg);
	}
}