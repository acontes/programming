package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;


/**
 * Implementation of a file representation in Data Spaces framework - {@link DataSpacesFileObject}.
 * This class represents file within space, that is accessed through VFS library, with usage of
 * {@link AbstractLimitingFileObject} decorator which limits access to some operations.
 * <p>
 * Instance of this class represents a file within Data Spaces framework, hence the URI address
 * related information is attached during the creation process. However, to make write-access limits
 * working properly, id of owner active object has to be set before any use, through
 * {@link #setOwnerActiveObjectId(String)}.
 * <p>
 * To conform to general Data Spaces guarantees, basing on file URI and Active Object id write
 * access is limited for:
 * <ul>
 * <li>input data space - for every AO</li>
 * <li>AO's scratch space - for AO different that scratch's owner</li>
 * </ul>
 * <p>
 * Instances of this class conform to the same rules regarding concurrent access, resources
 * management etc. as pure {@link FileObject} does.
 */
// FIXME limit access to the getParent()
public class DataSpacesFileObjectImpl extends AbstractLimitingFileObject implements DataSpacesFileObject {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final DataSpacesURI spaceUri;
    private final String vfsSpaceRootPath;
    private String ownerActiveObjectId;
    private Boolean readOnly;

    /**
     * Creates an instance of DataSpacesFileObjectImpl. Before any usage of this class, id of an
     * active object has to be set accordingly.
     * 
     * @param fileObject
     *            file object that is going to be represented as DataSpacesFileObject; cannot be
     *            <code>null</code>
     * @param spaceUri
     *            Data Spaces URI of this file object's space; cannot be <code>null</code>
     * @param vfsSpaceRootPath
     *            VFS path of the space root FileObject; cannot be <code>null</code>
     */
    public DataSpacesFileObjectImpl(FileObject fileObject, DataSpacesURI spaceUri, String vfsSpaceRootPath) {
        super(fileObject);
        this.spaceUri = spaceUri;
        this.vfsSpaceRootPath = vfsSpaceRootPath;
    }

    /**
     * Returned URI is always suitable for having path:
     * {@link DataSpacesURI#isSuitableForHavingPath()} returns true.
     * <p>
     * FIXME: after getParent() limitation it should be like that? what about active object id?
     */
    public String getURI() {
        // FIXME when DataSpacesFileObject will have just VFS adapter, store full URI as a field instead?
        final String path = getName().getPath();
        if (!path.startsWith(vfsSpaceRootPath)) {
            final ProActiveRuntimeException x = new ProActiveRuntimeException(
                "VFS path of this DataSpacesFileObject does not start with its space VFS path");
            ProActiveLogger.logImpossibleException(logger, x);
            throw x;
        }
        final String relPath = path.substring(vfsSpaceRootPath.length());
        return spaceUri.toString() + relPath;
    }

    /**
     * Sets id of owner active object of this FileObject instance. Can be done only once.
     * 
     * @param ownerActiveObjectId
     *            id of active object owning this FileObject instance.
     */
    public void setOwnerActiveObjectId(String ownerActiveObjectId) {
        if (this.ownerActiveObjectId != null) {
            final ProActiveRuntimeException x = new ProActiveRuntimeException("Owner AO id already set");
            ProActiveLogger.logImpossibleException(logger, x);
            throw x;
        }
        this.ownerActiveObjectId = ownerActiveObjectId;
    }

    @Override
    protected boolean isReadOnly() {
        if (readOnly == null) {
            readOnly = computeIsReadOnly();
        }
        return readOnly;
    }

    @Override
    protected FileObject doDecorateFile(FileObject file) {
        return new DataSpacesFileObjectImpl(file, spaceUri, vfsSpaceRootPath);
    }

    private boolean computeIsReadOnly() {
        final DataSpacesURI uri = getDataSpacesURI();

        if (ownerActiveObjectId == null) {
            logger.warn("Computing whether file access is read-only without owner AO being defined");
        }

        // real logic
        switch (uri.getSpaceType()) {
            case INPUT:
                return true;
            case OUTPUT:
                return false;
            case SCRATCH:
                final String uriAoId = uri.getActiveObjectId();
                return uriAoId == null || !uriAoId.equals(ownerActiveObjectId);
            default:
                throw new IllegalStateException("Unexpected space type");
        }
    }

    private DataSpacesURI getDataSpacesURI() {
        final String uriString = getURI();
        try {
            return DataSpacesURI.parseURI(uriString);
        } catch (MalformedURIException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            throw new ProActiveRuntimeException("Could not parse self-generated URI", e);
        }
    }
}
