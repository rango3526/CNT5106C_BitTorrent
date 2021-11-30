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
    private static PrintWriter printWriter;
    private static int clientId;

    // public static Logger getLogger(String peerId) {
    //     synchronized (Logger.class) {
    //         if (map.get(peerId) == null) {
    //             map.put(peerId, new Logger(peerId));
    //         }
    //     }
    //     return map.get(peerId);
    // }

    // public static Logger getLogger() {
    //     if (instance == null) {
    //         instance = new Logger(peerId);
    //     }
    //     return instance;
    // }

    // Creates the directories for logging and initializes the PrintWriter
    // private Logger(String peerId) {
    //     try {
    //         System.out.println("Logger created for peer: " + peerId);
    //         this.peerId = peerId;
    //         File file = createLog(peerId);
    //         initializePrintWriter(file);
    //     }
    //     catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //     }
    // }

    public static void initializeLogger(int peerId) {
        try {
            System.out.println("Logger created for peer: " + peerId);
            Logger.clientId = peerId;
            File file = createLog(peerId);
            initializePrintWriter(file);
        } catch (Exception e) {
            System.out.println("Error: ");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static File createLog(int peerId) throws Exception{
        String path = "../project/log_peer_" + peerId + ".log";
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }

    private static void initializePrintWriter(File file) throws IOException{
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        printWriter = new PrintWriter(fileOutputStream, true);
    }

    private static String getTimeStamp() {
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

    public static void logUnchoke(int peerId1) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " is unchoked by " + peerId1 + ".");
    }

    public static void logChoke(int peerId1) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " is choked by " + peerId1 + ".");
    }

    public static void logInterestedMessageReceived(int fromId) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " received the 'interested' messaging from " + fromId + ".");
    }

    public static void logNotInterestedMessageReceived(int fromId) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " received the 'not interested' messaging from " + fromId + ".");
    }

    public static void logPieceDownloadComplete(int fromId, int pieceIndex, int numberOfPieces) {
        writeFile(getTimeStamp() + ": Peer " + clientId + " has downloaded the piece " + pieceIndex + " from " + fromId + "." + "Now the number of pieces it has is " + numberOfPieces);
    }

    public static void logFullDownloadComplete() {
        writeFile(getTimeStamp() + "Peer " + clientId + " has downloaded the complete file.");
    }
}