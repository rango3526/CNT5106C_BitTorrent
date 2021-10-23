public class Piece {
    public static void ReceivedPiece(int pieceIndex) {
        PeerProcess.BroadcastHaveMessage(pieceIndex);
    }

    public static byte[] GeneratePieceMessage(int pieceIndex) {
        byte[] filePiece = FileHandler.GetFilePiece(pieceIndex);
        byte[] pieceMessage = ActualMessageHandler.constructPieceMessage(filePiece);
        return pieceMessage;
    }
}
