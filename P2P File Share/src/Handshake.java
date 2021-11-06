import java.nio.ByteBuffer;

public class Handshake {

	// TODO: David

	private final static String header = "P2PFILESHARINGPROJ";
    private final static String bits = "0000000000";

	public static byte[] getHandshakeMessage(int selfClientID) {
		return (header + bits + selfClientID).getBytes();
	}

	public static int receivedHandshakeResponseMessage(byte[] handshakeResponseMessage) {
		ByteBuffer bytearray = ByteBuffer.wrap(handshakeResponseMessage);
		byte[] headerbytes = new byte[18];
		byte[] zerobitsbytes = new byte[10];
		byte[] peerIdbytes = new byte[4];
		bytearray.get(headerbytes, 0, headerbytes.length);
		bytearray.get(zerobitsbytes, 0, zerobitsbytes.length);
		bytearray.get(peerIdbytes, 0, peerIdbytes.length);
		String headerString = new String(headerbytes);
		String zerobitString = new String(zerobitsbytes);
		String peerIdString = new String(peerIdbytes);
		int peerIdInt = Integer.parseInt(peerIdString);

		return peerIdInt;
	}
}
