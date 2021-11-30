import java.util.*;

public class ChokeHandler {
	volatile static List<Integer> clientIsChokedBy = new ArrayList<Integer>();
	volatile static List<Integer> neighborsChokedByClient = new ArrayList<Integer>();

	public static synchronized List<Integer> getChokedNeighbors() {
		return neighborsChokedByClient;
	}

	public static synchronized void receivedChokeMessage(int otherPeerID) {
		if (!clientIsChokedBy.contains(otherPeerID)) {
			Logger.logChokedBy(otherPeerID);
			clientIsChokedBy.add(otherPeerID);
		}
	}

	public static synchronized void receivedUnchokeMessage(int otherPeerID) {
		if (RequestHandler.clientNeedsSomePieceFromPeer(otherPeerID)) {
			byte[] requestMessage = RequestHandler.constructRequestMessageAndChooseRandomPiece(otherPeerID);
			PeerProcess.sendMessageToPeer(otherPeerID, requestMessage);
		}
		
		if (clientIsChokedBy.contains(otherPeerID)) {
			Logger.logUnchokedBy(otherPeerID);
			clientIsChokedBy.remove(Integer.valueOf(otherPeerID));
		}
	}

	public static byte[] constructChokeMessage(int peerID, boolean choke) { // if choke is false, then unchoke
		byte [] message = null;
		byte [] chokeMessage = null;
		if(choke = false) {
        	unchokePeer(peerID);
        }
        else {
        	chokeMessage = ActualMessageHandler.addHeader(message, ActualMessageHandler.CHOKE);
        }
		return chokeMessage;
		//throw new UnsupportedOperationException();
    }

	public static synchronized boolean chokePeer(int peerID) {
		if (neighborsChokedByClient.contains(peerID))
			return false;
		
		neighborsChokedByClient.add(peerID);
		return true;
	}

	public static synchronized boolean unchokePeer(int peerID) {
		if (!neighborsChokedByClient.contains(peerID))
			return false;
		
		neighborsChokedByClient.remove(Integer.valueOf(peerID));
		return true;
	}
}
