import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HaveHandler {
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

        return constructHaveMessage(pieceByteArray);
    }

    public static int haveMessagePayloadToPieceIndex(byte[] haveMessagePayload) {
        // TODO: Ranger, check if little-endian vs big-endian, also check in generateHaveMessage
        ByteBuffer wrappedBB = ByteBuffer.wrap(haveMessagePayload); 
        return wrappedBB.getInt();
    }

    public static synchronized void receivedHaveMessage(int fromPeerID, byte[] msgPayload) {
        int haveIndex = haveMessagePayloadToPieceIndex(msgPayload);
        Logger.logReceivedHaveMessage(fromPeerID, haveIndex);
        Bitfield.peerReceivedPiece(fromPeerID, haveIndex);
    }

    public static byte[] constructHaveMessage(byte[] pieceIndexByteArray) {
        return ActualMessageHandler.addHeader(pieceIndexByteArray, Message.HAVE);
    }
}
