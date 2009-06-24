package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.IOException;

import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;
import org.objectweb.proactive.extra.vfsprovider.protocol.StreamOperations;


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

    /**
     * @throws IOException
     * @see {@link StreamOperations#streamClose(long)}
     */
    public abstract void close() throws IOException;

    /**
     * @return
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamGetLength(long)}
     */
    public abstract long getLength() throws IOException, WrongStreamTypeException;

    /**
     * @return
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamGetPosition(long)}
     */
    public abstract long getPosition() throws IOException, WrongStreamTypeException;

    /**
     * @param bytes
     * @return
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamRead(long, int)}
     */
    public abstract byte[] read(int bytes) throws IOException, WrongStreamTypeException;

    /**
     * @param position
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamSeek(long, long)}
     */
    public abstract void seek(long position) throws IOException, WrongStreamTypeException;

    /**
     * @param bytes
     * @return
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamSkip(long, long)}
     */
    public abstract long skip(long bytes) throws IOException, WrongStreamTypeException;

    /**
     * @param data
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamWrite(long, byte[])}
     */
    public abstract void write(byte[] data) throws IOException, WrongStreamTypeException;

    /**
     * @throws IOException
     * @throws WrongStreamTypeException
     * @see {@link StreamOperations#streamFlush(long)}
     */
    public abstract void flush() throws IOException, WrongStreamTypeException;
}
