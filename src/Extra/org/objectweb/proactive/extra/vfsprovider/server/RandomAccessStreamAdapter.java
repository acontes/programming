package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


public class RandomAccessStreamAdapter implements Stream {

    private final RandomAccessFile randomFile;

    private final boolean writable;

    public static Stream createRandomAccessRead(File file) throws FileNotFoundException {
        return new RandomAccessStreamAdapter(file, false);
    }

    public static Stream createRandomAccessReadWrite(File file) throws FileNotFoundException {
        return new RandomAccessStreamAdapter(file, true);
    }

    private RandomAccessStreamAdapter(File file, boolean writable) throws FileNotFoundException {
        final String mode = writable ? "rw" : "r";

        this.randomFile = new RandomAccessFile(file, mode);
        this.writable = writable;
    }

    synchronized public void close() throws IOException {
        randomFile.close();
    }

    synchronized public long getLength() throws IOException {
        return randomFile.length();
    }

    synchronized public long getPosition() throws IOException {
        return randomFile.getFilePointer();
    }

    synchronized public byte[] read(int bytes) throws IOException, WrongStreamTypeException {
        final byte[] data = new byte[bytes];
        final int count = randomFile.read(data);

        if (count == -1)
            return null;
        return data;
    }

    synchronized public void seek(long position) throws IOException {
        randomFile.seek(position);
    }

    synchronized public long skip(int bytes) throws IOException {
        return randomFile.skipBytes(bytes);
    }

    synchronized public void write(byte[] data) throws IOException, WrongStreamTypeException {
        if (!writable)
            throw new WrongStreamTypeException();

        randomFile.write(data);
    }
}
