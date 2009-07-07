package org.objectweb.proactive.extra.vfsprovider.protocol;

import java.io.IOException;

import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;


/**
 * Defines set of protocol operations that are related to a file streams. Each stream is identified
 * by an id of a <code>long</code> type, that is returned by {@link #streamOpen(String, StreamMode)}
 * method call and is to be passed as an argument of the other methods.
 * <p>
 * Stream mode is set by invocation of {@link #streamOpen(String, StreamMode)} and thereby defining
 * what operations are allowed on this particular stream. When calling a not allowed method,
 * {@link WrongStreamTypeException} is thrown. Calling the methods without WrongStreamTypeException
 * in their <code>throws</code> clause are allowed on streams of an each mode.
 * <p>
 * Once a stream is open, it must be finally closed through {@link #streamClose(long)} method call.
 * This may cause that {@link StreamNotFoundException} is thrown, when trying to invoke a method on
 * already closed stream. In this case a stream needs to be reopen.
 * <p>
 * Note, that any method may throw {@link IOException} as a parameter type is not supported in the
 * native implementation provided by the platform. Implementations of this interface remain
 * transparent for such a behavior.
 * <p>
 * Implementations of this interface are thread-safe.
 */
public interface StreamOperations {

    /**
     * Open a file stream in specified <code>mode</code> that defines a subset of methods that are
     * allowed to invoke. If specified <code>path</code> points to not existing file and
     * <code>mode</code> indicates writings, that file will be created.
     * 
     * @param path
     *            of a file whose stream is to be open, cannot be <code>null</code>
     * @param mode
     *            of a stream to be open, cannot be <code>null</code>
     * @return generated stream id that may be used later in the other method calls, the id is
     *         unique within one server instance; stream identified by this id is open until
     *         {@link #streamClose(long)} method is called
     * @throws IOException
     *             when specified path points to directory, a non existing file that cannot/should
     *             not be created (e.g. reading mode specified), a stream cannot be open due to I/O
     *             error, or a security exception occurred
     */
    public abstract long streamOpen(String path, StreamMode mode) throws IOException;

    /**
     * Read number of bytes <code>bytes</code> from an open stream defined by unique id that was
     * previously returned by {@link #streamOpen(String, StreamMode)} method call.
     * 
     * @param stream
     *            an unique id of an open stream
     * @param bytes
     *            number of bytes that are to be read
     * @return an array of bytes read or <code>null</code> when an EOF occurred and nothing has been
     *         read; length of this array indicates how many bytes has been read successfully
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract byte[] streamRead(long stream, int bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Write an array of bytes into an open stream specified by an unique id that was previously
     * returned by {@link #streamOpen(String, StreamMode)} method call.
     * 
     * @param stream
     *            an unique id of an open stream
     * @param data
     *            array of bytes that is to be written, cannot be <code>null</code>
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract void streamWrite(long stream, byte[] data) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Change the position of a file stream pointer specified by an unique id that was previously
     * returned by {@link #streamOpen(String, StreamMode)} method call. This stream pointer
     * indicates the position of a next read or write into a stream. Seek operation with
     * <code>position</code> exceeding the EOF will not affect the file length, unless any write
     * operation is performed in the new position.
     * 
     * @param stream
     *            an unique id of an open stream
     * @param position
     *            an absolute position within a file, measured in bytes; cannot be negative number
     * @throws IOException
     *             if an I/O error occurred while performing this method or position is a negative
     *             number
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract void streamSeek(long stream, long position) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Return the length of a file represented by a stream that has been once open by
     * {@link #streamOpen(String, StreamMode)} method call. Stream is identified by an unique id.
     * 
     * @param stream
     *            an unique id of an open stream
     * @return length of a file represented by a stream
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract long streamGetLength(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Return the position of a file stream pointer specified by an unique id that was previously
     * returned by {@link #streamOpen(String, StreamMode)} method call. This stream pointer
     * indicates the position of a next read or write into a stream.
     * 
     * @param stream
     *            an unique id of an open stream
     * @return position of a file stream pointer
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract long streamGetPosition(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Skip and discard number of bytes <code>bytes</code> of an open stream defined by unique id
     * that was previously returned by {@link #streamOpen(String, StreamMode)} method call. From a
     * variety of reasons this method call may skip less that specified number of bytes. If
     * <code>bytes</code> is negative, no bytes are skipped.
     * 
     * @param stream
     *            an unique id of an open stream
     * @param bytes
     *            number of bytes to skip and discard
     * @return actual number of bytes skipped
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract long streamSkip(long stream, long bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Flushes an output stream specified by unique id that was previously returned by
     * {@link #streamOpen(String, StreamMode)} method call, and forces any buffered output bytes to
     * be written out.
     * <p>
     * This method guarantees, that if {@link StreamNotFoundException} is thrown, a corresponding
     * stream has been closed (and hence flushed).
     * 
     * @param stream
     *            an unique id of an open stream
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed correctly
     * @throws WrongStreamTypeException
     *             when mode of a stream does not allow to call this method
     */
    public abstract void streamFlush(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException;

    /**
     * Close a stream specified by unique id that was previously returned by
     * {@link #streamOpen(String, StreamMode)} method call.
     * 
     * @param stream
     *            an unique id of an open stream
     * @throws IOException
     *             if an I/O error occurred while performing this method
     * @throws StreamNotFoundException
     *             if specified stream unique id has not been found or it has been closed
     */
    public abstract void streamClose(long stream) throws IOException, StreamNotFoundException;
}