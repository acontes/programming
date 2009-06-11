package org.objectweb.proactive.extra.dataspaces.adapter.vfs;

import org.objectweb.proactive.extra.dataspaces.api.Capability;
import org.objectweb.proactive.extra.dataspaces.api.FileSystem;

public class VFSFileSystemAdapter implements FileSystem {

    final private org.apache.commons.vfs.FileSystem adaptee;

    public VFSFileSystemAdapter(org.apache.commons.vfs.FileSystem fileSystem) {
        adaptee = fileSystem;
    }

    public boolean hasCapability(Capability capability) {
        final org.apache.commons.vfs.Capability vfsCapability = buildVFSCapability(capability);
        return adaptee.hasCapability(vfsCapability);
    }

    private static org.apache.commons.vfs.Capability buildVFSCapability(Capability capability) {
        switch (capability) {
            case APPEND_CONTENT:
                return org.apache.commons.vfs.Capability.APPEND_CONTENT;
            case ATTRIBUTES:
                return org.apache.commons.vfs.Capability.ATTRIBUTES;
            case COMPRESS:
                return org.apache.commons.vfs.Capability.COMPRESS;
            case CREATE:
                return org.apache.commons.vfs.Capability.CREATE;
            case DELETE:
                return org.apache.commons.vfs.Capability.DELETE;
            case DIRECTORY_READ_CONTENT:
                return org.apache.commons.vfs.Capability.DIRECTORY_READ_CONTENT;
            case FS_ATTRIBUTES:
                return org.apache.commons.vfs.Capability.FS_ATTRIBUTES;
            case GET_LAST_MODIFIED:
                return org.apache.commons.vfs.Capability.GET_LAST_MODIFIED;
            case GET_TYPE:
                return org.apache.commons.vfs.Capability.GET_TYPE;
            case LAST_MODIFIED:
                return org.apache.commons.vfs.Capability.LAST_MODIFIED;
            case LIST_CHILDREN:
                return org.apache.commons.vfs.Capability.LIST_CHILDREN;
            case MANIFEST_ATTRIBUTES:
                return org.apache.commons.vfs.Capability.MANIFEST_ATTRIBUTES;
            case RANDOM_ACCESS_READ:
                return org.apache.commons.vfs.Capability.RANDOM_ACCESS_READ;
            case RANDOM_ACCESS_WRITE:
                return org.apache.commons.vfs.Capability.RANDOM_ACCESS_WRITE;
            case READ_CONTENT:
                return org.apache.commons.vfs.Capability.READ_CONTENT;
            case RENAME:
                return org.apache.commons.vfs.Capability.RENAME;
            case SET_LAST_MODIFIED_FILE:
                return org.apache.commons.vfs.Capability.SET_LAST_MODIFIED_FILE;
            case SET_LAST_MODIFIED_FOLDER:
                return org.apache.commons.vfs.Capability.SET_LAST_MODIFIED_FOLDER;
            case SIGNING:
                return org.apache.commons.vfs.Capability.SIGNING;
            case URI:
                return org.apache.commons.vfs.Capability.URI;
            case VIRTUAL:
                return org.apache.commons.vfs.Capability.VIRTUAL;
            case WRITE_CONTENT:
                return org.apache.commons.vfs.Capability.WRITE_CONTENT;
            default:
                return null;
        }
    }
}
