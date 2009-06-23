package org.objectweb.proactive.extra.vfsprovider.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.util.FileObjectUtils;
import org.apache.commons.vfs.util.MonitorInputStream;
import org.apache.commons.vfs.util.MonitorOutputStream;
import org.apache.commons.vfs.util.RandomAccessMode;
import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileInfo;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;
import org.objectweb.proactive.extra.vfsprovider.protocol.StreamMode;


// TODO check for correctness of protocol <-> FileObject exceptions/errors mapping
public class ProActiveFileObject extends AbstractFileObject {
    // log in VFS (not ProActive) way
    private static Log log = LogFactory.getLog(ProActiveFileObject.class);

    private static final FileInfo IMAGINARY_FILE_INFO = new FileInfo() {
        private static final long serialVersionUID = 7304036230538561807L;

        public long getLastModifiedTime() {
            return 0;
        }

        public long getSize() {
            return 0;
        }

        public org.objectweb.proactive.extra.vfsprovider.protocol.FileType getType() {
            return null;
        }

        public boolean isHidden() {
            return false;
        }

        public boolean isReadable() {
            return false;
        }

        public boolean isWritable() {
            return false;
        }
    };

    private static IOException generateAndLogIOWrongStreamTypeExecption(WrongStreamTypeException e) {
        log
                .error("File server unexpectedly does not allow to perform some type of operation on an opened stream");
        return new IOException(
            "File server unexpectedly does not allow to perform some type of operation on an opened stream",
            e);
    }

    private static IOException generateAndLogIOStreamNotFoundException(StreamNotFoundException e) {
        log.error("File server unexpectedly closed the reopened file stream");
        return new IOException("File server unexpectedly closed the reopened file stream", e);
    }

    private FileInfo fileInfo;
    private ProActiveFileSystem proactiveFS;

    protected ProActiveFileObject(FileName name, ProActiveFileSystem fs) {
        super(name, fs);
        this.proactiveFS = fs;
    }

    @Override
    protected void doAttach() throws Exception {
        synchronized (proactiveFS) {
            if (fileInfo == null) {
                fileInfo = getServer().fileGetInfo(getPath());
                if (fileInfo == null) {
                    fileInfo = IMAGINARY_FILE_INFO;
                }
            }
        }
    }

    @Override
    protected void doDetach() throws Exception {
        synchronized (proactiveFS) {
            fileInfo = null;
        }
    }

    @Override
    protected long doGetContentSize() throws Exception {
        synchronized (proactiveFS) {
            return fileInfo.getSize();
        }
    }

    @Override
    protected FileType doGetType() throws Exception {
        synchronized (proactiveFS) {
            if (fileInfo.getType() == null) {
                return FileType.IMAGINARY;
            }

            switch (fileInfo.getType()) {
                case FILE:
                    return FileType.FILE;
                case DIRECTORY:
                    return FileType.FOLDER;
                default:
                    throw new RuntimeException("Unexpected file type");
            }
        }
    }

    @Override
    protected boolean doIsHidden() throws Exception {
        synchronized (proactiveFS) {
            return fileInfo.isHidden();
        }
    }

    @Override
    protected boolean doIsReadable() throws Exception {
        synchronized (proactiveFS) {
            return fileInfo.isReadable();
        }
    }

    @Override
    protected boolean doIsWriteable() throws Exception {
        synchronized (proactiveFS) {
            return fileInfo.isWritable();
        }
    }

    @Override
    protected long doGetLastModifiedTime() throws Exception {
        synchronized (proactiveFS) {
            return fileInfo.getLastModifiedTime();
        }
    }

    @Override
    protected String[] doListChildren() throws Exception {
        final Set<String> files = getServer().fileListChildren(getPath());
        return (String[]) files.toArray();
    }

    @Override
    protected void doCreateFolder() throws Exception {
        getServer().fileCreate(getPath(),
                org.objectweb.proactive.extra.vfsprovider.protocol.FileType.DIRECTORY);
    }

    @Override
    protected void doDelete() throws Exception {
        getServer().fileDelete(getPath(), false);
    }

    @Override
    protected void doRename(FileObject newfile) throws Exception {
        final ProActiveFileObject proactiveDestFile = (ProActiveFileObject) FileObjectUtils
                .getAbstractFileObject(newfile);
        getServer().fileRename(getPath(), proactiveDestFile.getPath());
    }

    @Override
    protected boolean doSetLastModTime(long modtime) throws Exception {
        return getServer().fileSetLastModifiedTime(getPath(), modtime);
    }

    @Override
    protected void onChange() throws Exception {
        synchronized (proactiveFS) {
            doDetach();
            doAttach();
        }
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        return new MonitorInputStream(new RawProActiveInputStreamAdapter());
    }

    @Override
    protected OutputStream doGetOutputStream(boolean append) throws Exception {
        return new MonitorOutputStream(new RawProActiveOutputStreamAdapter(append));
    }

    @Override
    protected RandomAccessContent doGetRandomAccessContent(RandomAccessMode mode) throws Exception {
        // TODO Auto-generated method stub
        return super.doGetRandomAccessContent(mode);
    }

    private String getPath() {
        return ((ProActiveFileName) getName()).getPath();
    }

    // we always access server this way, as ProActiveFileSystem is responsible
    // for managing its instance(s)
    private FileSystemServer getServer() {
        return proactiveFS.getServer();
    }

    private class RawProActiveInputStreamAdapter extends InputStream {
        private final byte[] SINGLE_BYTE_BUF = new byte[1];
        private long streamId;
        private long pos;

        public RawProActiveInputStreamAdapter() throws IOException {
            streamId = getServer().streamOpen(getPath(), StreamMode.SEQUENTIAL_READ);
        }

        public synchronized long getStreamId() {
            return streamId;
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            byte result[];
            try {
                try {
                    result = getServer().streamRead(streamId, len);
                } catch (StreamNotFoundException e) {
                    reopenStream();
                    result = getServer().streamRead(streamId, len);
                }
            } catch (WrongStreamTypeException e) {
                throw generateAndLogIOWrongStreamTypeExecption(e);
            } catch (StreamNotFoundException e) {
                throw generateAndLogIOStreamNotFoundException(e);
            }

            if (result == null) {
                return -1;
            }
            System.arraycopy(result, 0, b, off, result.length);
            pos += result.length;
            return result.length;
        }

        @Override
        public int read() throws IOException {
            if (read(SINGLE_BYTE_BUF) == -1) {
                return -1;
            }
            return SINGLE_BYTE_BUF[0];
        }

        @Override
        public synchronized long skip(long n) throws IOException {
            try {
                long skippedBytes;
                try {
                    skippedBytes = getServer().streamSkip(streamId, n);
                } catch (StreamNotFoundException e) {
                    reopenStream();
                    skippedBytes = getServer().streamSkip(streamId, n);
                }
                pos += skippedBytes;
                return skippedBytes;
            } catch (StreamNotFoundException e) {
                throw generateAndLogIOStreamNotFoundException(e);
            } catch (WrongStreamTypeException e) {
                throw generateAndLogIOWrongStreamTypeExecption(e);
            }
        }

        @Override
        public synchronized void close() throws IOException {
            try {
                getServer().streamClose(streamId);
            } catch (StreamNotFoundException e) {
                // ignore
            }
        }

        private void reopenStream() throws IOException, StreamNotFoundException, WrongStreamTypeException {
            log.debug("Reopening input stream: " + streamId);
            streamId = getServer().streamOpen(getPath(), StreamMode.SEQUENTIAL_READ);
            if (pos > 0) {
                getServer().streamSkip(streamId, pos);
            }
        }
    }

    private class RawProActiveOutputStreamAdapter extends OutputStream {
        private final byte[] SINGLE_BYTE_BUF = new byte[1];
        private long streamId;

        public RawProActiveOutputStreamAdapter(final boolean append) throws IOException {
            final StreamMode mode;
            if (append) {
                mode = StreamMode.SEQUENTIAL_APPEND;
            } else {
                mode = StreamMode.SEQUENTIAL_WRITE;
            }
            streamId = getServer().streamOpen(getPath(), mode);
        }

        public synchronized long getStreamId() {
            return streamId;
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            final byte bytesToSent[];
            if (off != 0 || len != b.length) {
                bytesToSent = Arrays.copyOfRange(b, off, len);
            } else {
                bytesToSent = b;
            }

            try {
                try {
                    getServer().streamWrite(streamId, bytesToSent);
                } catch (StreamNotFoundException e) {
                    reopenStream();
                    getServer().streamWrite(streamId, bytesToSent);
                }
            } catch (WrongStreamTypeException e) {
                throw generateAndLogIOWrongStreamTypeExecption(e);
            } catch (StreamNotFoundException e) {
                throw generateAndLogIOStreamNotFoundException(e);
            }
        }

        @Override
        public void write(int b) throws IOException {
            SINGLE_BYTE_BUF[0] = (byte) b;
            write(SINGLE_BYTE_BUF);
        }

        @Override
        public synchronized void flush() throws IOException {
            try {
                getServer().streamFlush(streamId);
            } catch (WrongStreamTypeException e) {
                throw generateAndLogIOWrongStreamTypeExecption(e);
            } catch (StreamNotFoundException e) {
                // as long as FileSystemServer guarantees that this exception can occur
                // after streamOpen() only after proper close at server side,
                // we do not need to open it to flush it again - we can ignore it
            }
        }

        @Override
        public synchronized void close() throws IOException {
            try {
                getServer().streamClose(streamId);
            } catch (StreamNotFoundException e) {
                // ignore
            }
        }

        private void reopenStream() throws IOException {
            log.debug("Reopening output stream: " + streamId);
            streamId = getServer().streamOpen(getPath(), StreamMode.SEQUENTIAL_APPEND);
        }
    }
}
