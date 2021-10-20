public class Handshake {

	// TODO: David

	private final String header = "P2PFILESHARINGPROJ";
    private final String bits = "0000000000";
    private int peerId;
	
	Handshake() {

	}

	public Handshake(int peerId) {
		this.peerId = peerId;
	}

	public byte[] sendHandshake() {
		return (header + bits + peerId).getBytes();

	}

    public static byte[] getHandshakeMessage(int fromClientID, int toPeerID) {
		throw new UnsupportedOperationException();
	}
}
