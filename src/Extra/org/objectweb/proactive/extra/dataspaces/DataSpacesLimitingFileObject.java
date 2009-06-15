package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.NameScope;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
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
 * General file access is limited to spaces.
 * <p>
 * Instances of this class conform to the same rules regarding concurrent access, resources
 * management etc. as pure {@link FileObject} does.
 */
public class DataSpacesLimitingFileObject extends AbstractLimitingFileObject<DataSpacesLimitingFileObject> {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final DataSpacesURI spaceUri;
    private final FileName spaceRootFileName;
    private String ownerActiveObjectId;
    private volatile Boolean readOnly;
    private Object readOnlyLock = new Object();

    /**
     * Creates an instance of DataSpacesLimitingFileObject. Before any usage of this class, id of an
     * active object has to be set accordingly.
     * 
     * @param fileObject
     *            file object that is going to be represented as DataSpacesFileObject; cannot be
     *            <code>null</code>
     * @param spaceUri
     *            Data Spaces URI of this file object's space; cannot be <code>null</code>
     * @param spaceRootFileName
     *            VFS path of the space root FileObject; cannot be <code>null</code>
     */
    public DataSpacesLimitingFileObject(FileObject fileObject, DataSpacesURI spaceUri,
            FileName spaceRootFileName) {
        super(fileObject);
        this.spaceUri = spaceUri;
        this.spaceRootFileName = spaceRootFileName;
    }

    /**
     * Returned URI is always inside a space - {@link DataSpacesURI#isSpacePartFullyDefined()}
     * returns always true. {@link DataSpacesURI#isSuitableForHavingPath()} returns true.
     */
    public String getURI() {
        // TODO when DataSpacesFileObject will have just VFS adapter, maybe store full URI as a field instead?
        // way of concatenating spaceUri and absolutePathInSpace is not the best possible 
        final FileName name = getName();
        if (!spaceRootFileName.isDescendent(name, NameScope.DESCENDENT_OR_SELF)) {
            final ProActiveRuntimeException x = new ProActiveRuntimeException(
                "VFS path of this DataSpacesFileObject does not start with its space VFS path");
            ProActiveLogger.logImpossibleException(logger, x);
            throw x;
        }
        final String absolutePathInSpace = name.getPath().substring(spaceRootFileName.getPath().length());
        return spaceUri.toString() + absolutePathInSpace;
    }

    /**
     * @return URI of a file, always inside a space
     * @see #getURI()
     */
    public DataSpacesURI getDataSpacesURI() {
        final String uriString = getURI();
        try {
            return DataSpacesURI.parseURI(uriString);
        } catch (MalformedURIException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            throw new ProActiveRuntimeException("Could not parse self-generated URI", e);
        }
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
        //TODO why we do it again in a lazy way? Most of calls will use isReadOnly, so we can do it
        // in the constructor, even because it does not take a long while..
        if (readOnly == null) {
            synchronized (readOnlyLock) {
                if (readOnly == null) {
                    readOnly = computeIsReadOnly();
                }
            }
        }
        return readOnly;
    }

    @Override
    protected boolean canReturnAncestor(final DataSpacesLimitingFileObject fileObject) {
        return spaceRootFileName.isDescendent(fileObject.getName(), NameScope.DESCENDENT_OR_SELF);
    }

    @Override
    protected DataSpacesLimitingFileObject doDecorateFile(FileObject file) {
        return new DataSpacesLimitingFileObject(file, spaceUri, spaceRootFileName);
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
}
