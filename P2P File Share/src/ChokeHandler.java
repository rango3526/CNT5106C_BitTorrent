import java.util.*;

public class ChokeHandler {
	volatile static ArrayList<Integer> clientIsChokedBy = new ArrayList<>();
	volatile static ArrayList<Integer> neighborsChokedByClient = new ArrayList<>();

	public static synchronized List<Integer> getChokedNeighbors() {
		return new ArrayList<>(neighborsChokedByClient);
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
			if (requestMessage.length != 0) {
				System.out.println("Sending a REQUEST for piece from " + otherPeerID + " after just being unchoked");
				PeerProcess.sendMessageToPeer(otherPeerID, requestMessage);
			}
		}
		
		if (clientIsChokedBy.contains(otherPeerID)) {
			Logger.logUnchokedBy(otherPeerID);
			clientIsChokedBy.remove(Integer.valueOf(otherPeerID));
		}
	}

	public static byte[] constructChokeMessage(int peerID, boolean choke) { // if choke is false, then unchoke
		byte [] message = new byte[0];
		byte [] chokeMessage = null;
		if(!choke) {
        	chokeMessage = ActualMessageHandler.addHeader(message, ActualMessageHandler.UNCHOKE);
        }
        else {
        	chokeMessage = ActualMessageHandler.addHeader(message, ActualMessageHandler.CHOKE);
        }
		return chokeMessage;
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
