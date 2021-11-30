import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Contains functions for interested and not interested signals

public class InterestHandler {
    static List<Integer> interestedPeers = new ArrayList<Integer>();
    static List<Integer> uninterestedPeers = new ArrayList<Integer>();

    public static byte [] constructInterestMessage(boolean interested) {
    	byte [] emptyByte = new byte[0];
    	if(interested = true) {
    		return ActualMessageHandler.addHeader(emptyByte, Message.INTERESTED);
    	}
    	else{
    		return ActualMessageHandler.addHeader(emptyByte, Message.UNINTERESTED);
    	}
        //throw new UnsupportedOperationException();
    }

    public static List<Integer> getUninterestedPeers() {
        return uninterestedPeers;
    }

    public static List<Integer> getInterestedPeers() {
        return interestedPeers;
    }

    public static void receivedInterestedMessage(int fromPeerID) {
        Logger.logInterestedMessageReceived(fromPeerID);
        interestedPeers.add(fromPeerID);
        if (uninterestedPeers.contains(fromPeerID))
            uninterestedPeers.remove(Integer.valueOf(fromPeerID));
    }

    public static void receivedUninterestedMessage(int fromPeerID) {
        Logger.logNotInterestedMessageReceived(fromPeerID);
        uninterestedPeers.add(fromPeerID);
        if (interestedPeers.contains(fromPeerID))
            interestedPeers.remove(Integer.valueOf(fromPeerID));
    }
}
