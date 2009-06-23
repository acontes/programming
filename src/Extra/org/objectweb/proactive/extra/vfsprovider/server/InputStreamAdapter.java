package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


public class InputStreamAdapter implements Stream {

    private final InputStream adaptee;

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

    public long skip(int bytes) throws IOException {
        return adaptee.skip(bytes);
    }

    public void write(byte[] data) throws WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }
}
