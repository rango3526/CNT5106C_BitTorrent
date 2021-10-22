public class Handshake {

	// TODO: David

	private final static String header = "P2PFILESHARINGPROJ";
    private final static String bits = "0000000000";

	public static byte[] createHandshake(int peerId) {
		return (header + bits + peerId).getBytes();
	}

    public static byte[] getHandshakeMessage(int fromClientID, int toPeerID) {
		throw new UnsupportedOperationException();
	}
}
