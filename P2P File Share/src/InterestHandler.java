import java.util.Collection;
import java.util.List;

// Contains functions for interested and not interested signals

public class InterestHandler {
    // TODO: David
    public static final int TYPE = 2;

    public static byte [] GetInterestMessage() {
        // return byte array of correct format
        throw new UnsupportedOperationException();
    }

    public static List<Integer> GetNonInterestedPeers() {
        throw new UnsupportedOperationException();
    }

    public static void receivedInterestedMessage(int fromPeerID, byte[] msgPayload) {
        throw new UnsupportedOperationException();
    }

    public static void receivedUninterestedMessage(int fromPeerID, byte[] msgPayload) {
        throw new UnsupportedOperationException();
    }
}
