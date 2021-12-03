import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FindPreferredNeighbors extends Thread {

    @Override
    public void run() {
        while (PeerProcess.isRunning) {
            PeerProcess.setPreferredNeighbors(determinePreferredNeighbors());
            try {
                Thread.sleep(ConfigReader.getUnchokingInterval()*1000);
            } catch (InterruptedException e) {
                System.out.print("Preferred neighbors sleep interrupted.");
                e.printStackTrace();
            }
        }
    }

    public static List<Integer> determinePreferredNeighbors() {
		if (Bitfield.hasAllPieces) {
			// Randomly choose preferred neighbors
			List<Integer> interestedPeers = new ArrayList<>();
			for (int peerID : PeerProcess.getPeerIDList()) {
				if (peerID != PeerProcess.selfClientID && InterestHandler.peerIsInterestedInClient(peerID))
					interestedPeers.add(peerID);
			}

			List<Integer> preferredPeers = new ArrayList<>();
			Random r = new Random();
			for (int i = 0; i < ConfigReader.getNumPreferredNeighbors(); i++) {
				if (interestedPeers.isEmpty())
					break;
				int thisPeer = r.nextInt(interestedPeers.size());
				preferredPeers.add(interestedPeers.get(thisPeer));
				interestedPeers.remove(thisPeer);
			}

			return preferredPeers;
		}
		else {
			// Choose preferred neighbors based on download speeds
			List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<>();
			for (int peerID : PeerProcess.getPeerIDList()) {
				if (peerID != PeerProcess.selfClientID && InterestHandler.peerIsInterestedInClient(peerID))
					peerDownloadSpeeds.add(new PeerInfoDownloadSpeed(peerID, PeerProcess.getDownloadRateOfPeer(peerID)));
			}
			peerDownloadSpeeds.sort((a, b) -> Double.compare(b.downloadRate, a.downloadRate));

			List<PeerInfoDownloadSpeed> preferredNeighbors = peerDownloadSpeeds.subList(0,
					ConfigReader.getNumPreferredNeighbors() > peerDownloadSpeeds.size() ? peerDownloadSpeeds.size() : ConfigReader.getNumPreferredNeighbors());
	
			List<Integer> peerIDs = new ArrayList<>();
			for (PeerInfoDownloadSpeed pids : preferredNeighbors) {
				peerIDs.add(pids.peerID);
			}

			return peerIDs;
		}
	}

    static class PeerInfoDownloadSpeed {
		int peerID;
		double downloadRate;

		PeerInfoDownloadSpeed(int peerID, double downloadRate) {
			this.peerID = peerID;
			this.downloadRate = downloadRate;
		}
	}
}
