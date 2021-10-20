import java.util.*;

public class ChokeHandler implements Runnable
{
    private final Peer currentPeer;
    private final Thread thread;
    private final OptimisticChokeThread optimisticChokeThread;

    public ChokeHandler(Peer currentPeer)
    {
        this.currentPeer = currentPeer;
        thread = new Thread(this);
        optimisticChokeThread = new OptimisticChokeThread(currentPeer);
    }

    @Override
    public void run()
    {
        while (!thread.isInterrupted())
        {
            try
            {
                Thread.sleep(peerProcess.UnchokingInterval * 1000);

                //Get data from Peer
                ArrayList<Double> rate = new ArrayList<>(currentPeer.getPeerList().size() - 1);
                ArrayList<Integer> validID = new ArrayList<>(currentPeer.getPeerList().size() - 1);
                List<PeerDetails> peerList = currentPeer.getPeerList();

                for(PeerDetails peerInfo : peerList)
                {
                    int idOfPeer = peerInfo.getPeerID();
                    //Interested peer that is not yourself
                    if(idOfPeer != currentPeer.getPeerID() && currentPeer.checkInterestedNeighbor(idOfPeer))
                    {
                        rate.add(currentPeer.getDownloadRate(idOfPeer));
                        validID.add(idOfPeer);
                    }
                    else if(idOfPeer != currentPeer.getPeerID()) //Choke uninterested peers
                    {
                        currentPeer.setPreferredNeighbor(idOfPeer, false);
                    }
                }

                if(validID.size() == 0) 
                {	
                	continue;
                }

                int minSelectionOfNeighbors = Math.min(peerProcess.NumOfPreferredNeighbors, validID.size());

                ArrayList<Integer> largestIndex;
                if(!currentPeer.getHasFile())
                {
                    //Report indexes of K highest download rates
                    largestIndex = highestDownloadRate(rate, minSelectionOfNeighbors);
                }
                else
                {
                    //Random indexes from minimum selection of neighbors
                    Random index = new Random();
                    largestIndex = new ArrayList<>(minSelectionOfNeighbors);

                    while(largestIndex.size() < minSelectionOfNeighbors)
                    {
                        int currentIndex = index.nextInt(validID.size());
                        if(!largestIndex.contains(currentIndex))
                        {
                        	largestIndex.add(currentIndex);
                        }
                    }
                }

                //Keep the preferred neighbor updated
                boolean[] isPreferred = new boolean[validID.size()];
                Arrays.fill(isPreferred, false);
                for(int index : largestIndex)
                {
                	isPreferred[index] = true;
                }

                StringBuilder logPreferredNeighbor = new StringBuilder();
                for(int i = 0; i < validID.size(); i++)
                {
                    currentPeer.setPreferredNeighbor(validID.get(i), isPreferred[i]);
                    if(isPreferred[i])
                    {
                    	logPreferredNeighbor.append(", ").append(validID.get(i));
                    }
                }
                Log.println("Peer "+ currentPeer.getPeerID() + " has the preferred neighbors " + logPreferredNeighbor.substring(2));
            } 
            catch(InterruptedException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

    private ArrayList<Integer> highestDownloadRate(ArrayList<Double> rate, int k)
    {
        Double[] rates = rate.toArray(new Double[0]);

        ArrayList<Integer> result = new ArrayList<>(k);
        int lengthOfArray = rates.length;
        
        //Set up an index array
        int[] rateIndex = new int[lengthOfArray];
        for(int i = 0; i < lengthOfArray; i++)
        {
            rateIndex[i] = i;
        }

        if(lengthOfArray > k)
        {
            //Create heap
            for(int i = lengthOfArray / (2 - 1); i >= 0; i--)
            {
            	createHeap(rates, rateIndex, lengthOfArray, i);
            }

            //Pop an element from the heap one at a time
            for(int i = lengthOfArray - 1; i >= lengthOfArray - k; i--)
            {
                result.add(rateIndex[0]);
                
                //Place current root at the end
                Double swap = rates[0];
                rates[0] = rates[i];
                rates[i] = swap;
                int swapIndex = rateIndex[0];
                rateIndex[0] = rateIndex[i];
                rateIndex[i] = swapIndex;

                //From the reduced heap, create a new heap
                createHeap(rates, rateIndex, i, 0);
            }
        } 
        else
        {
            for(int i = lengthOfArray - 1; i >= 0; i--)
            {
                result.add(rateIndex[i]);
            }
        }
        return result;
    }

    private static void createHeap(Double[] rates, int[] rateIndex, int sizeOfHeap, int rootIndex)
    {
        int largest = rootIndex; //Set largest index to root
        int leftOfHeap = 2 * rootIndex + 1;
        int rightOfHeap = 2 * rootIndex + 2;

        //Left child is larger than root of heap
        if(leftOfHeap < sizeOfHeap && rates[leftOfHeap] > rates[largest])
        {
        	largest = leftOfHeap;
        }

        //Right child is largest to this point
        if(rightOfHeap < sizeOfHeap && rates[rightOfHeap] > rates[largest])
        {
        	largest = rightOfHeap;
        }

        //Largest isn't the root
        if(largest != rootIndex)
        {
            Double swap = rates[rootIndex];
            rates[rootIndex] = rates[largest];
            rates[largest] = swap;
            int swapIndex = rateIndex[rootIndex];
            rateIndex[rootIndex] = rateIndex[largest];
            rateIndex[largest] = swapIndex;

            //Continuously create a heap from the above sub-tree (recursion)
            createHeap(rates, rateIndex, sizeOfHeap, largest);
        }
    }

    //Start thread and optimisticChoke
    public void start()
    {
        thread.start();
        optimisticChokeThread.start();
    }

    //Exit
    public void exit()
    {
        thread.interrupt();
        optimisticChokeThread.exit();
        try
        {
            thread.join();
        } 
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

class OptimisticChokeThread implements Runnable
{
    private final Peer currentPeer;
    private final Thread thread;
    private final Random random = new Random();

    public OptimisticChokeThread(Peer thisPeer)
    {
        this.currentPeer = thisPeer;
        thread = new Thread(this);
    }

    @Override
    public void run()
    {
        List<PeerDetails> peerList = currentPeer.getPeerList();
        while (!thread.isInterrupted())
        {
            try
            {
                Thread.sleep(peerProcess.OptimisticUnchokingInterval * 1000);

                int previousID = currentPeer.getOptimistUnchoke();
                int neighborID = previousID;
                Set<Integer> verifiedNeighbor = new HashSet<>(peerList.size());

                while(neighborID == currentPeer.getPeerID() || neighborID == previousID
                        || currentPeer.checkPreferredNeighbor(neighborID) || !currentPeer.checkInterestedNeighbor(neighborID))
                {
                    verifiedNeighbor.add(neighborID);
                    if(verifiedNeighbor.size() == peerList.size())
                    {
                        neighborID = -1;
                        break;
                    }
                    neighborID = peerList.get(random.nextInt(peerList.size())).getPeerID();
                }

                if(neighborID != -1)
                {
                    currentPeer.setOptimistUnchoke(neighborID);
                }
            } 
            catch(InterruptedException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

    //Start this thread
    public void start()
    {
        thread.start();
    }

    //Exit
    public void exit()
    {
        thread.interrupt();
        try
        {
            thread.join();
        } 
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
