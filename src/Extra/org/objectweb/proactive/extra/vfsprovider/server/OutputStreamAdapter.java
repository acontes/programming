package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


public class OutputStreamAdapter implements Stream {

    private final OutputStream adaptee;

    public OutputStreamAdapter(File file, boolean append) throws FileNotFoundException {
        adaptee = new FileOutputStream(file, append);
    }

    public void close() throws IOException {
        adaptee.close();
    }

    public long getLength() throws IOException, WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public long getPosition() throws IOException, WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public byte[] read(int bytes) throws IOException, WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public void seek(long position) throws IOException, WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public long skip(int bytes) throws IOException, WrongStreamTypeException {
        throw new WrongStreamTypeException();
    }

    public void write(byte[] data) throws IOException, WrongStreamTypeException {
        adaptee.write(data);
    }
}
