import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PieceHandler {

    public static void receivedPieceMessage(int peerID, byte[] msgPayload) {
        int pieceIndex = getPieceIndexFromPiecePayload(msgPayload);
        FileHandler.addPiece(pieceIndex, getPieceBytesFromPiecePayload(msgPayload));
        Bitfield.selfReceivedPiece(pieceIndex);
        PeerProcess.broadcastHaveMessage(pieceIndex);
    }

    public static byte[] generatePieceMessage(int pieceIndex) {
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
            return ActualMessageHandler.addHeader(fullPayload, Message.PIECE);
        }
        catch (IOException exception) {
            throw new RuntimeException("IO Exception in constructPieceMessage\n" + exception.getMessage());
        }
    }

    public static int getPieceIndexFromPiecePayload(byte[] pieceMsgPayload) {
        return ByteBuffer.wrap(Arrays.copyOfRange(pieceMsgPayload, 0, 4)).getInt();
    }

    public static byte[] getPieceBytesFromPiecePayload(byte[] pieceMsgPayload) {
        return Arrays.copyOfRange(pieceMsgPayload, 4, pieceMsgPayload.length);
    }
}
