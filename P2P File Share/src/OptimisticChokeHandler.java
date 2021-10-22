
import java.util.List;
import java.util.Random;

public class OptimisticChokeHandler {
    public static void UnchokeRandomNeighbor() {
        List<Integer> eligibleNeighbors;

        eligibleNeighbors = ChokeHandler.GetChokedNeighbors();
        eligibleNeighbors.removeAll(InterestHandler.GetNonInterestedPeers());

        Random r = new Random();
        int randomEligibleNeighborIndex = r.nextInt(eligibleNeighbors.size());

        PeerProcess.UnchokePeer(eligibleNeighbors.get(randomEligibleNeighborIndex));
    }
}
