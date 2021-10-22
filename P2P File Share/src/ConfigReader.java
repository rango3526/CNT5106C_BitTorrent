
import java.io.File;
import java.util.*;
import java.io.*;
import java.net.InetAddress;

public class ConfigReader {
    // TODO: Tre'


    String pathToCommonCfg = "../config/Common.cfg";        // these paths might be wrong, not sure about the double . at the start
    String pathToPeerInfoCfg = "../config/PeerInfo.cfg";

    public static List<Integer> getAllPeerIDs() throws IOException
    {
    	//Return list of client IDs (i.e. 1001, 1002, 1003, etc.)
    	int peerID;
    	List<Integer> peerIDList = new ArrayList<>();
    	Scanner scan = new Scanner(new File("PeerInfo.cfg"));
    	String info = "";
    	while(scan.hasNextLine())
    	{
    		info = scan.nextLine();
    		String [] placeHolder = info.split("\\s+");
    		peerID = Integer.parseInt(placeHolder[0]);
    		
    		peerIDList.add(peerID);
    	}
    	scan.close();
    	return peerIDList;
    }

    public static List<String> getIPFromPeerID(int peerID) throws IOException
    {
    	//Given a peerID, return the IP of that client (i.e. peerID = 1001, return IP of client 1001)
    	InetAddress peerIP;
    	String peer = "";
    	List<String> peerIPList = new ArrayList<>();
    	Scanner scan = new Scanner(new File("PeerInfo.cfg"));
    	while(scan.hasNextLine()) 
    	{
    		peer = scan.nextLine();
    		String [] placeHolder = peer.split("\\s+");
    		peerIP = InetAddress.getByName(placeHolder[1]);
    		
    		peerIPList.add(peerIP.getHostAddress());
    	}
    	scan.close();
    	return peerIPList;
    }

    public static List<Integer> getPortFromPeerID(int peerID) throws IOException
    {
    	//Given a peerID, return the port of that client (i.e. peerID = 1001, return port of client 1001)
    	int peerPort;
    	List<Integer> peerPortList = new ArrayList<>();
    	Scanner scan = new Scanner(new File("PeerInfo.cfg"));
    	String info = "";
    	while(scan.hasNextLine())
    	{
    		info = scan.nextLine();
    		String [] placeHolder = info.split("\\s+");
    		peerPort = Integer.parseInt(placeHolder[2]);
    		
    		peerPortList.add(peerPort);
    	}
    	scan.close();
    	return peerPortList;
    }

    public static int getFileSize() {
        // the number in Common.cfg
        throw new UnsupportedOperationException();
    }

    public static int getPieceSize() {
        // the number in Common.cfg
        throw new UnsupportedOperationException();
    }

    public static int getNumPreferredNeighbors() {
        throw new UnsupportedOperationException();
    }
    // Also need to read the other parts of the configs
}
