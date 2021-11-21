import java.util.List;
import java.util.Random;

public class OptimisticChokeHandler {
    public static void UnchokeRandomNeighbor() {
        List<Integer> eligibleNeighbors;

        eligibleNeighbors = ChokeHandler.getChokedNeighbors();
        eligibleNeighbors.removeAll(InterestHandler.getNonInterestedPeers());

        Random r = new Random();
        int randomEligibleNeighborIndex = r.nextInt(eligibleNeighbors.size());

        PeerProcess.unchokePeer(eligibleNeighbors.get(randomEligibleNeighborIndex));
    }
}
