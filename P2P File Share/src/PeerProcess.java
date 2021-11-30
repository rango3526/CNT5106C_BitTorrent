import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeerProcess {
    static Map<Integer, Client> allClients = new HashMap<Integer, Client>();
    static int selfClientID = -1;

    public static void main(String args[]) {
        selfClientID = Integer.parseInt(args[0]);
        Bitfield.init(selfClientID);
        startServer();
        connectToPeers();
        Logger.initializeLogger(selfClientID);
    }

    public static void connectToPeers() {
        List<Integer> peerIDs = ConfigReader.getAllPeerIDs();
        
        for (Integer peerID : peerIDs) {
            try {
                if (peerID < selfClientID) {
                    Socket connection = new Socket(ConfigReader.getIPFromPeerID(peerID), ConfigReader.getPortFromPeerID(peerID));
                    allClients.put(peerID, new Client(connection, true));
                }
            }
            catch (Exception e) {
                System.out.println("Failed to connect to peer: " + peerID);
                e.printStackTrace();
            }
        }
    }

    public static List<Integer> getPeerIDList() {
        return Arrays.asList((Integer[])allClients.keySet().toArray());
    }

    public static double getDownloadRateOfPeer(int peerID) {
        return allClients.get(peerID).getDownloadRateInKBps();
    }

    public static void unchokePeer(int peerID) {
        allClients.get(peerID).unchokePeer();
    }

    public static void chokePeer(int peerID) {
        allClients.get(peerID).chokePeer();
    }

    public static void startServer() {
        try {
            Server.startServer(selfClientID);
        }
        catch (Exception e) {
            throw new RuntimeException("The server ran into a problem: \n" + e.getMessage());
        }
    }

    public static void broadcastHaveMessage(int pieceIndex) {
        byte[] haveMessage = HaveHandler.generateHaveMessage(pieceIndex);
        
        for (Client c : allClients.values()) {
            c.sendMessage(haveMessage);
        }
    }

    public static void sendMessageToPeer(int peerID, byte[] message) {
        allClients.get(peerID).sendMessage(message);
    }
}

