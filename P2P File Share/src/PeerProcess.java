import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerProcess {
    static volatile ConcurrentHashMap<Integer, Client> allClients = new ConcurrentHashMap<>();
    static volatile int selfClientID = -1;

    static volatile OptimisticUnchokeHandler ouh = null;
    static volatile FindPreferredNeighbors fpn = null;

    public static volatile boolean isRunning = true;

    static PrintWriter printWriter;

    public static void main(String args[]) {
        selfClientID = Integer.parseInt(args[0]);

        // Temporary debug logger ********************
        String path = "DEBUG_log_peer_" + selfClientID + ".log";
        File file = new File(path);
        try {
            file.createNewFile();
            System.out.println("File location is: " + file.getAbsolutePath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            printWriter = new PrintWriter(fileOutputStream, true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // ********************************************


        printWriter.println("Initializing peer...");
        System.out.println("Initializing peer...");
        Logger.initializeLogger(selfClientID);
        Bitfield.init(selfClientID);
        int state = ConfigReader.getStateFromPeerID(selfClientID);
        if (state == 1) {
            Bitfield.selfStartsWithFile();
            FileHandler.initializePieceMapFromCompleteFile();
        }
        startServer();
        connectToPeers();
        ouh = new OptimisticUnchokeHandler();
        ouh.start();
        fpn = new FindPreferredNeighbors();
        fpn.start();
        printWriter.println("Done initializing!");
        System.out.println("Done initializing!");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            isRunning = false;
            printWriter.println("Process stopped.");

System.out            .println("Process stopped.");
        }
    }

    public static void connectToPeers() {
        List<Integer> peerIDs = ConfigReader.getAllPeerIDs();
        
        for (Integer peerID : peerIDs) {
            try {
                if (peerID < selfClientID) {
                    Socket connection = new Socket(ConfigReader.getIPFromPeerID(peerID), ConfigReader.getPortFromPeerID(peerID));
                    Client c = new Client(connection, true);
                    c.start();
                    allClients.put(peerID, c);
                }
            }
            catch (Exception e) {
                printWriter.println("Failed to connect to peer: " + peerID);
    
    System.out            .println("Failed to connect to peer: " + peerID);
                e.printStackTrace();
            }
        }
    }

    public static synchronized List<Integer> getPeerIDList() {
        return Arrays.asList(allClients.keySet().toArray(new Integer[0]));
    }

    public static synchronized double getDownloadRateOfPeer(int peerID) {
        return allClients.get(peerID).getDownloadRateInKBps();
    }

    public static synchronized void unchokePeer(int peerID) {
        allClients.get(peerID).unchokePeer();
    }

    public static synchronized void chokePeer(int peerID) {
        allClients.get(peerID).chokePeer();
    }

    public static synchronized void setPreferredNeighbors(List<Integer> preferredNeighbors) {
		List<Integer> peerIDList = getPeerIDList();

        for (Integer peerID : peerIDList) {
            if (preferredNeighbors.contains(peerID) && ChokeHandler.getChokedNeighbors().contains(peerID))
                unchokePeer(peerID);
            else if (peerID != OptimisticUnchokeHandler.getOptimisticallyUnchokedNeighbor())
                chokePeer(peerID);
        }
	}

    public static void startServer() {
        try {
            Server.startServer(selfClientID);
        }
        catch (Exception e) {
            throw new RuntimeException("The server ran into a problem: \n" + e.getMessage());
        }
    }

    public static synchronized void connectionFromNewPeer(int peerID, Client c) {
        allClients.put(peerID, c);
    }

    public static synchronized void broadcastHaveMessage(int pieceIndex) {
        byte[] haveMessage = HaveHandler.generateHaveMessage(pieceIndex);
        
        for (Client c : allClients.values()) {
            c.sendMessage(haveMessage);
        }
    }

    public static synchronized void sendMessageToPeer(int peerID, byte[] message) {
        allClients.get(peerID).sendMessage(message);
    }
}

