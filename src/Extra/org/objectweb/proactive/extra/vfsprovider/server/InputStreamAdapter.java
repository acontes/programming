package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


/**
 * Stream adapter for {@link InputStream} of a {@link File}, allowing the sequential readings.
 */
public class InputStreamAdapter implements Stream {

    private final InputStream adaptee;

    /**
     * Adapt input stream of a specified file.
     * 
     * @param file
     *            of which stream is to be open
     * @throws FileNotFoundException
     *             when specified file does not exist
     */
    public InputStreamAdapter(File file) throws FileNotFoundException {
        adaptee = new FileInputStream(file);
    }

    public void close() throws IOException {
        adaptee.close();
    }

    public long getLength() throws WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public long getPosition() throws WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public byte[] read(int bytes) throws IOException {
        final byte[] data = new byte[bytes];
        final int count = adaptee.read(data);

        if (count == -1)
            return null;
        return data;
    }

    public void seek(long position) throws WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public long skip(long bytes) throws IOException {
        return adaptee.skip(bytes);
    }

    public void write(byte[] data) throws WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public void flush() throws WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }
}
