package org.objectweb.proactive.extra.dataspaces;

/**
 * Interface any access limitation policy for {@link DataSpacesFileObject} instance. Note, that a
 * new policy can be instantiated for a new {@link DataSpacesFileObject} instance as
 * {@link #newInstance(DataSpacesFileObject)}.
 *
 * @see {@link AbstractLimitingFileObject}
 */
public interface LimitingPolicy {

    /**
     * Implementation of this method may limit any write access for a
     * {@link AbstractLimitingFileObject} instance.
     *
     * @return <code>true</code> if any write access is prohibited, <code>false</code> if is
     *         allowed.
     */
    public abstract boolean isReadOnly();

    /**
     * Instantiates a new policy instance for a new {@link DataSpacesFileObject}.
     *
     * @param newFileObject
     * @return
     */
    public abstract LimitingPolicy newInstance(DataSpacesFileObject newFileObject);

}