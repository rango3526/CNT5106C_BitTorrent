import java.util.List;
import java.util.Random;

public class OptimisticUnchokeHandler extends Thread {

    private static volatile int optimisticallyUnchokedNeighbor = -1;

    @Override
    public void run() {
        while (PeerProcess.isRunning) {
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
        
        if (eligibleNeighbors.isEmpty()) {
            // optimisticallyUnchokedNeighbor = -1;
            // Unsure if this ^^^ is needed
            System.out.println("No neighbors eligible for optimistic unchoke");
            return;
        }
            
        Random r = new Random();
        int randomEligibleNeighborIndex = r.nextInt(eligibleNeighbors.size());

        PeerProcess.unchokePeer(eligibleNeighbors.get(randomEligibleNeighborIndex));
        optimisticallyUnchokedNeighbor = eligibleNeighbors.get(randomEligibleNeighborIndex);
        System.out.println("Peer " + eligibleNeighbors.get(randomEligibleNeighborIndex) + " OPTIMISTICALLY UNCHOKED");
    }

    public static synchronized int getOptimisticallyUnchokedNeighbor() {
        return optimisticallyUnchokedNeighbor;
    }
}
