public class PeerDetails implements Cloneable
{
    private int peerID;
    private String hostNameOfPeer;
    private int portUsed;
    private int hasFile;

    public PeerDetails(int peerID, String hostNameOfPeer, int portUsed, int hasFile)
    {
        this.peerID = peerID;
        this.hostNameOfPeer = hostNameOfPeer;
        this.portUsed = portUsed;
        this.hasFile = hasFile;
    }

    public int getPeerID()
    {
        return peerID;
    }

    public void setPeerID(int peerID)
    {
        this.peerID = peerID;
    }

    public String getHostname()
    {
        return hostNameOfPeer;
    }

    public void setHostname(String hostname)
    {
        this.hostNameOfPeer = hostname;
    }

    public int getPort()
    {
        return portUsed;
    }

    public void setPort(int portUsed)
    {
        this.portUsed = portUsed;
    }

    public int getHasFile()
    {
        return hasFile;
    }

    public void setHasFile(int hasFile)
    {
        this.hasFile = hasFile;
    }

    public void printPeerDetails()
    {
        System.out.println("ID: " + peerID);
        System.out.println("Host name: " + hostNameOfPeer);
        System.out.println("Port: " + portUsed);
        System.out.println("Has file: " + hasFile);
    }

    @Override
    public PeerDetails clone()
    {
        try
        {
            return (PeerDetails) super.clone();
        } 
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
