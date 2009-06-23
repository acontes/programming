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


// TODO paths..
//TODO auto close of opened and unused streams
// TODO finish javadoc..
/**
 * Implements remote file system protocol defined in {@link FileSystemServer} interface.
 * <p>
 * File stream related operations are delegated to particular {@link Stream} implementation, created
 * by {@link StreamFactory} private inner class basing on {@link StreamMode}.
 * <p>
 * Operations performed on {@link #streams} map are synchronized on <code>this</code> lock. To
 * fulfill protocol's thread-safety, an explicit {@link Stream} operations synchronization is
 * required with double checking if map contains an open stream.
 * 
 * @see FileSystemServer
 * @see Stream
 */
public class FileSystemServerImpl implements FileSystemServer {

    private final Map<Long, Stream> streams = new HashMap<Long, Stream>();

    private long idGenerator = 0;

    public long streamOpen(String path, StreamMode mode) throws IOException {
        final File file = resolvePath(path);
        final Stream instance = StreamFactory.createStreamInstance(file, mode);
        return storeStream(instance);
    }

    public void streamClose(long stream) throws IOException, StreamNotFoundException {
        final Stream instance = tryGetAndRemoveStreamOrWound(stream);
        synchronized (instance) {
            instance.close();
        }
    }

    public long streamGetLength(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        final Stream instance = tryGetStreamOrWound(stream);
        synchronized (instance) {
            checkContainsStreamOrWound(stream);
            return instance.getLength();
        }
    }

    public long streamGetPosition(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        final Stream instance = tryGetStreamOrWound(stream);
        synchronized (instance) {
            checkContainsStreamOrWound(stream);
            return instance.getPosition();
        }
    }

    public byte[] streamRead(long stream, int bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        final Stream instance = tryGetStreamOrWound(stream);
        synchronized (instance) {
            checkContainsStreamOrWound(stream);
            return instance.read(bytes);
        }
    }

    public void streamSeek(long stream, long position) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        final Stream instance = tryGetStreamOrWound(stream);
        synchronized (instance) {
            checkContainsStreamOrWound(stream);
            instance.seek(position);
        }
    }

    public long streamSkip(long stream, long bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        final Stream instance = tryGetStreamOrWound(stream);
        synchronized (instance) {
            checkContainsStreamOrWound(stream);
            // FIXME: do it better than casting, which is only a hotfix to make it compile
            return instance.skip((int) bytes);
        }
    }

    public void streamWrite(long stream, byte[] data) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        final Stream instance = tryGetStreamOrWound(stream);
        synchronized (instance) {
            checkContainsStreamOrWound(stream);
            instance.write(data);
        }
    }

    public void streamFlush(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        // TODO Auto-generated method stub
    }

    public boolean fileCreate(String path, FileType type) throws IOException {
        final File file = resolvePath(path);
        switch (type) {
            case DIRECTORY:
                return file.mkdir();
            case FILE:
                return file.createNewFile();
        }
        return false;
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

    public boolean fileSetLastModifiedTime(String path, long time) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return false;
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

    synchronized private void checkContainsStreamOrWound(long stream) throws StreamNotFoundException {
        if (!streams.containsKey(stream))
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
