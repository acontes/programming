package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;

// TODO
public class VFSFactory {
    public static DefaultFileSystemManager createDefaultFileSystemManager() throws FileSystemException {
        final DefaultFileSystemManager manager = new DefaultFileSystemManager();
        manager.addProvider("file", new DefaultLocalFileProvider());
        // TODO
        manager.init();
        return manager;
    }
}
