package org.objectweb.proactive.extra.dataspaces;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;


/**
 * An write access limiting policy implementation for a DataSpacesFileObject.
 * <p>
 * Basing on file URI and Active Object id it limits write access for:
 * <ul>
 * <li>URI without space being fully defined - for every AO</li>
 * <li>input data space - for every AO</li>
 * <li>AO's scratch space - for AO different that scratch's owner</li>
 * </ul>
 *
 * @see AbstractLimitingFileObject
 */
public class URIBasedWriteLimitingPolicy implements LimitingPolicy {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final Boolean readOnly;

    private final String activeObjectId;

    /**
     * Creates an instance of a write limiting policy for a DataSpacesFileObject. It will be valid
     * for Active Object with specified id, so it can be used within that AO's thread, or related
     * threads (like immediate services).
     *
     * @param aoId
     *            identifier of an Active Object that will use this DataSpacesFileObject
     * @param fileObject
     *            file object for that a policy should be instantiated
     */
    public URIBasedWriteLimitingPolicy(final String aoId, DataSpacesFileObject fileObject) {
        activeObjectId = aoId;
        readOnly = computeIsReadOnly(aoId, fileObject);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public LimitingPolicy newInstance(DataSpacesFileObject newFileObject) {
        return new URIBasedWriteLimitingPolicy(activeObjectId, newFileObject);
    }

    private boolean computeIsReadOnly(String aoId, DataSpacesFileObject fileObject) {
        final String uriString = fileObject.getURI();
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
