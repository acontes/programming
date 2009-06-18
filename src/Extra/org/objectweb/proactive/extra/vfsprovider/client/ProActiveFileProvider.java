package org.objectweb.proactive.extra.vfsprovider.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileProvider;


public class ProActiveFileProvider extends AbstractFileProvider {

    private static final Set<Capability> CAPABILITIES = Collections.unmodifiableSet(new HashSet<Capability>(
        Arrays.asList(new Capability[] { Capability.READ_CONTENT, Capability.WRITE_CONTENT,
                Capability.RANDOM_ACCESS_READ, Capability.RANDOM_ACCESS_WRITE, Capability.APPEND_CONTENT,
                Capability.LAST_MODIFIED, Capability.GET_LAST_MODIFIED, Capability.SET_LAST_MODIFIED_FILE,
                Capability.SET_LAST_MODIFIED_FOLDER, Capability.CREATE, Capability.DELETE, Capability.RENAME,
                Capability.GET_TYPE, Capability.LIST_CHILDREN, Capability.URI })));

    public FileObject findFile(FileObject arg0, String arg1, FileSystemOptions arg2)
            throws FileSystemException {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    public Collection getCapabilities() {
        return CAPABILITIES;
    }

}
