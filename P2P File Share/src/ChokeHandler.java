import java.util.*;

public class ChokeHandler {
	static List<Integer> clientIsChokedBy = new ArrayList<Integer>();
	static List<Integer> neighborsChokedByClient = new ArrayList<Integer>();


	public static List<Integer> determinePreferredNeighbors() {
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

	public static List<Integer> getChokedNeighbors() {
		return neighborsChokedByClient;
	}

	public static void receivedChokeMessage(int otherPeerID) {
		clientIsChokedBy.add(otherPeerID);
	}

	public static void receivedUnchokeMessage(int otherPeerID) {
		if (Bitfield.clientNeedsPiecesFromPeer(otherPeerID)) {
			int neededPieceIndex = Bitfield.getFirstPieceIndexNeedFromPeer(otherPeerID);
			byte[] requestMessage = RequestHandler.constructRequestMessage(neededPieceIndex);

			PeerProcess.sendMessageToPeer(otherPeerID, requestMessage);
		}

		if (clientIsChokedBy.contains(otherPeerID)) {
			clientIsChokedBy.remove(Integer.valueOf(otherPeerID));
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
		byte [] message = null;
		byte [] chokeMessage = null;
		if(choke = false) {
        	unchokePeer(peerID);
        }
        else {
        	chokeMessage = ActualMessageHandler.addHeader(message, Message.CHOKE);
        }
		return chokeMessage;
		//throw new UnsupportedOperationException();
    }

	public static boolean chokePeer(int peerID) {
		if (neighborsChokedByClient.contains(peerID))
			return false;
		
		neighborsChokedByClient.add(peerID);
		return true;
	}

	public static boolean unchokePeer(int peerID) {
		if (!neighborsChokedByClient.contains(peerID))
			return false;
		
		neighborsChokedByClient.remove(Integer.valueOf(peerID));
		return true;
	}
}
