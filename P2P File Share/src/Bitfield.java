import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class Bitfield {
    private static volatile int maxPieceAmt = -1;
    private static volatile AtomicInteger curPieceNumPossessed = new AtomicInteger(0);
    private static volatile int selfClientID = -1;
    private static volatile ConcurrentHashMap<Integer, BitSet> bitfields = new ConcurrentHashMap<>();

    private static boolean selfStartedWithData = false;
    
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
        if (maxPieceAmt == -1) {
            int fileSize = ConfigReader.getFileSize();
            int pieceSize = ConfigReader.getPieceSize();
    
            maxPieceAmt = fileSize / pieceSize;
    
            if (fileSize % pieceSize != 0) {
                maxPieceAmt += 1;
            }
        }
    }

    public static void peerReceivedPiece(int peerID, int pieceIndex) {
        bitfields.get(peerID).set(pieceIndex, true);
    }

    public static void selfReceivedPiece(int pieceIndex) {
        curPieceNumPossessed = new AtomicInteger(curPieceNumPossessed.get() + 1);
        bitfields.get(selfClientID).set(pieceIndex, true);
        if (!selfStartedWithData && curPieceNumPossessed.get() == maxPieceAmt) {
            FileHandler.combinePiecesIntoCompleteFile();
        }
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
            PeerProcess.sendMessageToPeer(otherPeerID, InterestHandler.constructInterestMessage(true));
		}
    }

    public static void selfStartsWithFile() {
        selfStartedWithData = true;
        for (int i = 0; i < maxPieceAmt; i++) {
            selfReceivedPiece(i);
        }
    }

    public static byte[] constructBitfieldMessage(byte[] bitfield) 
    {
        // this one is just adding the header (length + message type) to the payload (payload is just the bitfield)
		/*
		 * int lengthOfBitfield = bitfield.length; byte [] newBitfieldLength =
		 * ActualMessageHandler.convertIntToBytes(lengthOfBitfield); bitfield =
		 * ActualMessageHandler.addHeader(newBitfieldLength, Message.BITFIELD);
		 */
        //throw new UnsupportedOperationException();
    	return ActualMessageHandler.addHeader(bitfield, Message.BITFIELD);
    }

    public static int getNumberOfPiecesClientHas() {
        return curPieceNumPossessed.get();
    }
}
