import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FindPreferredNeighbors extends Thread {

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

		try {
			while (PeerProcess.isRunning) {
				System.out.println(Logger.getTimestamp() + ": Calculating preferred neighbors...");
				PeerProcess.setPreferredNeighbors(determinePreferredNeighbors());
				try {
					Thread.sleep(ConfigReader.getUnchokingInterval()*1000);
				} catch (InterruptedException e) {
					System.out.print("FATAL: Preferred neighbors sleep interrupted.");
					e.printStackTrace();
					break;
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error in FinderPreferredNeighbors");
			e.printStackTrace();
		}
		finally {
			System.out.println("FindPreferredNeighbors stopped.");
		}
		System.out.println("FindPreferredNeighbors stopped (outside loop)");
    }

    public static List<Integer> determinePreferredNeighbors() {

		System.out.println("CPN 0");
		
		RequestHandler.clearPiecesAlreadyRequestedList();
		System.out.println("CPN 1");
		
		List<Integer> preferredNeighbors = new ArrayList<>();
		System.out.println("CPN 2");
		
		if (Bitfield.hasAllPieces) {
			System.out.println("CPN H 0");
			// Randomly choose preferred neighbors
			List<Integer> interestedPeers = new ArrayList<>();
			for (int peerID : PeerProcess.getPeerIDList()) {
				if (peerID != PeerProcess.selfClientID && InterestHandler.peerIsInterestedInClient(peerID))
				interestedPeers.add(peerID);
			}
			System.out.println("CPN H 1");
			
			Random r = new Random();
			for (int i = 0; i < ConfigReader.getNumPreferredNeighbors(); i++) {
				if (interestedPeers.isEmpty())
				break;
				int thisPeerIndex = r.nextInt(interestedPeers.size());
				preferredNeighbors.add(interestedPeers.get(thisPeerIndex));
				interestedPeers.remove(thisPeerIndex);
			}
			System.out.println("CPN H 2");
			
		}
		else {
			System.out.println("CPN NH 0");
			// Choose preferred neighbors based on download speeds
			List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<>();
			System.out.println("CPN NH 0.5");
			for (int peerID : PeerProcess.getPeerIDList()) {
				if (peerID != PeerProcess.selfClientID && InterestHandler.peerIsInterestedInClient(peerID)) {
					peerDownloadSpeeds.add(new PeerInfoDownloadSpeed(peerID, PeerProcess.getDownloadRateOfPeer(peerID)));
				}
			}
			System.out.println("CPN NH 1");
			peerDownloadSpeeds.sort((a, b) -> Double.compare(b.downloadRate, a.downloadRate));
			System.out.println("CPN NH 2");
			
			List<PeerInfoDownloadSpeed> preferredNeighborSpeeds = peerDownloadSpeeds.subList(0,
			ConfigReader.getNumPreferredNeighbors() > peerDownloadSpeeds.size() ? peerDownloadSpeeds.size() : ConfigReader.getNumPreferredNeighbors());
			System.out.println("CPN NH 3");
			
			for (PeerInfoDownloadSpeed pids : preferredNeighborSpeeds) {
				preferredNeighbors.add(pids.peerID);
			}
			System.out.println("CPN NH 4");

		}

		System.out.println(Logger.getTimestamp() + ": ************* Found preferred neighbors: ************");

		for (Integer peerID : preferredNeighbors) {
			System.out.println(Logger.getTimestamp() + ": Peer " + peerID);
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
