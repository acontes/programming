package org.objectweb.proactive.extra.vfsprovider.server;

import static java.util.Collections.sort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.RemoteObject;
import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileInfo;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileType;
import org.objectweb.proactive.extra.vfsprovider.protocol.StreamMode;


/**
 * Implements remote file system protocol defined in {@link FileSystemServer} interface.
 * <p>
 * File stream related operations are delegated to particular {@link Stream} implementation, created
 * by {@link StreamFactory} private inner class basing on {@link StreamMode}.
 * <p>
 * There is an auto closing of unused streams mechanism implemented, that starts when
 * {@link #startAutoClosing()} method is called and stops explicitly with {@link #stopServer()}
 * method call.
 * <p>
 * Operations performed on {@link #streams} map are synchronized trough
 * {@link Collections#synchronizedMap(Map)} implicit synchronization. To fulfill protocol's
 * thread-safety, an explicit {@link Stream} operations synchronization is required with double
 * checking if map contains an open stream. Generating unique identifiers is synchronized on
 * <code>this</code>.
 * <p>
 * To guarantee that {@link #streamFlush(long)} method throws {@link StreamNotFoundException} only
 * if stream has been closed correctly, an "in progress state" map is hold. Flush requests are
 * queued on stream instances from this map until stream is finally closed. Stream instance is put
 * to this map only if close operation is in progress, therefore all flush requests will eventually
 * return.
 * <p>
 * File managing related operations implementation base on {@link File} class.
 * 
 * @see FileSystemServer
 * @see Stream
 */
@RemoteObject
public class FileSystemServerImpl implements FileSystemServer {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.VFS_PROVIDER_SERVER);

    private static final char SEPARATOR_TO_REPLACE = File.separatorChar == '\\' ? '/' : '\\';

    public static final long STREAM_AUTOCLOSE_CHECKING_INTERVAL_MILIS = 1000 * 30;

    public static final long STREAM_OPEN_MAXIMUM_PERIOD_MILIS = 1000 * 60;

    private final Map<Long, Stream> streams = Collections.synchronizedMap(new HashMap<Long, Stream>());

    private final Map<Long, Stream> streamsToClose = Collections.synchronizedMap(new HashMap<Long, Stream>());

    private final Map<Long, Long> streamLastUsedTimestamp = Collections
            .synchronizedMap(new HashMap<Long, Long>());

    private File rootFile;

    private String rootCanonicalPath;

    private boolean serverStopped;

    private final Object serverStopLock = new Object();

    private long idGenerator = 0;

    private StreamAutocloseThread streamAutocloseThread;

    /**
     * ProActive empty non-arg constructor. <strong>Internal use only.</strong>
     */
    public FileSystemServerImpl() {
    }

    /**
     * Create an instance of {@link FileSystemServer} that has its root in <code>rootPath</code>
     * directory. To enable auto closing of unused streams call {@link #startAutoClosing()} method.
     * 
     * @param rootPath
     *            path of an existing directory that will be root for the file system
     * @throws IllegalArgumentException
     *             when specified path points to file that does not exist or is not a directory
     * @throws IOException
     *             when IO error occurred
     */
    public FileSystemServerImpl(String rootPath) throws IOException {
        rootFile = new File(rootPath);
        if (!rootFile.isDirectory())
            throw new IllegalArgumentException("Root directory does not exist");

        rootCanonicalPath = rootFile.getCanonicalPath();
    }

    /**
     * Enable auto closing of unused streams. The mechanism bases on
     * {@link #STREAM_OPEN_MAXIMUM_PERIOD_MILIS} and
     * {@link #STREAM_AUTOCLOSE_CHECKING_INTERVAL_MILIS} constant values.
     */
    public synchronized void startAutoClosing() {
        if (serverStopped)
            throw new IllegalStateException("Server has been already stopped");

        if (streamAutocloseThread == null) {
            streamAutocloseThread = new StreamAutocloseThread();
            streamAutocloseThread.start();
            logger.info("Starting autoclose feature");
        } else {
            logger.info("Autoclose feature already started");
        }
    }

    /**
     * Stop server facilities, in particular the auto closing mechanism. This method should be
     * called when server is no longer used, to release system resources (stop facilities' threads).
     */
    public synchronized void stopServer() {
        synchronized (serverStopLock) {
            serverStopped = true;
        }

        if (streamAutocloseThread != null)
            streamAutocloseThread.setToStop();
        final HashSet<Long> snapshot = new HashSet<Long>(streams.keySet());
        for (Long stream : snapshot) {
            try {
                streamClose(stream);
            } catch (IOException e) {
                logger.info("Exception while closing stream", e);
            } catch (StreamNotFoundException e) {
                // someone has just closed stream through streamClose() 
            }
        }
        logger.info("Autoclose feature stopped, all streams closed");
    }

    public long streamOpen(String path, StreamMode mode) throws IOException {
        synchronized (serverStopLock) {
            if (serverStopped)
                throw new IllegalStateException("File server has been stopped");

            final Stream instance;
            final File file = resolvePath(path);

            try {
                instance = StreamFactory.createStreamInstance(file, mode);
            } catch (SecurityException sec) {
                throw new IOException(sec);
            }
            return storeStream(instance);
        }
    }

    public void streamClose(long stream) throws IOException, StreamNotFoundException {
        final Stream instance = tryGetAndRemoveStreamOrWound(stream);
        synchronized (instance) {
            instance.close();
            streamsToClose.remove(stream);
            instance.notifyAll();
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
            return instance.skip(bytes);
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

        try {
            final Stream instance = tryGetStreamOrWound(stream);

            synchronized (instance) {
                checkContainsStreamOrWound(stream);
                instance.flush();
                return;
            }
        } catch (StreamNotFoundException notFound) {
            // be sure that a stream instance is closed successfully
            final Stream instance = streamsToClose.get(stream);

            if (instance != null)
                synchronized (instance) {
                    while (streamsToClose.containsKey(stream))
                        try {
                            instance.wait();
                        } catch (InterruptedException e) {
                        }
                }
            throw notFound;
        }
    }

    public void fileCreate(String path, FileType type) throws IOException {
        final File file = resolvePath(path);

        try {
            if (type == FileType.DIRECTORY) {
                file.mkdirs();
                checkConditionIsTrue(file.isDirectory(), "Directory creation failed");
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
                checkConditionIsTrue(file.isFile(), "File creation failed");
            }
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
    }

    public void fileDelete(String path, boolean recursive) throws IOException {
        final File file = resolvePath(path);
        final String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();

            if (recursive)
                deleteRecursive(file);
            if (file.isDirectory())
                checkConditionIsTrue(file.list().length == 0, "Unable to delete a not empty directory");
            if (!canonicalPath.equals(rootCanonicalPath)) {
                try {
                    file.delete();
                } catch (SecurityException sec) {
                    throw new IOException(sec);
                }
                checkConditionIsTrue(!file.exists(), "Unable to delete a file");
            }
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
    }

    public FileInfo fileGetInfo(String path) throws IOException {
        final File file = resolvePath(path);
        if (file.exists())
            return new FileInfoImpl(file);
        return null;
    }

    public Set<String> fileListChildren(String path) throws IOException {
        final File file = resolvePath(path);
        final String[] list;

        try {
            list = file.list();
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
        if (list == null) {
            return null;
        }
        return new HashSet<String>(Arrays.asList(list));
    }

    public Map<String, FileInfo> fileListChildrenInfo(String path) throws IOException {
        final File file = resolvePath(path);
        final File[] children;
        final Map<String, FileInfo> infos;

        try {
            children = file.listFiles();
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
        if (children == null) {
            return null;
        }
        infos = new HashMap<String, FileInfo>(children.length);
        for (int i = 0; i < children.length; i++) {
            File ch = children[i];
            infos.put(ch.getName(), new FileInfoImpl(ch));
        }
        return infos;
    }

    public void fileRename(String path, String newPath) throws IOException {
        final File src = resolvePath(path);
        final File dest = resolvePath(newPath);
        final boolean result;

        try {
            result = src.renameTo(dest);
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
        checkConditionIsTrue(result, "Failed to rename a file");
    }

    public boolean fileSetLastModifiedTime(String path, long time) throws IOException {
        final File file = resolvePath(path);
        checkConditionIsTrue(file.exists(), "Cannot set last modified time property of a not existing file");
        try {
            return file.setLastModified(time);
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
    }

    /*
     * Replace not-platform-like separators and check if path is valid.
     */
    private File resolvePath(String absolute) throws IOException {
        final String path = absolute.replace(SEPARATOR_TO_REPLACE, File.separatorChar);
        checkConditionIsTrue(path.startsWith(File.separator), "Provided path is not absolute");
        final File file = new File(rootFile, path);
        final String canonicalPath;

        try {
            canonicalPath = file.getCanonicalPath();
        } catch (SecurityException sec) {
            throw new IOException(sec);
        }
        if (!canonicalPath.startsWith(rootCanonicalPath))
            throw new IOException("Provided path is out of file system tree scope");
        return file;
    }

    private void checkConditionIsTrue(boolean condition, String message) throws IOException {
        if (!condition)
            throw new IOException(message);
    }

    synchronized private long storeStream(Stream instance) {
        final Long timestamp = System.currentTimeMillis();
        idGenerator++;
        streams.put(idGenerator, instance);
        streamLastUsedTimestamp.put(idGenerator, timestamp);
        return idGenerator;
    }

    private Stream tryGetStreamOrWound(long stream) throws StreamNotFoundException {
        final Stream instance = streams.get(stream);

        if (instance == null)
            throw new StreamNotFoundException();
        final Long timestamp = System.currentTimeMillis();
        streamLastUsedTimestamp.put(stream, timestamp);
        return instance;
    }

    private Stream tryGetAndRemoveStreamOrWound(long stream) throws StreamNotFoundException {
        final Stream instance = streams.remove(stream);

        if (instance == null)
            throw new StreamNotFoundException();
        streamsToClose.put(stream, instance);
        streamLastUsedTimestamp.remove(stream);
        return instance;
    }

    private void checkContainsStreamOrWound(long stream) throws StreamNotFoundException {
        if (!streams.containsKey(stream))
            throw new StreamNotFoundException();
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children != null)
                for (File child : file.listFiles()) {
                    deleteRecursive(child);
                    child.delete();
                }
        }
    }

    /**
     * An private inner class that plays a role of a factory for particular stream modes adapters.
     * 
     * @see StreamMode
     * @see Stream
     */
    private static class StreamFactory {
        public static Stream createStreamInstance(File file, StreamMode mode) throws FileNotFoundException,
                SecurityException {
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

    /**
     * An private inner class that is used as a thread for auto closing streams that are not used at
     * least for {@link FileSystemServerImpl#STREAM_OPEN_MAXIMUM_PERIOD_MILIS}.
     */
    private class StreamAutocloseThread extends Thread {
        private Comparator<Entry<Long, Long>> comparator = new StreamTimestampsComparator();

        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                processTimestamps();
                freeze();
            }
            logger.trace("StreamAutocloseThread thread terminates");
        }

        public void setToStop() {
            running = false;
        }

        /**
         * Take a snapshot of time stamps corresponding to last stream access and decide whether to
         * close the old streams according to
         * {@link FileSystemServerImpl#STREAM_OPEN_MAXIMUM_PERIOD_MILIS}.
         */
        private void processTimestamps() {
            final ArrayList<Entry<Long, Long>> snapshot = new ArrayList<Map.Entry<Long, Long>>(
                streamLastUsedTimestamp.entrySet());
            final long current = System.currentTimeMillis();
            sort(snapshot, comparator);

            logger.debug("Autoclose: processing streams");
            if (logger.isTraceEnabled()) {
                logger.trace("Autoclose: current time " + current);
                logger.trace("Autoclose: timestamps snapshot: " + snapshot.toString());
            }

            for (Entry<Long, Long> entry : snapshot) {
                if (logger.isTraceEnabled())
                    logger.trace("Autoclose: iterating timestamp: " + entry.getValue());
                if (current - entry.getValue() < STREAM_OPEN_MAXIMUM_PERIOD_MILIS) {
                    logger.debug("Autoclose: remaining streams are still valid, break..");
                    return;
                }
                try {
                    logger.debug("Autoclose: closing an old stream");
                    streamClose(entry.getKey());
                } catch (IOException e) {
                    logger.info("An exception when trying to autoclose an open stream", e);
                } catch (StreamNotFoundException e) {
                    // it seems someone else has already closed it
                }
            }
        }

        private void freeze() {
            long timestamp = System.currentTimeMillis() + STREAM_AUTOCLOSE_CHECKING_INTERVAL_MILIS;
            long period = STREAM_AUTOCLOSE_CHECKING_INTERVAL_MILIS;
            do {
                try {
                    sleep(period);
                } catch (InterruptedException e) {
                }
                period = timestamp - System.currentTimeMillis();
            } while (period > 0 && running);
        }

        /**
         * Comparator used to sort <code>(stream, time stamp)</code> entries according to the time
         * stamp in the ascending order.
         */
        private class StreamTimestampsComparator implements Comparator<Entry<Long, Long>> {
            public int compare(Entry<Long, Long> o1, Entry<Long, Long> o2) {
                final long t1 = o1.getValue();
                final long t2 = o2.getValue();
                if (t1 < t2)
                    return -1;
                if (t1 == t2)
                    return 0;
                return 1;
            }
        }
    }
}
