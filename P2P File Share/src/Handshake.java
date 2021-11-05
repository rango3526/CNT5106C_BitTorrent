public class Handshake {

	// TODO: David

	private final static String header = "P2PFILESHARINGPROJ";
    private final static String bits = "0000000000";

	public static byte[] getHandshakeMessage(int selfClientID) {
		return (header + bits + selfClientID).getBytes();
	}
}
