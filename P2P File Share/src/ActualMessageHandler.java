
public class ActualMessageHandler {
    // I think Actual Messages are anything except Handshake messages

    // TODO: Tre'

    public static byte[] constructBitfieldMessage(byte[] bitfield) {
        // this one is just adding the header (length + message type) to the payload (payload is just the bitfield)
        throw new UnsupportedOperationException();
    }

    public static byte[] constructChokeMessage(int peerID, boolean choke) { // if choke is false, then unchoke
        throw new UnsupportedOperationException();
    }

    public static byte[] extractPayload(byte[] fullMessage) {
        throw new UnsupportedOperationException();
    }
}
