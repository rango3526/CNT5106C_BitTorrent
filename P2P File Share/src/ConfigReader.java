import java.io.File;
import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.*;

public class ConfigReader 
{
    // TODO: Tre'
    //Paths may be incorrect...
    static String pathToPeerInfoCfg = "../config/PeerInfo.cfg";
    static String pathToCommonCfg = "../config/Common.cfg";

    public static List<Integer> getAllPeerIDs()
    {
        List<Integer> peerIDList = new ArrayList<>();
    	//Return list of client IDs (i.e. 1001, 1002, 1003, etc.)
        try {
            int peerID;
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
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
        catch (Exception e) {
            // TODO: Handle this
            System.out.println("Config file path was incorrect");
        }
        return peerIDList;
    }

    public static InetAddress getIPFromPeerID(int peerID) throws IOException
    {
            //Given a peerID, return the IP of that client (i.e. peerID = 1001, return IP of client 1001)
            InetAddress peerIP = null;
            String peer = "";
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            while(scan.hasNextLine()) 
            {
                peer = scan.nextLine();
                String [] placeHolder = peer.split("\\s+");
                peerIP = InetAddress.getByName(placeHolder[1]);
            }
            scan.close();
            return peerIP;
    }

    public static int getPortFromPeerID(int peerID) throws IOException
    {
            //Given a peerID, return the port of that client (i.e. peerID = 1001, return port of client 1001)
            int peerPort = 0;
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            String info = "";
            while(scan.hasNextLine())
            {
                info = scan.nextLine();
                String [] placeHolder = info.split("\\s+");
                peerPort = Integer.parseInt(placeHolder[2]);
            }
            scan.close();
            return peerPort;
    }
    
    public static int getStateFromPeerID(int peerID) throws IOException
    {
            //Given a peerID, return the state of that client (i.e. peerID = 1001, return state of client 1001 which will be 0 or 1)
            int state = 0;
            Scanner scan = new Scanner(new File(pathToPeerInfoCfg));
            String info = "";
            while(scan.hasNextLine())
            {
                info = scan.nextLine();
                String [] placeHolder = info.split("\\s+");
                state = Integer.parseInt(placeHolder[3]);
            }
            scan.close();
            return state;
    }

    public static int getNumPreferredNeighbors() throws IOException
    {
    	//Get number of preferred neighbors from Common.cfg
    	int numOfPreferredNeighbors = 0;
    	Scanner scan = new Scanner(new File(pathToCommonCfg));
    	String file = scan.nextLine();
    	String [] neighbors = file.split("\\s+");
    	numOfPreferredNeighbors = Integer.parseInt(neighbors[1]);
    	
    	scan.close();
    	return numOfPreferredNeighbors;
    }
    
    public static int getUnchokingInterval() throws IOException
    {
    	//Get the unchoking interval from Common.cfg
    	int unchokeInterval = 0;
    	Scanner scan = new Scanner(new File(pathToCommonCfg));
    	scan.nextLine();
    	String file = scan.nextLine();
    	String [] unchoke = file.split("\\s+");
    	unchokeInterval = Integer.parseInt(unchoke[1]);
    	
    	scan.close();
    	return unchokeInterval;
    }
    
    public static int getOptimisticUnchokingInterval() throws IOException
    {
    	//Get the optimistic unchoking interval from Common.cfg
    	int optimisticUnchokeInterval = 0;
    	Scanner scan = new Scanner(new File(pathToCommonCfg));
    	for(int i = 0; i <= 1; i++)
    	{
    		scan.nextLine();
    	}
    	String file = scan.nextLine();
    	String [] optimisticUnchoke = file.split("\\s+");
    	optimisticUnchokeInterval = Integer.parseInt(optimisticUnchoke[1]);
    	
    	scan.close();
    	return optimisticUnchokeInterval;
    }
    
    public static String getFileName() throws IOException
    {
    	//Get the name of the file from Common.cfg
    	Scanner scan = new Scanner(new File(pathToCommonCfg));
    	for(int i = 0; i <= 2; i++)
    	{
    		scan.nextLine();
    	}
    	String fileName = scan.nextLine();
    	String [] unchoke = fileName.split("\\s+");
    	fileName = unchoke[1];
    	
    	scan.close();
    	return fileName;
    }
    
    public static int getFileSize() throws IOException
    {
        //Get the specified size of the file from Common.cfg
    	int fileSize = 0;
    	Scanner scan = new Scanner(new File(pathToCommonCfg));
    	for(int i = 0; i <= 3; i++)
    	{
    		scan.nextLine();
    	}
    	String file = scan.nextLine();
    	String [] size = file.split("\\s+");
    	fileSize = Integer.parseInt(size[1]);
    	
    	scan.close();
    	return fileSize;
    }

    public static int getPieceSize() throws IOException
    {
    	//Get the specified piece size of the file from Common.cfg
    	int pieceSize = 0;
    	Scanner scan = new Scanner(new File(pathToCommonCfg));
    	for(int i = 0; i <= 4; i++)
    	{
    		scan.nextLine();
    	}
    	String file = scan.nextLine();
    	String [] size = file.split("\\s+");
    	pieceSize = Integer.parseInt(size[1]);
    	
    	scan.close();
    	return pieceSize;
    }
}
