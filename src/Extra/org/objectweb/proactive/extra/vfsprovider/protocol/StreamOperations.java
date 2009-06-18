package org.objectweb.proactive.extra.vfsprovider.protocol;

import java.io.IOException;

import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


public interface StreamOperations {

    public abstract long streamOpen(String path, StreamMode mode) throws IOException;

    public abstract byte[] streamRead(long stream, long bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    public abstract int streamWrite(long stream, byte[] data) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    public abstract void streamSeek(long stream, long position) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    public abstract long streamGetLength(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    public abstract long streamGetPosition(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    public abstract long streamSkip(long stream, long bytes) throws IOException, StreamNotFoundException;

    public abstract void streamClose(long stream) throws IOException, StreamNotFoundException;

}