
public class Message 
{
	public static final byte CHOKE = 0;
	public static final byte UNCHOKE = 1;
	public static final byte INTERESTED = 2;
	public static final byte UNINTERESTED = 3;
	public static final byte HAVE = 4;
	public static final byte BITFIELD = 5;
	public static final byte REQUEST = 6;
	public static final byte PIECE = 7;
	
	private final int messageType;
	private final byte[] payload;
	
	public Message(int messageType, byte[] payload)
	{
		this.messageType = messageType;
		this.payload = payload;
	}
	
	public int getMessageType()
	{
		return messageType;
	}
	
	public byte[] getPayload()
	{
		return payload;
	}
}
