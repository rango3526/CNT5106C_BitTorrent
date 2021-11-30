import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PieceHandler {

    public static synchronized void receivedPieceMessage(int peerID, byte[] msgPayload) {
        int pieceIndex = getPieceIndexFromPiecePayload(msgPayload);
        Logger.logPieceDownloadComplete(peerID, pieceIndex, Bitfield.getNumberOfPiecesClientHas() + 1);
        FileHandler.addPiece(pieceIndex, getPieceBytesFromPiecePayload(msgPayload));
        Bitfield.selfReceivedPiece(pieceIndex);
        PeerProcess.broadcastHaveMessage(pieceIndex);

        if (RequestHandler.clientNeedsSomePieceFromPeer(peerID)) {
            PeerProcess.sendMessageToPeer(peerID, RequestHandler.constructRequestMessage(peerID));
        }
    }

    public static synchronized byte[] generatePieceMessage(int pieceIndex) {
        byte[] filePiece = FileHandler.GetFilePiece(pieceIndex);
        byte[] pieceMessage = constructPieceMessage(pieceIndex, filePiece);
        return pieceMessage;
    }

    public static byte[] constructPieceMessage(int pieceIndex, byte[] pieceBytes) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(ActualMessageHandler.convertIntToBytes(pieceIndex));
            outputStream.write(pieceBytes);
    
            byte[] fullPayload = outputStream.toByteArray();
            return ActualMessageHandler.addHeader(fullPayload, ActualMessageHandler.PIECE);
        }
        catch (IOException exception) {
            throw new RuntimeException("IO Exception in constructPieceMessage\n" + exception.getMessage());
        }
    }

    public static byte[] constructPieceMessage(int pieceIndex) {
        byte[] pieceBytes = FileHandler.GetFilePiece(pieceIndex);
        return constructPieceMessage(pieceIndex, pieceBytes);
    }

    public static int getPieceIndexFromPiecePayload(byte[] pieceMsgPayload) {
        return ByteBuffer.wrap(Arrays.copyOfRange(pieceMsgPayload, 0, 4)).getInt();
    }

    public static byte[] getPieceBytesFromPiecePayload(byte[] pieceMsgPayload) {
        return Arrays.copyOfRange(pieceMsgPayload, 4, pieceMsgPayload.length);
    }
}
