import java.nio.ByteBuffer;
import java.util.*;

import javax.sql.rowset.spi.SyncResolver;

public class RequestHandler {
	static volatile BitSet piecesWeHaveAlreadyRequested = new BitSet();

	public static synchronized int findNeededPieceIndexFromPeer(int peerID) {
		BitSet ourBitfield = Bitfield.getSelfBitfield();
		BitSet peerBitfield = Bitfield.getPeerBitfield(peerID);

		BitSet lackingPieces = (BitSet)peerBitfield.clone();
		lackingPieces.andNot(ourBitfield);

		if (lackingPieces.cardinality() == 0)
			return -1;

		Random r = new Random();

		int numTriesLeft = Bitfield.calculatePieceAmt();
		while (numTriesLeft > 0) {
			int randomPick = r.nextInt(lackingPieces.cardinality()) + 1;
			// System.out.println(Logger.getTimestamp() + ": ************************** Random pick: " + randomPick);
			int curIndex = -1;

			for (int i = 0; i < randomPick; i++)
				curIndex = lackingPieces.nextSetBit(curIndex+1);

			if (clientNeedsThisPiece(curIndex)) {
				// System.out.println(Logger.getTimestamp() + ": ************************** Randomly chosen index: " + curIndex);
				return curIndex;
			}
			
			numTriesLeft -= 1;
		}

		return -1; // there are no pieces left, we should have all pieces (or we have all except
					// ones we've requested)
	}

	public static synchronized byte[] constructRequestMessage(int pieceIndex) {
		byte[] pieceIndexBytes = ActualMessageHandler.convertIntTo4Bytes(pieceIndex);
		byte[] message = ActualMessageHandler.addHeader(pieceIndexBytes, ActualMessageHandler.REQUEST);

		piecesWeHaveAlreadyRequested.set(pieceIndex, true);
		// TODO: consider case where piece is not received (other Peer re-decides optimal neighbors); must reset this value

		return message;
	}

	public static synchronized boolean clientNeedsSomePieceFromPeer(int peerID) {
		return findNeededPieceIndexFromPeer(peerID) != -1;
	}

	public static boolean clientNeedsThisPiece(int pieceIndex) {
        if (Bitfield.clientHasThisPiece(pieceIndex))
			return false;
		
		if (piecesWeHaveAlreadyRequested.get(pieceIndex))
			return false;
		
		return true;
    }

	public static synchronized byte[] constructRequestMessageAndChooseRandomPiece(int peerID) {
		int pieceIndex = findNeededPieceIndexFromPeer(peerID);
		if (pieceIndex == -1) {
			System.out.println(Logger.getTimestamp() + ": FATAL: Cannot find the piece client needs from peer: " + peerID);
			return new byte[0];
		}
		return constructRequestMessage(pieceIndex);
	}

	public static synchronized void receivedRequestMessage(int peerID, byte[] msgPayload) {
		if (ChokeHandler.getChokedNeighbors().contains(peerID)) {
			System.out.println(Logger.getTimestamp() + ": REQUEST message from peer " + peerID + " will be ignored because it is choked.");
			return;
		}

		int pieceIndex = ActualMessageHandler.byteArrayToInt(msgPayload);
		System.out.println(Logger.getTimestamp() + ": Sending piece message...");
		PeerProcess.sendMessageToPeer(peerID, PieceHandler.constructPieceMessage(pieceIndex));
		System.out.println(Logger.getTimestamp() + ": Piece message sent!");
	}

	public static synchronized void clearPiecesAlreadyRequestedList() {
		piecesWeHaveAlreadyRequested.clear();
	}
}