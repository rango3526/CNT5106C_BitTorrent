import java.util.*;

public class ChokeHandler {
	public static List<Integer> DeterminePreferredNeighbors() {
		List<PeerInfoDownloadSpeed> peerDownloadSpeeds = new ArrayList<PeerInfoDownloadSpeed>();
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

	public static List<Integer> GetChokedNeighbors() {
		throw new UnsupportedOperationException();
	}

	public static void receivedChokeMessage(int otherPeerID, byte[] msgPayload) {
		throw new UnsupportedOperationException();
	}

	public static void receivedUnchokeMessage(int otherPeerID, byte[] msgPayload) {
		if (Bitfield.clientNeedsPiecesFromPeer(otherPeerID)) {
			int neededPieceIndex = Bitfield.getFirstPieceIndexNeedFromPeer(otherPeerID);
			byte[] requestMessage = RequestHandler.constructRequestMessage(neededPieceIndex);

			PeerProcess.sendMessageToPeer(otherPeerID, requestMessage);
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

	public static byte[] constructChokeMessage(int peerID, boolean choke) { // if choke is false, then unchoke
        throw new UnsupportedOperationException();
    }
}
