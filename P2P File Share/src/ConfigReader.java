import java.util.*;

public class ConfigReader {
    // TODO: Tre'


    String pathToCommonCfg = "../config/Common.cfg";        // these paths might be wrong, not sure about the double . at the start
    String pathToPeerInfoCfg = "../config/PeerInfo.cfg";

    public static List<Integer> getAllPeerIDs() {
        // return list of client IDs
        // i.e. {1001, 1002, 1003, ......}
        throw new UnsupportedOperationException();
    }

    public static String getIPFromPeerID(int peerID) {
        // ex. when I give client ID 1001, return the IP of client 1001
        throw new UnsupportedOperationException();
    }

    public static int getPortFromPeerID(int peerID) {
        // ex. when I give client ID 1001, return the port of client 1001
        throw new UnsupportedOperationException();
    }

    public static int getFileSize() {
        // the number in Common.cfg
        throw new UnsupportedOperationException();
    }

    public static int getPieceSize() {
        // the number in Common.cfg
        throw new UnsupportedOperationException();
    }

    // Also need to read the other parts of the configs
}
