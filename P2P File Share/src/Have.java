import java.math.BigInteger;
import java.nio.ByteBuffer;

public class Have {
    public static final int TYPE = 4;

    public Have() {

    }

    public static byte[] generateHaveMessage(int pieceIndex) {
        // TODO: Ranger, check if little-endian vs big-endian
        byte[] pieceByteArray = BigInteger.valueOf(pieceIndex).toByteArray();
        if (pieceByteArray.length != 4) {
            byte[] newBytes = new byte[4];
            for (int i = 0; i < pieceByteArray.length; i++) {
                newBytes[i] = pieceByteArray[i];
            }
            pieceByteArray = newBytes;
        }

        return ActualMessageHandler.constructHaveMessage(pieceByteArray);
    }

    public static int haveMessagePayloadToPieceIndex(byte[] haveMessagePayload) {
        // TODO: Ranger, check if little-endian vs big-endian, also check in generateHaveMessage
        ByteBuffer wrappedBB = ByteBuffer.wrap(haveMessagePayload); 
        return wrappedBB.getInt();
    }

    public static void handleHaveMessageReceipt(int fromPeerID, byte[] haveMessagePayload) {
        int haveIndex = haveMessagePayloadToPieceIndex(haveMessagePayload);
        Bitfield.peerReceivedPiece(fromPeerID, haveIndex);
    }
}
