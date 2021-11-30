import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class FindPreferredNeighbors extends Thread {

    @Override
    public void run() {
        while (true) {
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
		List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<>();
		for (int peerID : PeerProcess.getPeerIDList()) {
			peerDownloadSpeeds.add(new PeerInfoDownloadSpeed(peerID, PeerProcess.getDownloadRateOfPeer(peerID)));
		}
		peerDownloadSpeeds.sort((a, b) -> Double.compare(b.downloadRate, a.downloadRate));

		List<PeerInfoDownloadSpeed> preferredNeighbors = peerDownloadSpeeds.subList(0,
				ConfigReader.getNumPreferredNeighbors());

		List<Integer> peerIDs = new ArrayList<>();
		for (PeerInfoDownloadSpeed pids : preferredNeighbors) {
			peerIDs.add(pids.peerID);
		}

		return peerIDs;
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
