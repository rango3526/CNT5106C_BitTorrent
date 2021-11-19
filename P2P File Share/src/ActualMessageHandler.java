import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ActualMessageHandler 
{
    // I think Actual Messages are anything except Handshake messages
    public static byte[] extractPayload(byte[] fullMessage) 
    {
        ByteBuffer bytearray = ByteBuffer.wrap(fullMessage);
		byte[] msglengthbytes = new byte[4];
    	byte[] msgtypebytes = new byte[1];
        bytearray.get(msglengthbytes, 0, msglengthbytes.length);
        String msgLengthString = new String(msglengthbytes);
		String msgtypeString = new String(msgtypebytes);
        if (msgLengthString == "1") 
        {
            byte [] msgpayloadbytes = new byte[0];
            return msgpayloadbytes;
        } 
        else 
        {
            int msgLengthInt = Integer.parseInt(msgLengthString);
            byte[] msgpayloadbytes = new byte[msgLengthInt];
            bytearray.get(msgpayloadbytes, 0, msgpayloadbytes.length);
            String msgpayloadString = new String(msgpayloadbytes);
            return msgpayloadbytes;
        }
    }
  
    //4digit byte array
    public static byte[] convertIntToBytes(int pieceIndex) 
	{
	    return new byte[] 
	    {
	        (byte)((pieceIndex >> 24) & 0xff),
	        (byte)((pieceIndex >> 16) & 0xff),
	        (byte)((pieceIndex >> 8) & 0xff),
	        (byte)((pieceIndex >> 0) & 0xff),
	    };
	}
    
    //1 digit byte array
    public static byte[] bigIntToByteArray(int pieceIndex) 
    {
        BigInteger bigPieceIndex = BigInteger.valueOf(pieceIndex);      
        return bigPieceIndex.toByteArray();
    }
    
    public static byte[] addHeader(byte[] messagePayload, int messageType) 
    {
    	int lengthOfPayload = messagePayload.length + 1;
    	byte [] newLength = convertIntToBytes(lengthOfPayload);
    	byte [] newMessageType = bigIntToByteArray(messageType);
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	try 
    	{
			stream.write(newLength);
			stream.write(newMessageType);
	    	stream.write(messagePayload);
		} 
    	catch(IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	byte [] combined = stream.toByteArray();
    	return (combined);
    }
    
    public static int getMsgType(byte[] msg) 
    {
        throw new UnsupportedOperationException();
    }
}
