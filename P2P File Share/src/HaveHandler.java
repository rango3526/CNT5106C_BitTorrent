import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HaveHandler {

    public static synchronized void receivedHaveMessage(int fromPeerID, byte[] msgPayload) {
        int haveIndex = ActualMessageHandler.byteArrayToInt(msgPayload);
        Logger.logReceivedHaveMessage(fromPeerID, haveIndex);
        Bitfield.peerReceivedPiece(fromPeerID, haveIndex);

        // send interested / non-interested message
        System.out.println("Sending INTEREST(or not) message to " + fromPeerID);
        PeerProcess.sendMessageToPeer(fromPeerID, InterestHandler.constructInterestMessage(RequestHandler.clientNeedsSomePieceFromPeer(fromPeerID)));
    }

    public static byte[] constructHaveMessage(int pieceIndex) {
        byte[] pieceIndexByteArray = ActualMessageHandler.convertIntTo4Bytes(pieceIndex);
        return ActualMessageHandler.addHeader(pieceIndexByteArray, ActualMessageHandler.HAVE);
    }
}
