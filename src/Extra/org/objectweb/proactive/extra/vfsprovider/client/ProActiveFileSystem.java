package org.objectweb.proactive.extra.vfsprovider.client;

import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;


public class ProActiveFileSystem extends AbstractFileSystem {
    private FileSystemServer server;

    protected ProActiveFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) {
        super(rootName, null, fileSystemOptions);
        this.server = createServerStub();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addCapabilities(Collection caps) {
        caps.addAll(ProActiveFileProvider.CAPABILITIES);
    }

    @Override
    protected FileObject createFile(FileName name) throws Exception {
        return new ProActiveFileObject(name, this);
    }

    protected FileSystemServer getServer() {
        synchronized (this) {
            if (server == null) {
                server = createServerStub();
            }
            return server;
        }
    }

    // always called within synchronized (this)
    @Override
    protected void doCloseCommunicationLink() {
        server = null;
    }

    private FileSystemServer createServerStub() {
        final String serverURL = ((ProActiveFileName) getRootName()).getServerURL();
        // TODO
        return null;
    }
}
