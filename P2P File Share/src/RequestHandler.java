import java.nio.ByteBuffer;
import java.util.*;

public class RequestHandler {
	static volatile BitSet bitsWeHaveAlreadyRequested = new BitSet();

	public static synchronized int findNeededPieceIndexFromPeer(int peerID) {
		// TODO: Make random by using ourBitfield.cardinality() and stuff

		BitSet ourBitfield = Bitfield.getSelfBitfield();
		BitSet peerBitfield = Bitfield.getPeerBitfield(peerID);

		for (int i = 0; i < ourBitfield.length(); i++) {
			if (!ourBitfield.get(i) && peerBitfield.get(i) && clientNeedsThisPiece(i))
				return i;
		}
		return -1; // there are no pieces left, we should have all pieces (or we have all except
					// ones we've requested)
	}

	public static synchronized byte[] constructRequestMessage(int pieceIndex) {
		byte[] newPieceIndex = ActualMessageHandler.convertIntToBytes(pieceIndex);
		byte[] message = ActualMessageHandler.addHeader(newPieceIndex, ActualMessageHandler.REQUEST);

		bitsWeHaveAlreadyRequested.set(pieceIndex, true);
		// TODO: consider case where piece is not received (other Peer re-decides optimal neighbors); must reset this value

		return message;
	}

	public static synchronized boolean clientNeedsSomePieceFromPeer(int peerID) {
		BitSet peerBitfield = Bitfield.getPeerBitfield(peerID);

		for (int i = 0; i < peerBitfield.length(); i++) {
			if (peerBitfield.get(i) && clientNeedsThisPiece(i))
				return true;
		}

		return false;
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
		return constructRequestMessage(pieceIndex);
	}

	public static synchronized void receivedRequestMessage(int peerID, byte[] msgPayload) {
		int pieceIndex = ByteBuffer.wrap(msgPayload).getInt();
		PeerProcess.sendMessageToPeer(peerID, PieceHandler.constructPieceMessage(pieceIndex));
	}
}