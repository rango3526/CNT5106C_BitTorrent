import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
    
public class Logger {
    // private static Map<String, Logger> map = new HashMap<>();s
    private static volatile PrintWriter printWriter;
    private static volatile int clientId;

    // public static void main(String args[]) {
    //     initializeLogger(1001);
    //     logReceivedHaveMessage(1002, 69);
    //     // printWriter.close();
    // }

    public static void initializeLogger(int peerId) {
        try {
            System.out.println("Logger created for peer: " + peerId);
            Logger.clientId = peerId;
            File file = createLog(peerId);
            initializePrintWriter(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File createLog(int peerId) throws Exception{
        String path = "log_peer_" + peerId + ".log";
        File file = new File(path);
        file.createNewFile();
        // file.getParentFile().mkdirs();
        return file;
    }

    private static void initializePrintWriter(File file) throws IOException{
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        printWriter = new PrintWriter(fileOutputStream, true);
    }

    public static String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
    }

    private static void writeFile(String message) {
        printWriter.println(message);
    }

    public static void logReceivedHaveMessage(int fromId, int pieceIndex) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " received the 'have' message from " + fromId + " for the piece " + pieceIndex + ".");
    }

    public static void logTcpConnectionTo(int connectionToPeerId) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " makes a connection to Peer " + connectionToPeerId + ".");
    }

    public static void logTcpConnectionFrom(int connectionFromPeerId) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " is connected from Peer " + connectionFromPeerId + ".");
    }

    public static void logChangedPreferredNeighbors(Map<String, String> preferredNeighbors) {
        StringBuilder message = new StringBuilder();
        message.append(getTimeStamp());
        message.append(": Peer ");
        message.append(clientId);
        message.append(" has preferred neighbors [");
        String separator = "";
        for (String remotePeerId: preferredNeighbors.values()) {
            message.append(separator);
            separator = ", ";
            message.append(remotePeerId);
        }
        writeFile(message.toString() + "].");
    }

    public static void logNewOptimisticallyUnchokedNeighbor(int unchokedNeighbor) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " has the optimistically unchoked neighbor " + unchokedNeighbor + ".");
    }

    public static void logUnchokedBy(int peerId1) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " is unchoked by " + peerId1 + ".");
    }

    public static void logChokedBy(int peerId1) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " is choked by " + peerId1 + ".");
    }

    public static void logInterestedMessageReceived(int fromId) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " received the 'interested' messaging from " + fromId + ".");
    }

    public static void logNotInterestedMessageReceived(int fromId) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " received the 'not interested' messaging from " + fromId + ".");
    }

    public static void logPieceDownloadComplete(int fromId, int pieceIndex, int numberOfPieces) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " has downloaded the piece " + pieceIndex + " from " + fromId + "." + " Now the number of pieces it has is " + numberOfPieces);
    }

    public static void logFullDownloadComplete() {
        writeFile(getTimeStamp() + "Peer " + clientId + " has downloaded the complete file.");
    }
}