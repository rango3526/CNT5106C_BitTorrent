import java.util.*;

public class RequestHandler 
{
	static BitSet bitsWeHaveAlreadyRequested;

	/*
	 * public static byte[] convertIntToBytes(int pieceIndex) { return new byte[] {
	 * (byte)((pieceIndex >> 24) & 0xff), (byte)((pieceIndex >> 16) & 0xff),
	 * (byte)((pieceIndex >> 8) & 0xff), (byte)((pieceIndex >> 0) & 0xff), }; }
	 */
	/*
	 * public static int findNeededPieceIndex() { BitSet ourBitfield =
	 * Bitfield.getSelfBitfield();
	 * 
	 * for(int i = 0; i < ourBitfield.length(); i++) { if((ourBitfield == 0) &&
	 * (bitsWeHaveAlreadyRequested[i] == 0)) { bitsWeHaveAlreadyRequested[i] = 1;
	 * return i; } } return -1; // there are no pieces left, we should have all
	 * pieces (or we have all except ones we've requested) }
	 */

	
	  public static int findNeededPieceIndex() 
	  { 
		  BitSet ourBitfield = Bitfield.getSelfBitfield();
	  
		  for(int i = ourBitfield.nextSetBit(0); i >= 0; i = ourBitfield.nextSetBit(i + 1)) 
		  {
			  if(i == Integer.MAX_VALUE) 
			  { 
				  return i; 
			  }
		  } 
		  return -1; // there are no pieces left, we should have all pieces (or we have all except ones we've requested) 
	  }
	 
	
	public static byte [] constructRequestMessage(int pieceIndex) 
	{
	    pieceIndex = findNeededPieceIndex();

	    byte [] newPieceIndex = ActualMessageHandler.convertIntToBytes(pieceIndex);
	    byte [] message = ActualMessageHandler.addHeader(newPieceIndex, Message.REQUEST);
	    
	    return message;
	}

    public static void receivedRequestMessage(int peerID, byte[] msgPayload) 
    {
        throw new UnsupportedOperationException();
    }
}