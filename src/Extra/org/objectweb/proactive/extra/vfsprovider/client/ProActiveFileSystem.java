package org.objectweb.proactive.extra.vfsprovider.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;


public class ProActiveFileSystem extends AbstractFileSystem {
    private FileSystemServer server;

    protected ProActiveFileSystem(FileName rootName, FileSystemOptions fileSystemOptions)
            throws FileSystemException {
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

    protected FileSystemServer getServer() throws FileSystemException {
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

    private FileSystemServer createServerStub() throws FileSystemException {
        final String serverURL = ((ProActiveFileName) getRootName()).getServerURL();
        final Object stub;
        try {
            stub = PARemoteObject.lookup(new URI(serverURL));
        } catch (URISyntaxException e) {
            throw new FileSystemException("Unexpected URL of file system server: " + serverURL, e);
        } catch (ProActiveException e) {
            throw new FileSystemException("Could not connect to file system server under specified URL: " +
                serverURL, e);
        }

        if (!(stub instanceof FileSystemServer)) {
            throw new FileSystemException(
                "No valid FileSystemServer instance can be found under specified URL: " + serverURL);
        }
        return (FileSystemServer) stub;
    }
}
