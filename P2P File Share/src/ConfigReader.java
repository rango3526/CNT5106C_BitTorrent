import java.io.File;
import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.*;

public class ConfigReader {
    // TODO: use a more general path
    static String pathToPeerInfoCfg = "C:/Users/Tre' Jeter/Desktop/Java Projects/Projects/Peer-To-Peer Network/src/PeerInfo.cfg";
    static String pathToCommonCfg = "C:/Users/Tre' Jeter/Desktop/Java Projects/Projects/Peer-To-Peer Network/src/Common.cfg";

    public static List<Integer> getAllPeerIDs() {
        List<Integer> peerIDList = new ArrayList<>();
        // Return list of client IDs (i.e. 1001, 1002, 1003, etc.)
        try {
            int peerID;
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            String info = "";
            while (scan.hasNextLine()) {
                info = scan.nextLine();
                String[] placeHolder = info.split("\\s+");
                peerID = Integer.parseInt(placeHolder[0]);

                peerIDList.add(peerID);
            }
            scan.close();
            return peerIDList;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file in getAllPeerIDs:\n" + e.getStackTrace());
        }
    }

    public static InetAddress getIPFromPeerID(int peerID) {
        try {
            // Given a peerID, return the IP of that client (i.e. peerID = 1001, return IP
            // of client 1001)
            @SuppressWarnings("resource")
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            String[] placeHolder = scan.nextLine().split("\\s+");
            InetAddress peerIP = null;
            while (scan.hasNextLine()) {
                if (peerID == Integer.parseInt(placeHolder[0])) {
                    peerIP = InetAddress.getByName(placeHolder[1]);
                    return peerIP;
                } else if (peerID != Integer.parseInt(placeHolder[0])) {
                    placeHolder = scan.nextLine().split("\\s+");
                    // return peerIP;
                }
            }
            scan.close();
            return peerIP;
        } catch (Exception e) {
            // System.out.println("Error reading config file in getIPFromPeerID");
            throw new RuntimeException("Error reading config file in getIPFromPeerID:\n" + e.getStackTrace());
        }
    }

    public static int getPortFromPeerID(int peerID) {
        try {
            // Given a peerID, return the port of that client (i.e. peerID = 1001, return
            // port of client 1001)
            @SuppressWarnings("resource")
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            int peerPort = 0;
            String[] placeHolder = scan.nextLine().split("\\s+");
            while (scan.hasNextLine()) {
                if (peerID == Integer.parseInt(placeHolder[0])) {
                    peerPort = Integer.parseInt(placeHolder[2]);
                    return peerPort;
                } else if (peerID != Integer.parseInt(placeHolder[0])) {
                    placeHolder = scan.nextLine().split("\\s+");
                }
            }
            scan.close();
            return peerPort;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file in getPortFromPeerID:\n" + e.getStackTrace());
        }
    }

    public static int getStateFromPeerID(int peerID) {
        try {
            // Given a peerID, return the state of that client (i.e. peerID = 1001, return
            // state of client 1001 which will be 0 or 1)
            @SuppressWarnings("resource")
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            int state = 0;
            String[] placeHolder = scan.nextLine().split("\\s+");
            while (scan.hasNextLine()) {
                if (peerID == Integer.parseInt(placeHolder[0])) {
                    state = Integer.parseInt(placeHolder[3]);
                    return state;
                } else if (peerID != Integer.parseInt(placeHolder[0])) {
                    placeHolder = scan.nextLine().split("\\s+");
                }
            }
            scan.close();
            return state;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }

    public static int getNumPreferredNeighbors() {
        try {
            // Get number of preferred neighbors from Common.cfg
            int numOfPreferredNeighbors = 0;
            Scanner scan = new Scanner(new File(pathToCommonCfg));
            String file = scan.nextLine();
            String[] neighbors = file.split("\\s+");
            numOfPreferredNeighbors = Integer.parseInt(neighbors[1]);

            scan.close();
            return numOfPreferredNeighbors;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }

    public static int getUnchokingInterval() {
        try {
            // Get the unchoking interval from Common.cfg
            int unchokeInterval = 0;
            Scanner scan = new Scanner(new File(pathToCommonCfg));
            scan.nextLine();
            String file = scan.nextLine();
            String[] unchoke = file.split("\\s+");
            unchokeInterval = Integer.parseInt(unchoke[1]);

            scan.close();
            return unchokeInterval;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }

    public static int getOptimisticUnchokingInterval() {
        try {
            // Get the optimistic unchoking interval from Common.cfg
            int optimisticUnchokeInterval = 0;
            Scanner scan = new Scanner(new File(pathToCommonCfg));
            for (int i = 0; i <= 1; i++) {
                scan.nextLine();
            }
            String file = scan.nextLine();
            String[] optimisticUnchoke = file.split("\\s+");
            optimisticUnchokeInterval = Integer.parseInt(optimisticUnchoke[1]);

            scan.close();
            return optimisticUnchokeInterval;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }

    public static String getFileName() {
        try {
            // Get the name of the file from Common.cfg
            Scanner scan = new Scanner(new File(pathToCommonCfg));
            for (int i = 0; i <= 2; i++) {
                scan.nextLine();
            }
            String fileName = scan.nextLine();
            String[] unchoke = fileName.split("\\s+");
            fileName = unchoke[1];

            scan.close();
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }

    public static int getFileSize() {
        try {
            // Get the specified size of the file from Common.cfg
            int fileSize = 0;
            Scanner scan = new Scanner(new File(pathToCommonCfg));
            for (int i = 0; i <= 3; i++) {
                scan.nextLine();
            }
            String file = scan.nextLine();
            String[] size = file.split("\\s+");
            fileSize = Integer.parseInt(size[1]);

            scan.close();
            return fileSize;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }

    public static int getPieceSize() {
        try {
            // Get the specified piece size of the file from Common.cfg
            int pieceSize = 0;
            Scanner scan = new Scanner(new File(pathToCommonCfg));
            for (int i = 0; i <= 4; i++) {
                scan.nextLine();
            }
            String file = scan.nextLine();
            String[] size = file.split("\\s+");
            pieceSize = Integer.parseInt(size[1]);

            scan.close();
            return pieceSize;
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file:\n" + e.getStackTrace());
        }
    }
}