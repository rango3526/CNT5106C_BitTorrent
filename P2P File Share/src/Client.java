import java.net.*;
import java.util.concurrent.atomic.AtomicLong;
import java.io.*;

public class Client extends Thread {
	volatile Socket connection; // socket connect to the server
	volatile ObjectOutputStream out; // stream write to the socket
	volatile ObjectInputStream in; // stream read from the socket

	volatile int selfClientID = -1; // This own client's ID
	public volatile int otherPeerID = -1; // ID of peer we're connecting to

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
					System.out.println("Initiated handhshake with " + otherPeerID);
					sendMessage(Handshake.constructHandshakeMessage(selfClientID));
					System.out.println("Waiting for handshake response from " + otherPeerID);
					this.otherPeerID = waitForAndHandleHandshake();
					Logger.logTcpConnectionTo(this.otherPeerID);
					// TODO: make sure this looks right
				} else {
					System.out.println("Waiting for handshake initiation from " + otherPeerID);
					this.otherPeerID = waitForAndHandleHandshake();
					System.out.println("Sending handhshake reponse to " + otherPeerID);
					sendMessage(Handshake.constructHandshakeMessage(selfClientID));
					Logger.logTcpConnectionFrom(this.otherPeerID);
					PeerProcess.connectionFromNewPeer(otherPeerID, this);
				}

				System.out.println("Sending BITFIELD message to " + otherPeerID);
				sendMessage(Bitfield.constructBitfieldMessage());

				System.out.println("Peer " + otherPeerID + " has finished handshaking!");

				while (PeerProcess.isRunning) {
					try {
						try {
							handleAnyMessage(receiveMessage());
						}
						catch (EOFException eofException) {
							// System.out.println("EOF detected, but will continue.");
							// break;
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						break;
					}

					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			} catch (ClassNotFoundException classnot) {
				System.out.println("Data received in unknown format");
				classnot.printStackTrace();
			} catch (Exception e) {
				System.out.println("Exception in second big try statement");
				e.printStackTrace();
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
			System.out.println("Connection to " + otherPeerID + " closed.");
		}

		System.out.println("Client process terminated.");
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
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": CHOKE");
			ChokeHandler.receivedChokeMessage(otherPeerID);
			break;
		case 1:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": UNCHOKE");
			ChokeHandler.receivedUnchokeMessage(otherPeerID);
			break;
		case 2:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": INTEREST");
			InterestHandler.receivedInterestedMessage(otherPeerID);
			break;
		case 3:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": NON-INTEREST");
			InterestHandler.receivedUninterestedMessage(otherPeerID);
			break;
		case 4:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": HAVE");
			HaveHandler.receivedHaveMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 5:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": BITFIELD");
			Bitfield.receivedBitfieldMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 6:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": REQUEST");
			RequestHandler.receivedRequestMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		case 7:
			System.out.println(Logger.getTimeStamp() + ": Message from " + otherPeerID + ": PIECE");
			PieceHandler.receivedPieceMessage(otherPeerID, ActualMessageHandler.extractPayload(msg));
			break;
		default:
			throw new RuntimeException("FATAL: Message received has invalid type");
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
		Bitfield.constructBitfieldMessage();
	}

	public synchronized double getDownloadRateInKBps() { // KiloBytes per second
		return ((double) cumulativeBytesDownloaded.get() / 1000.0 * 1000000000
				/ (double) cumulativeDownloadTimeNanoseconds.get());
	}

	public synchronized void unchokePeer() {
		byte[] unchokeMsg = ChokeHandler.constructChokeMessage(otherPeerID, false);
		ChokeHandler.unchokePeer(otherPeerID);
		System.out.println("Sending UNCHOKE message to " + otherPeerID);
		sendMessage(unchokeMsg);
	}

	public synchronized void chokePeer() {
		byte[] chokeMsg = ChokeHandler.constructChokeMessage(otherPeerID, true);
		ChokeHandler.chokePeer(otherPeerID);
		System.out.println("Sending CHOKE message to " + otherPeerID);
		sendMessage(chokeMsg);
	}

	public synchronized int waitForAndHandleHandshake() throws ClassNotFoundException, IOException {
		// boolean receivedHandshake = false;

		while (true) {
			try {
				byte[] message = receiveMessage();
				if (Handshake.isHandshakeMessage(message)) {
					return Handshake.receivedHandshakeResponseMessage(message);
				}
			} catch (EOFException e) {
				// this is fine, do nothing
			}
		}

		// System.out.println("FATAL: Error caused handshake not to be received from peer: " + otherPeerID);
	}
}