/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.core.naming;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.objectweb.proactive.extra.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.core.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;


/**
 * In-memory implementation of {@link SpacesDirectory}.
 * <p>
 * Instances of this class are thread-safe.
 * 
 * @see SpacesDirectory
 */
public class SpacesDirectoryImpl implements SpacesDirectory {
    private final SortedMap<DataSpacesURI, SpaceInstanceInfo> data = new TreeMap<DataSpacesURI, SpaceInstanceInfo>();

    protected static void checkAbstractURI(DataSpacesURI uri) {
        if (uri.isSpacePartFullyDefined())
            throw new IllegalArgumentException(
                "Space part must not be fully defined in URI for this method call");
    }

    protected static void checkMountingPointURI(DataSpacesURI uri) {
        if (!uri.isSpacePartFullyDefined())
            throw new IllegalArgumentException("Space part must be fully defined in URI for this method call");

        if (!uri.isSpacePartOnly())
            throw new IllegalArgumentException("Space URI must define only space part for this method call");
    }

    public Set<SpaceInstanceInfo> lookupMany(DataSpacesURI uri) {
        checkAbstractURI(uri);

        final DataSpacesURI nextKey = uri.nextURI();
        final Set<SpaceInstanceInfo> ret = new HashSet<SpaceInstanceInfo>();

        synchronized (data) {
            final SortedMap<DataSpacesURI, SpaceInstanceInfo> sub = data.subMap(uri, nextKey);

            if (sub.size() == 0)
                return null;
            ret.addAll(sub.values());
        }
        return ret;
    }

    public SpaceInstanceInfo lookupOne(DataSpacesURI uri) {
        checkMountingPointURI(uri);

        synchronized (data) {
            return data.get(uri);
        }
    }

    public void register(SpaceInstanceInfo spaceInstanceInfo) throws SpaceAlreadyRegisteredException {
        final DataSpacesURI mpoint;

        // get mounting point URI that cannot be null
        synchronized (data) {
            mpoint = spaceInstanceInfo.getMountingPoint();

            if (data.containsKey(mpoint))
                throw new SpaceAlreadyRegisteredException(
                    "Mapping for a given space URI is already registered");
            data.put(mpoint, spaceInstanceInfo);
        }
    }

    public boolean unregister(DataSpacesURI uri) {
        checkMountingPointURI(uri);

        synchronized (data) {
            if (!data.containsKey(uri))
                return false;

            data.remove(uri);
        }
        return true;
    }

    /**
     * Helper method for bulked registration as obtaining lock is done only once.
     * 
     * @param ssis
     */
    protected void register(Set<SpaceInstanceInfo> ssis) {
        synchronized (data) {
            for (SpaceInstanceInfo ssi : ssis)
                data.put(ssi.getMountingPoint(), ssi);
        }
    }

    /**
     * Helper method for bulked unregistration as obtaining lock is done only once.
     * 
     * @param uris
     */
    protected void unregister(Set<DataSpacesURI> uris) {
        synchronized (data) {
            for (DataSpacesURI key : uris)
                data.remove(key);
        }
    }
}
