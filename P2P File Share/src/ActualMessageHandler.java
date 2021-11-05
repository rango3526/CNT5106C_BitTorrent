import java.nio.ByteBuffer;

public class ActualMessageHandler {
    // I think Actual Messages are anything except Handshake messages

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
            int msgLengthInt = Integer.parseInt(msgLengthString);
            byte[] msgpayloadbytes = new byte[msgLengthInt];
            bytearray.get(msgpayloadbytes, 0, msgpayloadbytes.length);
            String msgpayloadString = new String(msgpayloadbytes);
            return msgpayloadbytes;
        }
    }
  
    public static int getMsgType(byte[] msg) {
        throw new UnsupportedOperationException();
    }
}
