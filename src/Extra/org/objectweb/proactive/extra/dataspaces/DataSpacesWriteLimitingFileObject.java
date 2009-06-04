package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;


/**
 * FileObject decorator limiting write access to some DataSpaces files, that can not be done at
 * {@link SpacesMountManager} or VFS level.
 * <p>
 * Basing on file URI and Active Object id it limits write access for:
 * <ul>
 * <li>URI without space being fully defined - for every AO</li>
 * <li>input data space - for every AO</li>
 * <li>AO's scratch space - for AO different that scratch's owner</li>
 * </ul>
 * 
 * @see AbstractWriteLimitingFileObject
 */
public class DataSpacesWriteLimitingFileObject extends AbstractWriteLimitingFileObject {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final String aoId;
    private volatile Boolean readOnly;
    private Object readOnlyLock = new Object();

    /**
     * Creates Data Spaces FileObject decorator, that will be valid for Active Object with specified
     * id, so it can be used within that AO's thread, or related threads (like immediate services).
     * 
     * @param decoratedFileObject
     *            file object to decorate
     * @param aoId
     *            identifier of Active Object that will use this decorated FileObject
     */
    public DataSpacesWriteLimitingFileObject(final FileObject decoratedFileObject, final String aoId) {
        super(decoratedFileObject);
        this.aoId = aoId;
    }

    @Override
    protected FileObject doDecorateFile(final FileObject file) {
        return new DataSpacesWriteLimitingFileObject(file, aoId);
    }

    @Override
    protected boolean isReadOnly() {
        // perhaps synchronization is not needed here as it should be called from AO's thread, but maybe
        // somebody would like to use already opened FileObject from another thread, like immediate service 
        if (readOnly == null) {
            synchronized (readOnlyLock) {
                if (readOnly == null)
                    readOnly = computeIsReadOnly();
            }
        }
        return readOnly;
    }

    private boolean computeIsReadOnly() {
        final String uriString = DataSpacesImpl.getURI(this);
        final DataSpacesURI uri;
        try {
            uri = DataSpacesURI.parseURI(uriString);
        } catch (MalformedURIException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            throw new RuntimeException("Could not parse self-generated URI", e);
        }

        if (!uri.isSpacePartFullyDefined())
            return true;

        switch (uri.getSpaceType()) {
            case INPUT:
                return true;
            case OUTPUT:
                return false;
            case SCRATCH:
                final String uriAoId = uri.getActiveObjectId();
                return uriAoId == null || !uriAoId.equals(aoId);
            default:
                throw new IllegalStateException("Unexpected space type");
        }
    }
}
