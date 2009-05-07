/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DecoratedFileObject;


/**
 * Partial-workaround for VFS bug - unsynchronized content-related methods. It does not resolve the
 * issue completely, just reduces the risk or problems.
 * <p>
 * FIXME: depends on bug VFS-253
 */
public class SyncingFileObjectDectorator extends DecoratedFileObject {
    private final Object sync = new Object();

    public SyncingFileObjectDectorator(final FileObject decoratedFileObject) {
        super(decoratedFileObject);
    }

    @Override
    public FileContent getContent() throws FileSystemException {
        synchronized (sync) {
            return super.getContent();
        }
    }

    @Override
    public boolean isContentOpen() {
        synchronized (sync) {
            return super.isContentOpen();
        }
    }

    @Override
    public void close() throws FileSystemException {
        synchronized (sync) {
            super.close();
        }
    }
}