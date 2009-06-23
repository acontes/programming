package org.objectweb.proactive.extra.vfsprovider.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;


public class ProActiveFileProvider extends AbstractOriginatingFileProvider {

    static final Set<Capability> CAPABILITIES = Collections.unmodifiableSet(new HashSet<Capability>(Arrays
            .asList(new Capability[] { Capability.READ_CONTENT, Capability.WRITE_CONTENT,
                    Capability.RANDOM_ACCESS_READ, Capability.RANDOM_ACCESS_WRITE, Capability.APPEND_CONTENT,
                    Capability.LAST_MODIFIED, Capability.GET_LAST_MODIFIED,
                    Capability.SET_LAST_MODIFIED_FILE, Capability.SET_LAST_MODIFIED_FOLDER,
                    Capability.CREATE, Capability.DELETE, Capability.RENAME, Capability.GET_TYPE,
                    Capability.LIST_CHILDREN, Capability.URI })));

    public ProActiveFileProvider() {
        setFileNameParser(ProActiveFileNameParser.getInstance());
    }

    @SuppressWarnings("unchecked")
    public Collection getCapabilities() {
        return CAPABILITIES;
    }

    @Override
    protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions)
            throws FileSystemException {
        return new ProActiveFileSystem(rootName, fileSystemOptions);
    }
}
