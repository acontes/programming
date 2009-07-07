package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.IOException;

import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;


/**
 * Deploys {@link FileSystemServer} with instance of {@link FileSystemServerImpl} implementation on
 * the local runtime.
 */
public class FileSystemServerDeployer {

    private static final String FILE_SERVER_DEFAULT_NAME = "defaultFileSystemServer";

    /** URL of the remote object */
    final private String url;

    final private FileSystemServer fileSystemServer;

    RemoteObjectExposer<FileSystemServer> roe;

    /**
     * Deploys locally a FileSystemServer as a RemoteObject with a default name.
     * 
     * @param rootPath
     * @throws IOException
     */
    public FileSystemServerDeployer(String rootPath) throws IOException {
        this(FILE_SERVER_DEFAULT_NAME, rootPath);
    }

    /**
     * Deploys locally a FileSystemServer as a RemoteObject with a given name.
     * 
     * @param name
     *            of deployed RemoteObject
     * @param rootPath
     * @throws IOException
     */
    public FileSystemServerDeployer(String name, String rootPath) throws IOException {
        fileSystemServer = new FileSystemServerImpl(rootPath);
        roe = PARemoteObject.newRemoteObject(FileSystemServerImpl.class.getName(), this.fileSystemServer);
        roe.createRemoteObject(name);
        url = roe.getURL();
    }

    public FileSystemServer getLocalFileSystemServer() {
        return this.fileSystemServer;
    }

    public FileSystemServer getRemoteFileSystemServer() throws ProActiveException {
        return (FileSystemServer) RemoteObjectHelper.generatedObjectStub(this.roe.getRemoteObject());
    }

    public String getRemoteFileSystemServerURL() {
        return this.url;
    }

    /**
     * Unexport the remote object.
     * 
     * @throws ProActiveException
     */
    public void terminate() throws ProActiveException {
        if (roe != null) {
            roe.unexportAll();
            roe = null;
        }
    }
}
