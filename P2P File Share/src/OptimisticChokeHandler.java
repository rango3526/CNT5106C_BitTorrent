import java.util.List;
import java.util.Random;

public class OptimisticChokeHandler {
    public static void unchokeRandomNeighbor() {
        List<Integer> eligibleNeighbors;

        eligibleNeighbors = ChokeHandler.getChokedNeighbors();
        eligibleNeighbors.removeAll(InterestHandler.getUninterestedPeers());

        Random r = new Random();
        int randomEligibleNeighborIndex = r.nextInt(eligibleNeighbors.size());

        PeerProcess.unchokePeer(eligibleNeighbors.get(randomEligibleNeighborIndex));
    }
}
