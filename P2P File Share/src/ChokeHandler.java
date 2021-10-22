
import java.util.*;

public class ChokeHandler
{   
	public static List<Integer> DeterminePreferredNeighbors()
	{
		List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<PeerInfoDownloadSpeed>();
		for(int peerID : PeerProcess.GetPeerIDList())
		{
			peerDownloadSpeeds.add(new PeerInfoDownloadSpeed(peerID, PeerProcess.GetDownloadRateOfPeer(peerID)));
		}
		peerDownloadSpeeds.sort((a,b) -> Double.compare(b.downloadRate, a.downloadRate));
		
		List<PeerInfoDownloadSpeed> preferredNeighbors = peerDownloadSpeeds.subList(0, ConfigReader.getNumPreferredNeighbors());

        	List<Integer> peerIDs = new ArrayList<>();
        	for (PeerInfoDownloadSpeed pids : preferredNeighbors) 
		{
            		peerIDs.add(pids.peerID);
        	}

        	return peerIDs;
	}
	
	public static List<Integer> GetChokedNeighbors() 
	{
        	throw new UnsupportedOperationException();
    	}

	static class PeerInfoDownloadSpeed
	{
		int peerID;
		double downloadRate;
		
		PeerInfoDownloadSpeed(int peerID, double downloadRate)
		{
			this.peerID = peerID;
			this.downloadRate = downloadRate;
		}
	}
}
