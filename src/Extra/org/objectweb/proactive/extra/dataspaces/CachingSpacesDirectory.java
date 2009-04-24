/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.Set;

import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;


/**
 * Decorator of SpacesDirectory that caches SpaceInstanceInfo in its SpacesDirectoryImpl instance.
 * <p>
 * Instances of this class are thread-safe.
 */
public class CachingSpacesDirectory implements SpacesDirectory {

    private final SpacesDirectoryImpl localDirectory;

    private final SpacesDirectory remoteDirectory;

    public CachingSpacesDirectory(SpacesDirectory directoryToCache) {
        localDirectory = new SpacesDirectoryImpl();
        remoteDirectory = directoryToCache;
    }

    /**
     * This method call is always delegated remotely.
     * 
     * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupAll
     *      (org.objectweb.proactive.extensions.dataspaces.DataSpacesURI)
     */
    public Set<SpaceInstanceInfo> lookupAll(DataSpacesURI uri) {

        if (uri.isComplete())
            throw new IllegalArgumentException("Space URI must not be complete for this method call");

        synchronized (this) {
            final Set<SpaceInstanceInfo> ret = remoteDirectory.lookupAll(uri);
            localDirectory.register(ret);

            return ret;
        }
    }

    /**
     * Try in cache, if not found try remotely.
     * 
     * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#lookupFirst
     *      (org.objectweb.proactive.extensions.DataSpacesURI.DataSpacesURI)
     */
    public SpaceInstanceInfo lookupFirst(DataSpacesURI uri) {

        if (!uri.isComplete())
            throw new IllegalArgumentException("Space URI must be complete for this method call");

        if (uri.getPath() != null)
            throw new IllegalArgumentException("Space URI must not contain path for this method call");

        // double-checked locking, as it would be a pity if we have to wait for
        // remote lookups when we can answer some lookups using local directory
        SpaceInstanceInfo sii = localDirectory.lookupFirst(uri);
        if (sii != null)
            return sii;

        synchronized (this) {
            sii = localDirectory.lookupFirst(uri);
            if (sii != null)
                return sii;

            sii = remoteDirectory.lookupFirst(uri);

            if (sii != null)
                try {
                    localDirectory.register(sii);
                } catch (SpaceAlreadyRegisteredException e) {
                    // this should never happen when properly synchronized
                    throw new RuntimeException(e);
                    // FIXME log instead of throwing it?
                }
            return sii;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#register
     * (org.objectweb.proactive.extensions.dataspaces.DataSpacesURI,
     * org.objectweb.proactive.extensions.dataspaces.SpaceInstanceInfo)
     */
    public synchronized void register(SpaceInstanceInfo spaceInstanceInfo)
            throws SpaceAlreadyRegisteredException, WrongApplicationIdException {

        remoteDirectory.register(spaceInstanceInfo);
        localDirectory.register(spaceInstanceInfo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.extensions.dataspaces.SpacesDirectory#unregister
     * (org.objectweb.proactive.extensions.dataspaces.DataSpacesURI)
     */
    public synchronized boolean unregister(DataSpacesURI uri) {
        localDirectory.unregister(uri);
        return remoteDirectory.unregister(uri);
    }
}
