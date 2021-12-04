import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class Bitfield {
    private static volatile int maxPieceAmt = -1;
    private static volatile AtomicInteger curPieceNumPossessed = new AtomicInteger(0);
    private static volatile int selfClientID = -1;
    private static volatile ConcurrentHashMap<Integer, BitSet> bitfields = new ConcurrentHashMap<>();

    private static volatile boolean selfStartedWithData = false;
    public static volatile boolean hasAllPieces = false;
    
    private static boolean initialized = false;

    public static void init(int selfClientID) {
        if (!initialized) {
            Bitfield.maxPieceAmt = calculatePieceAmt();
            Bitfield.selfClientID = selfClientID;
            
            List<Integer> allPeerIDs = ConfigReader.getAllPeerIDs();

            for (Integer peerID : allPeerIDs) {
                int pieceAmt = Bitfield.calculatePieceAmt();
                System.out.println(Logger.getTimestamp() + ": Create bitset size " + pieceAmt + " for peer: " + peerID);
                BitSet thisBitSet = new BitSet(pieceAmt);
                for (int i = 0; i < pieceAmt; i++) {
                    thisBitSet.set(i, false);
                }
                bitfields.put(peerID, thisBitSet);
            }
            
            initialized = true;
        }
    }

    public static int calculatePieceAmt() {
        int fileSize = ConfigReader.getFileSize();
        int pieceSize = ConfigReader.getPieceSize();

        int maximumPieceAmt = fileSize / pieceSize;

        if (fileSize % pieceSize != 0) {
            maximumPieceAmt += 1;
        }

        return maximumPieceAmt;
    }

    public static void peerReceivedPiece(int peerID, int pieceIndex) {
        bitfields.get(peerID).set(pieceIndex, true);
    }

    public static void selfReceivedPiece(int pieceIndex) {
        curPieceNumPossessed = new AtomicInteger(curPieceNumPossessed.get() + 1);
        bitfields.get(selfClientID).set(pieceIndex, true);
        // if (!selfStartedWithData && curPieceNumPossessed.get() == maxPieceAmt) {
        //     FileHandler.combinePiecesIntoCompleteFile();
        // }
        if (curPieceNumPossessed.get() == maxPieceAmt) {
            FileHandler.combinePiecesIntoCompleteFile();
            hasAllPieces = true;
        }
    }

    public static void setPeerBitfield(int peerID, BitSet bitfield) {
        bitfields.put(peerID, bitfield);
    }

    public static BitSet getPeerBitfield(int peerID) {
        return (BitSet)bitfields.get(peerID).clone();
    }

    public static BitSet getSelfBitfield() {
        return (BitSet)bitfields.get(selfClientID).clone();
    }

    public static byte[] bitfieldToByteArray(BitSet bitfield) {
        return bitfield.toByteArray();
    }

    public static BitSet byteArrayToBitfield(byte[] bytes) {
        BitSet receivedBitSet = BitSet.valueOf(bytes);

        BitSet newBitSet = new BitSet(Bitfield.calculatePieceAmt());
        for (int i = 0; i < newBitSet.size(); i++) {
            newBitSet.set(i, receivedBitSet.get(i));
        }

        return newBitSet;
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

    public static boolean clientHasThisPiece(int pieceIndex) {
        return getSelfBitfield().get(pieceIndex);
    }

    public static void printBitset(BitSet bitSet) {
        StringBuilder s = new StringBuilder();
        for( int i = 0; i < bitSet.size();  i++ )
        {
            s.append( bitSet.get(i) ? "1": "0" );
        }

        System.out.println(s);
    }

    public static void receivedBitfieldMessage(int otherPeerID, byte[] msgPayload) {
        BitSet peerBitfield = Bitfield.byteArrayToBitfield(msgPayload);
        printBitset(peerBitfield);
		Bitfield.setPeerBitfield(otherPeerID, peerBitfield);
		
        // send interested / non-interested message
        boolean interested = RequestHandler.clientNeedsSomePieceFromPeer(otherPeerID);
        System.out.println(Logger.getTimestamp() + ": Sending " + (interested ? "" : "NON-") + "INTERESTED message to " + otherPeerID);
        PeerProcess.sendMessageToPeer(otherPeerID, InterestHandler.constructInterestMessage(interested));
    }

    public static void selfStartsWithFile() {
        selfStartedWithData = true;
        hasAllPieces = true;
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
		 * ActualMessageHandler.addHeader(newBitfieldLength, ActualMessageHandler.BITFIELD);
		 */
        //throw new UnsupportedOperationException();
    	return ActualMessageHandler.addHeader(bitfield, ActualMessageHandler.BITFIELD);
    }

    public static byte[] constructBitfieldMessage() 
    {
        // constructs bitfield for self
    	return constructBitfieldMessage(getSelfBitfieldAsByteArray());
    }

    public static int getNumberOfPiecesClientHas() {
        return curPieceNumPossessed.get();
    }
}
