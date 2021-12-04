import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
    
public class DebugLogger {
    // private static Map<String, Logger> map = new HashMap<>();s
    private static volatile PrintStream printStream;

    // public static void main(String args[]) {
    //     initializeLogger(1001);
    //     logReceivedHaveMessage(1002, 69);
    //     // printWriter.close();
    // }

    public static void initializeLogger(int peerId) {
        try {
            System.out.println(Logger.getTimestamp() + ": DEBUG Logger created for peer: " + peerId);
            File file = createLog(peerId);
            initializePrintWriter(file);
            System.setOut(printStream);
            System.setErr(printStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File createLog(int peerId) throws Exception{
        String path = "DEBUG_log_peer_" + peerId + ".log";
        File file = new File(path);
        file.createNewFile();
        // file.getParentFile().mkdirs();
        return file;
    }

    private static void initializePrintWriter(File file) throws IOException{
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        printStream = new PrintStream(file);
    }
}