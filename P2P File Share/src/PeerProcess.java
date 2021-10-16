import java.util.ArrayList;
import java.util.List;

public class PeerProcess {
    static List<Client> allClients = new ArrayList<Client>();
    static int selfClientID = -1;

    public static void main(String args[]) {
        selfClientID = getSelfClientID(args);
        startServer();
        connectToPeers();
    }

    public static void connectToPeers() {
        List<Integer> peerIDs = ConfigReader.getAllPeerIDs();

        for (Integer peerID : peerIDs) {
            if (peerID < selfClientID) {
                allClients.add(new Client(selfClientID, peerID));
            }
        }
    }

    public static int getSelfClientID(String args[]) {
        // Depends on how the argument will be passed to PeerProcess by the program that runs all of them

        throw new UnsupportedOperationException();
    }

    public static void startServer() {

    }
}
