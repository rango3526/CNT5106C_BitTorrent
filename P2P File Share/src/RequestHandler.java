import java.nio.ByteBuffer;
import java.util.*;

public class RequestHandler {
	static volatile BitSet bitsWeHaveAlreadyRequested = new BitSet();

	public static synchronized int findNeededPieceIndexFromPeer(int peerID) {
		// TODO: Make random by using ourBitfield.cardinality() and stuff

		BitSet ourBitfield = Bitfield.getSelfBitfield();
		BitSet peerBitfield = Bitfield.getPeerBitfield(peerID);

		for (int i = 0; i < ourBitfield.size(); i++) {
			if (peerBitfield.get(i) && clientNeedsThisPiece(i))
				return i;
		}
		return -1; // there are no pieces left, we should have all pieces (or we have all except
					// ones we've requested)
	}

	public static synchronized byte[] constructRequestMessage(int pieceIndex) {
		byte[] pieceIndexBytes = ActualMessageHandler.convertIntTo4Bytes(pieceIndex);
		byte[] message = ActualMessageHandler.addHeader(pieceIndexBytes, ActualMessageHandler.REQUEST);

		bitsWeHaveAlreadyRequested.set(pieceIndex, true);
		// TODO: consider case where piece is not received (other Peer re-decides optimal neighbors); must reset this value

		return message;
	}

	public static synchronized boolean clientNeedsSomePieceFromPeer(int peerID) {
		return findNeededPieceIndexFromPeer(peerID) != -1;
	}

	public static boolean clientNeedsThisPiece(int pieceIndex) {
        if (Bitfield.clientHasThisPiece(pieceIndex))
			return false;
		
		if (bitsWeHaveAlreadyRequested.get(pieceIndex))
			return false;
		
		return true;
    }

	public static synchronized byte[] constructRequestMessageAndChooseRandomPiece(int peerID) {
		int pieceIndex = findNeededPieceIndexFromPeer(peerID);
		if (pieceIndex == -1) {
			System.out.println("FATAL: Cannot find the piece client needs from peer: " + peerID);
			return new byte[0];
		}
		return constructRequestMessage(pieceIndex);
	}

	public static synchronized void receivedRequestMessage(int peerID, byte[] msgPayload) {
		int pieceIndex = ActualMessageHandler.byteArrayToInt(msgPayload);
		System.out.println("Sending piece message...");
		PeerProcess.sendMessageToPeer(peerID, PieceHandler.constructPieceMessage(pieceIndex));
		System.out.println("Piece message sent!");
	}
}