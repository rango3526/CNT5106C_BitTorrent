import java.util.HashMap;
import java.util.*;

public class Bitfield {
    private static int pieceAmt = -1;
    private static int selfClientID = -1;
    private static HashMap<Integer, BitSet> bitfields = new HashMap<Integer, BitSet>();
    
    private static boolean initialized = false;

    public static void init(int selfClientID) {
        if (!initialized) {
            calculatePieceAmt();
            
            List<Integer> allPeerIDs = ConfigReader.getAllPeerIDs();
            for (Integer peerID : allPeerIDs) {
                bitfields.put(peerID, new BitSet());
            }
            
            initialized = true;
        }
    }

    private static void calculatePieceAmt() {
        if (pieceAmt == -1) {
            int fileSize = ConfigReader.getFileSize();
            int pieceSize = ConfigReader.getPieceSize();
    
            pieceAmt = fileSize / pieceSize;
    
            if (fileSize % pieceSize != 0) {
                pieceAmt += 1;
            }
        }
    }

    public static void peerReceivedPiece(int peerID, int pieceIndex) {
        bitfields.get(peerID).set(pieceIndex, true);
    }

    public static void selfReceivedPiece(int pieceIndex) {
        bitfields.get(selfClientID).set(pieceIndex, true);
    }

    public static void setPeerBitfield(int peerID, BitSet bitfield) {
        bitfields.put(peerID, bitfield);
    }

    public static BitSet getPeerBitfield(int peerID) {
        return bitfields.get(peerID);
    }

    public static BitSet getSelfBitfield() {
        return bitfields.get(selfClientID);
    }

    public static byte[] bitfieldToByteArray(BitSet bitfield) {
        return bitfield.toByteArray();
    }

    public static BitSet byteArrayToBitfield(byte[] bytes) {
        return BitSet.valueOf(bytes);
    }
    
    public static boolean doesPeerHavePiece(int peerID, int pieceIndex) {
        return bitfields.get(peerID).get(pieceIndex);
    }

    public static byte[] getSelfBitfieldAsByteArray() {
        return bitfieldToByteArray(getSelfBitfield());
    }

    public static byte[] getBitfieldMessagePayload() {
        return getSelfBitfieldAsByteArray();
    }

    // does this client need at least 1 piece from the peer with peerID?
    public static boolean clientNeedsPiecesFromPeer(int otherPeerID) { 
        throw new UnsupportedOperationException();
    }

    public static int getFirstPieceIndexNeedFromPeer(int otherPeerID) {
        throw new UnsupportedOperationException();
    }

    public static void receivedBitfieldMessage(int otherPeerID, byte[] msgPayload) {
        BitSet peerBitfield = Bitfield.byteArrayToBitfield(msgPayload);
		Bitfield.setPeerBitfield(otherPeerID, peerBitfield);
		if (Bitfield.clientNeedsPiecesFromPeer(otherPeerID)) {
			// sendMessage(InterestHandler.GetInterestMessage());
            PeerProcess.sendMessageToPeer(otherPeerID, InterestHandler.getInterestMessage());
		}
    }

    public static byte[] constructBitfieldMessage(byte[] bitfield) {
        // this one is just adding the header (length + message type) to the payload (payload is just the bitfield)
        throw new UnsupportedOperationException();
    }
}
