package org.objectweb.proactive.extra.forwardingv2.protocol;

import java.io.IOException;
import java.io.InputStream;


public class MessageInputStream {
    InputStream input;

    public MessageInputStream(InputStream input) {
        this.input = input;
    }

    public byte[] readMessage() throws IOException {
        byte[] arrayLength = new byte[4];

        internalRead(arrayLength, 0, 4);
        int messageLength = TypeHelper.byteArrayToInt(arrayLength, 0);

        byte[] msg = new byte[messageLength];
        for (int i = 0; i < 4; i++) {
            msg[i] = arrayLength[i];
        }

        internalRead(msg, 4, messageLength - 4);
        return msg;
    }

    private void internalRead(byte[] array, int offset, int len) throws IOException {
        int nbRead = 0;
        int retVal = 0;
        do {
            retVal = input.read(array, offset + nbRead, len - nbRead);
            if (retVal == -1) {
                throw new IOException("read EOF");
            }
            nbRead += retVal;
        } while (nbRead < len);
    }

    public void close() throws IOException {
        input.close();
    }
}
