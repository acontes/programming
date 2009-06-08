package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Implements abstract {@AbstractLimitingFileObject} which is a
 * decorator for VFS FileObject that limit access to some operations according to provided
 * LimitingPolicy. Implements also the public interface of a file representation in Data Spaces
 * framework - {@link DataSpacesFileObject}.
 * <p>
 * Instance of this class represents a file within Data Spaces framework, hence the URI address is
 * attached and set during the creation process.
 */
// FIXME limit access to the getParent()
public class DataSpacesFileObjectImpl extends AbstractLimitingFileObject implements DataSpacesFileObject {

    private final DataSpacesURI spaceUri;
    private String vfsSpaceRootPath;

    /**
     * @param fileObject
     *            file object that is going to be represented as DataSpacesFileObject;cannot be
     *            <code>null</code>
     * @param spaceUri
     *            Data Spaces URI of this file object's space; cannot be <code>null</code>
     * @param vfsSpaceRootPath
     *            VFS path of the space root FileObject
     */
    public DataSpacesFileObjectImpl(FileObject fileObject, DataSpacesURI spaceUri, String vfsSpaceRootPath) {
        super(fileObject);
        this.spaceUri = spaceUri;
        this.vfsSpaceRootPath = vfsSpaceRootPath;
    }

    public String getURI() {
        // FIXME when DataSpacesFileObject will have just VFS adapter, store full URI as a field instead?
        final String path = getName().getPath();
        if (!path.startsWith(vfsSpaceRootPath)) {
            final ProActiveRuntimeException x = new ProActiveRuntimeException(
                "VFS path of this DataSpacesFileObject does not start with its space VFS path");
            ProActiveLogger.logImpossibleException(ProActiveLogger.getLogger(Loggers.DATASPACES), x);
            throw x;
        }
        final String relPath = path.substring(vfsSpaceRootPath.length());
        return spaceUri.toString() + relPath;
    }

    @Override
    protected FileObject doDecorateFile(FileObject file) {
        final DataSpacesFileObjectImpl newFile = new DataSpacesFileObjectImpl(file, spaceUri,
            vfsSpaceRootPath);
        final LimitingPolicy policy = getLimitingPolicy();

        if (policy != null) {
            final LimitingPolicy newPolicy = policy.newInstance(newFile);
            newFile.setLimitingPolicy(newPolicy);
        }
        return newFile;
    }
}
