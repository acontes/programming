/**
 * 
 */
package org.objectweb.proactive.extra.vfsprovider.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;


abstract class AbstractProActiveOutputStreamAdapter extends OutputStream {
    private static final Log log = LogFactory.getLog(AbstractProActiveOutputStreamAdapter.class);

    private final byte[] SINGLE_BYTE_BUF = new byte[1];

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len + off > b.length) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        final byte bytesToSent[];
        if (off != 0 || len != b.length) {
            bytesToSent = Arrays.copyOfRange(b, off, len);
        } else {
            bytesToSent = b;
        }

        try {
            try {
                getServer().streamWrite(getStreamId(), bytesToSent);
            } catch (StreamNotFoundException e) {
                reopenStream();
                getServer().streamWrite(getStreamId(), bytesToSent);
            }
            notifyBytesWritten(bytesToSent.length);
        } catch (WrongStreamTypeException e) {
            throw Utils.generateAndLogIOExceptionWrongStreamType(log, e);
        } catch (StreamNotFoundException e) {
            throw Utils.generateAndLogIOExceptionStreamNotFound(log, e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        SINGLE_BYTE_BUF[0] = (byte) b;
        write(SINGLE_BYTE_BUF);
    }

    @Override
    public synchronized void flush() throws IOException {
        try {
            getServer().streamFlush(getStreamId());
        } catch (WrongStreamTypeException e) {
            throw Utils.generateAndLogIOExceptionWrongStreamType(log, e);
        } catch (StreamNotFoundException e) {
            // as long as FileSystemServer guarantees that this exception can occur
            // after streamOpen() only after proper close at server side,
            // we do not need to open it to flush it again - we can ignore it
        }
    }

    protected abstract long getStreamId();

    protected abstract FileSystemServer getServer();

    @Override
    public abstract void close() throws IOException;

    protected abstract void notifyBytesWritten(long bytesNumber);

    protected abstract void reopenStream() throws IOException;
}