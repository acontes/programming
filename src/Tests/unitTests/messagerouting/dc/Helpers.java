package unitTests.messagerouting.dc;

public class Helpers {

    public static final String REQUEST_PAYLOAD = "request";
    public static final String REPLY_PAYLOAD = "reply";

    public static String byteArrayToString(byte[] reply) {
        return new String(reply);
    }

    public static byte[] stringToByteArray(String request) {
        return request.getBytes();
    }
}
