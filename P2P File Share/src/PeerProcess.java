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
        selfClientID = getSelfClientID(args);
        Bitfield.init(selfClientID);
        startServer();
        connectToPeers();
    }

    public static void connectToPeers() {
        List<Integer> peerIDs = ConfigReader.getAllPeerIDs();

        for (Integer peerID : peerIDs) {
            if (peerID < selfClientID) {
                allClients.put(peerID, new Client(selfClientID, peerID));
            }
        }
    }

    public static int getSelfClientID(String args[]) {
        // Depends on how the argument will be passed to PeerProcess by the program that runs all of them

        throw new UnsupportedOperationException();
    }

    public static List<Integer> GetPeerIDList() {
        return Arrays.asList((Integer[])allClients.keySet().toArray());
    }

    public static double GetDownloadRateOfPeer(int peerID) {
        return allClients.get(peerID).getDownloadRateInKBps();
    }

    public static void UnchokePeer(int peerID) {
        allClients.get(peerID).UnchokePeer();
    }

    public static void ChokePeer(int peerID) {
        allClients.get(peerID).ChokePeer();
    }

    public static void startServer() {
        Server.startServer(selfClientID);
    }

    public static void BroadcastHavePiece(int pieceIndex) {
        byte[] haveMessage = Have.generateHaveMessage(pieceIndex);
        
        for (Client c : allClients.values()) {
            c.sendMessage(haveMessage);
        }
    }
}
