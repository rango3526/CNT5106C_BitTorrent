import java.util.*;

public class ChokeHandler implements Runnable
{   
	public static void DeterminePreferredNeighbors()
	{
		List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<PeerInfoDownloadSpeed>();
		List<Integer> preferredNeigbors = new ArrayList<Integer>();
		for(int peerID : PeerProcess.getPeerIdList())
		{
			peerDownloadSpeeds.add(new PeerInfoDownloadSpeed(peerID, CalculateDownloadRate(peerID)));
		}
		peerDownloadSpeeds.sort((a,b) -> Double.compare(b.downloadRate, a.downloadRate));
		
		List<PeerInfoDownloadSpeed> preferredNeighbors = peerDownloadSpeeds.subList(0, ConfigReader.getMaxNumberOfPreferredNeighbors());
	}

	class PeerInfoDownloadSpeed
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
