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


public class FileSystemServerImpl implements FileSystemServer {

    private final Map<Long, Stream> streams = new HashMap<Long, Stream>();

    private long counter = 0;

    public void streamClose(long stream) throws IOException, StreamNotFoundException {
        final Stream instance = tryGetStreamOrWound(stream);
        instance.close();
        tryRemoveStreamSilently(stream);
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
        final Stream instance = StreamFactory.createStreamInstance(path, mode);
        instance.open();
        return storeStream(instance);
    }

    public byte[] streamRead(long stream, long bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        return tryGetStreamOrWound(stream).read(bytes);
    }

    public void streamSeek(long stream, long position) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        tryGetStreamOrWound(stream).seek(position);
    }

    public long streamSkip(long stream, long bytes) throws IOException, StreamNotFoundException {
        return tryGetStreamOrWound(stream).skip(bytes);
    }

    public int streamWrite(long stream, byte[] data) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        return tryGetStreamOrWound(stream).write(data);
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
        counter++;
        streams.put(counter, instance);
        return counter;
    }

    synchronized private Stream tryGetStreamOrWound(long stream) throws StreamNotFoundException {
        if (streams.containsKey(stream))
            return streams.get(stream);
        throw new StreamNotFoundException();
    }

    synchronized private void tryRemoveStreamSilently(long stream) {
        streams.remove(stream);
    }

    // TODO implement me
    private static class StreamFactory {

        public static Stream createStreamInstance(String relative, StreamMode mode) {
            return null;
        }
    }
}
