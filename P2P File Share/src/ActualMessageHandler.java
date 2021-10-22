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
        ByteBuffer bytearray = ByteBuffer.wrap(fullMessage);
		byte[] msglengthbytes = new byte[4];
    	byte[] msgtypebytes = new byte[1];
        bytearray.get(msglengthbytes, 0, msglengthbytes.length);
        String msgLengthString = new String(msglengthbytes);
		String msgtypeString = new String(msgtypebytes);
        if (msgLengthString == "1") {
            byte [] msgpayloadbytes = new byte[0];
            return msgpayloadbytes;

        } else {
            msgLengthInt = Integer.parseInt(msgLengthString);
            byte[] msgpayloadbytes = new byte[msgLengthInt];
            bytearray.get(msgpayloadbytes, 0, msgpayloadbytes.length);
            String msgpayloadString = new String(msgpayloadbytes);
            return msgpayloadbytes;
        }
    	
		
    }
  
    public static int getMsgType(byte[] msgType) {
        throw new UnsupportedOperationException();
    }

    public static byte[] constructHaveMessage(byte[] pieceIndexByteArray) {
        throw new UnsupportedOperationException();
    }
}
