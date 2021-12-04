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
    public static volatile int selfClientID = -1;

    static volatile OptimisticUnchokeHandler ouh = null;
    static volatile FindPreferredNeighbors fpn = null;

    public static volatile boolean isRunning = true;

    public static volatile Server server;

    // static PrintWriter printWriter;

    public static void main(String args[]) {
        selfClientID = Integer.parseInt(args[0]);

        // Temporary debug logger ********************
        // String path = "DEBUG_log_peer_" + selfClientID + ".log";
        // File file = new File(path);
        // try {
        //     file.createNewFile();
        //     System.out.println(Logger.getTimestamp() + ": File location is: " + file.getAbsolutePath());
        // } catch (IOException e1) {
        //     e1.printStackTrace();
        // }
        // try {
        //     FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        //     printWriter = new PrintWriter(fileOutputStream, true);
        // } catch (IOException e1) {
        //     e1.printStackTrace();
        // }
        DebugLogger.initializeLogger(selfClientID);
        // ********************************************


        // printWriter.println("Initializing peer...");
        System.out.println(Logger.getTimestamp() + ": : Initializing peer...");
        Logger.initializeLogger(selfClientID);
        Bitfield.init(selfClientID);
        int state = ConfigReader.getStateFromPeerID(selfClientID);
        if (state == 1) {
            System.out.println(Logger.getTimestamp() + ": STARTING WITH WHOLE FILE");
            FileHandler.initializePieceMapFromCompleteFile();
            Bitfield.selfStartsWithFile();
        }
        startServer();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        connectToPeers();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        ouh = new OptimisticUnchokeHandler();
        ouh.start();
        fpn = new FindPreferredNeighbors();
        fpn.start();
        // printWriter.println("Done initializing!");
        System.out.println(Logger.getTimestamp() + ": Done initializing!");
        try {
            Thread.sleep(450000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            isRunning = false;
            try {
                System.out.println(Logger.getTimestamp() + ": Stopping server on time...");
                Server.listener.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
            for (Client client: allClients.values()) {
                client.interrupt();
            }

            System.out.println(Logger.getTimestamp() + ": PeerProcess stopping on time...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Logger.getTimestamp() + ": PeerProcess stopped.");
        }
    }

    public static void connectToPeers() {
        List<Integer> peerIDs = ConfigReader.getAllPeerIDs();
        
        for (Integer peerID : peerIDs) {
            try {
                if (peerID < selfClientID) {
                    System.out.println(Logger.getTimestamp() + ": Connecting to peer " + peerID + "...");
                    // Socket connection = new Socket(ConfigReader.getIPFromPeerID(peerID), ConfigReader.getPortFromPeerID(peerID));
                    Socket connection = new Socket(ConfigReader.getIPFromPeerID(peerID), Server.sPort);
                    Client c = new Client(connection, true);
                    c.start();
                    allClients.put(peerID, c);
                    System.out.println(Logger.getTimestamp() + ": Connected.");
                }
                else {
                    System.out.println(Logger.getTimestamp() + ": Skipping connection to later peer " + peerID);
                }
            }
            catch (Exception e) {
                // printWriter.println("Failed to connect to peer: " + peerID);
    
                System.out.println(Logger.getTimestamp() + ": Failed to connect to peer: " + peerID);
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
            if (preferredNeighbors.contains(peerID))
                unchokePeer(peerID);
            else if (peerID != OptimisticUnchokeHandler.getOptimisticallyUnchokedNeighbor())
                chokePeer(peerID);
        }
	}

    public static void startServer() {
        Server.selfClientID = selfClientID;
        server = new Server();
        server.start();
    }

    public static synchronized void connectionFromNewPeer(int peerID, Client c) {
        allClients.put(peerID, c);
    }

    public static synchronized void broadcastHaveMessage(int pieceIndex) {
        byte[] haveMessage = HaveHandler.constructHaveMessage(pieceIndex);
        
        for (Client c : allClients.values()) {
            System.out.println(Logger.getTimestamp() + ": Sending HAVE message to " + c.otherPeerID);
            c.sendMessage(haveMessage);
        }
    }

    public static synchronized void sendMessageToPeer(int peerID, byte[] message) {
        allClients.get(peerID).sendMessage(message);
    }
}

