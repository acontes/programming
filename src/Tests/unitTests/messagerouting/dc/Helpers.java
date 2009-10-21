package unitTests.messagerouting.dc;

public class Helpers {

    public static final String REQUEST_PAYLOAD = "300";
    public static final String REPLY_PAYLOAD = "reply";
    public static final String ERROR_PAYLOAD = "error";

    public static String byteArrayToString(byte[] reply) {
        return new String(reply);
    }

    public static byte[] stringToByteArray(String request) {
        return request.getBytes();
    }
}
