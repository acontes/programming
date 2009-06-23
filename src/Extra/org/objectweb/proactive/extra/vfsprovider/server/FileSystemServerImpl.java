package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileInfo;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileType;
import org.objectweb.proactive.extra.vfsprovider.protocol.StreamMode;


//TODO auto close of opened and unused streams
// TODO finish javadoc..
/**
 * Implements remote file system protocol defined in {@link FileSystemServer} interface.
 * <p>
 * File stream related operations are delegated to particular {@link Stream} implementation, created
 * by {@link StreamFactory} private inner class basing on {@link StreamMode}.
 * <p>
 * Operations performed on {@link #streams} map are synchronized on <code>this</code> lock. To
 * fulfill protocol's thread-safety, an implicit {@link Stream} operations synchronization is
 * required and assumed.
 *
 * @see FileSystemServer
 */
public class FileSystemServerImpl implements FileSystemServer {

    private final Map<Long, Stream> streams = new HashMap<Long, Stream>();

    private long idGenerator = 0;

    public void streamClose(long stream) throws IOException, StreamNotFoundException {
        tryGetAndRemoveStreamOrWound(stream).close();
    }

    public long streamGetLength(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        return tryGetStreamOrWound(stream).getLength();
    }

    public long streamGetPosition(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        return tryGetStreamOrWound(stream).getPosition();
    }

    public long streamOpen(String path, StreamMode mode) throws IOException {
        final File file = resolvePath(path);
        final Stream instance = StreamFactory.createStreamInstance(file, mode);
        return storeStream(instance);
    }

    public byte[] streamRead(long stream, int bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        return tryGetStreamOrWound(stream).read(bytes);
    }

    public void streamSeek(long stream, long position) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        tryGetStreamOrWound(stream).seek(position);
    }

    public long streamSkip(long stream, int bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        return tryGetStreamOrWound(stream).skip(bytes);
    }

    public void streamWrite(long stream, byte[] data) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        tryGetStreamOrWound(stream).write(data);
    }

    public void fileCreate(String path, FileType type) throws IOException {
        // TODO Auto-generated method stub
    }

    public void fileDelete(String path, boolean recursive) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

    public FileInfo fileGetInfo(String path) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> fileListChildren(String path) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, FileInfo> fileListChildrenInfo(String path) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public void fileRename(String path, String newPath) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

    public void fileSetLastModifiedTime(String path, long time) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

    // FIXME
    private File resolvePath(String relative) {
        final String ROOT = "/";
        return new File(ROOT + relative);
    }

    synchronized private long storeStream(Stream instance) {
        idGenerator++;
        streams.put(idGenerator, instance);
        return idGenerator;
    }

    synchronized private Stream tryGetStreamOrWound(long stream) throws StreamNotFoundException {
        if (streams.containsKey(stream))
            return streams.get(stream);
        throw new StreamNotFoundException();
    }

    synchronized private Stream tryGetAndRemoveStreamOrWound(long stream) throws StreamNotFoundException {
        if (streams.containsKey(stream))
            return streams.remove(stream);
        throw new StreamNotFoundException();
    }

    /**
     * An private inner class that plays a role of a factory for particular stream modes adapters.
     *
     * @see StreamMode
     * @see Stream
     */
    private static class StreamFactory {
        public static Stream createStreamInstance(File file, StreamMode mode) throws FileNotFoundException {
            switch (mode) {
                case RANDOM_ACCESS_READ:
                    return RandomAccessStreamAdapter.createRandomAccessRead(file);
                case RANDOM_ACCESS_READ_WRITE:
                    return RandomAccessStreamAdapter.createRandomAccessReadWrite(file);
                case SEQUENTIAL_APPEND:
                    return new OutputStreamAdapter(file, true);
                case SEQUENTIAL_READ:
                    return new InputStreamAdapter(file);
                case SEQUENTIAL_WRITE:
                    return new OutputStreamAdapter(file, false);
            }
            return null;
        }
    }
}
