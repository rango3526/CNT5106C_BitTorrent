public class Piece {
    public static void ReceivedPiece(int pieceIndex) {
        PeerProcess.BroadcastHaveMessage(pieceIndex);
    }
}
