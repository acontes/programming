package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.IOException;

import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


/**
 * Interface defining a set of operations that can be performed on a file stream from
 * {@link FileSystemServerImpl}.
 * <p>
 * Implementations of this interface adapt variety of streams, hence
 * {@link WrongStreamTypeException} is thrown when particular operation is not supported. Methods
 * without {@link WrongStreamTypeException} in their <code>throws</code> clause must be supported by
 * each implementation.
 */
public interface Stream {

    public abstract void close() throws IOException;

    public abstract long getLength() throws IOException, WrongStreamTypeException;

    public abstract long getPosition() throws IOException, WrongStreamTypeException;

    public abstract byte[] read(int bytes) throws IOException, WrongStreamTypeException;

    public abstract void seek(long position) throws IOException, WrongStreamTypeException;

    public abstract long skip(long bytes) throws IOException, WrongStreamTypeException;

    public abstract void write(byte[] data) throws IOException, WrongStreamTypeException;

    public abstract void flush() throws IOException, WrongStreamTypeException;
}
