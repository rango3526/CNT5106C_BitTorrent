import java.util.List;
import java.util.Random;

public class OptimisticUnchokeHandler extends Thread {

    private static volatile int optimisticallyUnchokedNeighbor = -1;

    @Override
    public void run() {
        while (true) {
            unchokeRandomNeighbor();
            try {
                Thread.sleep(ConfigReader.getOptimisticUnchokingInterval()*1000);
            } catch (InterruptedException e) {
                System.out.print("Optimistic unchoke sleep interrupted");
                e.printStackTrace();
            }
        }
    }

    public static void unchokeRandomNeighbor() {
        List<Integer> eligibleNeighbors;

        eligibleNeighbors = ChokeHandler.getChokedNeighbors();
        eligibleNeighbors.removeAll(InterestHandler.getUninterestedPeers());

        Random r = new Random();
        int randomEligibleNeighborIndex = r.nextInt(eligibleNeighbors.size());

        PeerProcess.unchokePeer(eligibleNeighbors.get(randomEligibleNeighborIndex));
        optimisticallyUnchokedNeighbor = eligibleNeighbors.get(randomEligibleNeighborIndex);
    }

    public static synchronized int getOptimisticallyUnchokedNeighbor() {
        return optimisticallyUnchokedNeighbor;
    }
}
