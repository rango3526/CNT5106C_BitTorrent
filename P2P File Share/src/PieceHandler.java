public class PieceHandler {
    private static void receivedPiece(int pieceIndex) {
        PeerProcess.BroadcastHaveMessage(pieceIndex);
    }

    public static void receivedPieceMessage(int peerID, byte[] msgPayload) {
        throw new UnsupportedOperationException();
    }

    public static byte[] generatePieceMessage(int pieceIndex) {
        byte[] filePiece = FileHandler.GetFilePiece(pieceIndex);
        byte[] pieceMessage = constructPieceMessage(filePiece);
        return pieceMessage;
    }

    public static byte[] constructPieceMessage(byte[] pieceBytes) {
        throw new UnsupportedOperationException();
    }
}
