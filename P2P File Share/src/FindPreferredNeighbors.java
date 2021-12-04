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
                System.out.print("FATAL: Preferred neighbors sleep interrupted.");
                e.printStackTrace();
            }
        }
    }

    public static List<Integer> determinePreferredNeighbors() {
		RequestHandler.clearPiecesAlreadyRequestedList();

		List<Integer> preferredNeighbors = new ArrayList<>();

		if (Bitfield.hasAllPieces) {
			// Randomly choose preferred neighbors
			List<Integer> interestedPeers = new ArrayList<>();
			for (int peerID : PeerProcess.getPeerIDList()) {
				if (peerID != PeerProcess.selfClientID && InterestHandler.peerIsInterestedInClient(peerID))
					interestedPeers.add(peerID);
			}

			Random r = new Random();
			for (int i = 0; i < ConfigReader.getNumPreferredNeighbors(); i++) {
				if (interestedPeers.isEmpty())
					break;
				int thisPeer = r.nextInt(interestedPeers.size());
				preferredNeighbors.add(interestedPeers.get(thisPeer));
				interestedPeers.remove(thisPeer);
			}

		}
		else {
			// Choose preferred neighbors based on download speeds
			List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<>();
			for (int peerID : PeerProcess.getPeerIDList()) {
				if (peerID != PeerProcess.selfClientID && InterestHandler.peerIsInterestedInClient(peerID))
				peerDownloadSpeeds.add(new PeerInfoDownloadSpeed(peerID, PeerProcess.getDownloadRateOfPeer(peerID)));
			}
			peerDownloadSpeeds.sort((a, b) -> Double.compare(b.downloadRate, a.downloadRate));
			
			List<PeerInfoDownloadSpeed> preferredNeighborSpeeds = peerDownloadSpeeds.subList(0,
			ConfigReader.getNumPreferredNeighbors() > peerDownloadSpeeds.size() ? peerDownloadSpeeds.size() : ConfigReader.getNumPreferredNeighbors());
			
			for (PeerInfoDownloadSpeed pids : preferredNeighborSpeeds) {
				preferredNeighbors.add(pids.peerID);
			}

		}

		System.out.println("************* Found preferred neighbors: ************");

		for (Integer peerID : preferredNeighbors) {
			System.out.println("Peer " + peerID);
		}

		return preferredNeighbors;
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
