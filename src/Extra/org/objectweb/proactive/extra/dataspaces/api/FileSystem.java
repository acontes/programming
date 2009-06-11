package org.objectweb.proactive.extra.dataspaces.api;

public interface FileSystem {

    /**
     * Determines if this file system has a particular capability.
     *
     * @param capability
     *            The capability to check for.
     * @return true if this filesystem has the requested capability. Note that not all files in the
     *         file system may have the capability.
     * @todo Move this to another interface, so that set of capabilities can be queried.
     */
    boolean hasCapability(Capability capability);
}
