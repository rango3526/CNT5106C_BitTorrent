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
    private static Map<String, Logger> map = new HashMap<>();
    public PrintWriter printWriter = null;
    private String peerId;

    public static Logger getLogger(String peerId) {
        synchronized (Logger.class) {
            if (map.get(peerId) == null) {
                map.put(peerId, new Logger(peerId));
            }
        }
        return map.get(peerId);
    }

    // Creates the directories for logging and initializes the PrintWriter
    private Logger(String peerId) {
        try {
            System.out.println("Logger created for peer: " + peerId);
            this.peerId = peerId;
            File file = createLog(peerId);
            initializePrintWriter(file);
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private File createLog(String peerId) throws Exception{
        String path = "../project/log_peer_" + peerId + ".log";
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }

    private void initializePrintWriter(File file) throws IOException{
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        printWriter = new PrintWriter(fileOutputStream, true);
    }

    private String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
    }

    private void writeFile(String message) {
        synchronized (this) {
            printWriter.println(message);
        }
    }

    public void logReceivedHaveMessage(String fromId, int pieceIndex) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " received the 'have' message from " + fromId + " for the piece " + pieceIndex + ".");
    }

    public void logTcpConnectionTo(String connectionToPeerId) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " makes a connection to Peer " + connectionToPeerId + ".");
    }

    public void logTcpConnectionFrom(String connectionFromPeerId) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " is connected from Peer " + connectionFromPeerId + ".");
    }

    public void logChangedPreferredNeighbors(Map<String, String> preferredNeighbors) {
        StringBuilder message = new StringBuilder();
        message.append(getTimeStamp());
        message.append(": Peer ");
        message.append(peerId);
        message.append(" has preferred neighbors [");
        String separator = "";
        for (String remotePeerId: preferredNeighbors.values()) {
            message.append(separator);
            separator = ", ";
            message.append(remotePeerId);
        }
        writeFile(message.toString() + "].");
    }

    public void logNewOptimisticallyUnchokedNeighbor(String unchokedNeighbor) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " has the optimistically unchoked neighbor " + unchokedNeighbor + ".");
    }

    public void logUnchoke(String peerId1) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " is unchoked by " + peerId1 + ".");
    }

    public void logChoke(String peerId1) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " is choked by " + peerId1 + ".");
    }

    public void logInterestedMessageReceived(String fromID) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " received the 'interested' messaging from " + fromID + ".");
    }

    public void logNotInterestedMessageReceived(String fromID) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " received the 'not interested' messaging from " + fromID + ".");
    }

    public void logPieceDownloadComplete(String fromID, int pieceIndex, int numberOfPieces) {
        writeFile(getTimeStamp() + ": Peer " + peerId + " has downloaded the piece " + pieceIndex + " from " + fromID + "." + "Now the number of pieces it has is " + numberOfPieces);
    }

    public void logFullDownloadComplete() {
        writeFile(getTimeStamp() + "Peer " + peerId + " has downloaded the complete file.");
    }
}