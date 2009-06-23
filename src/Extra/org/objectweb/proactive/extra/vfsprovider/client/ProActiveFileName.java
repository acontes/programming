package org.objectweb.proactive.extra.vfsprovider.client;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.GenericFileName;


public class ProActiveFileName extends GenericFileName {
    // TODO move to protocol or server deployer specification?
    public static final String SERVICE_AND_FILE_PATH_SEPARATOR = "?";

    private static int getDefaultPortForScheme(String scheme) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Path of a FileSystemServer service. Not including file path.
     */
    private final String servicePath;

    /**
     * Cached URL of ProActiveFileSystem server
     */
    private volatile String serverURL;
    private final Object serverURLSync = new Object();

    protected ProActiveFileName(String scheme, String hostName, int port, String userName, String password,
            String servicePath, String path, FileType type) {
        super(scheme, hostName, port, getDefaultPortForScheme(scheme), userName, password, path, type);
        if (servicePath == null || servicePath.length() == 0) {
            this.servicePath = ROOT_PATH;
        } else {
            this.servicePath = servicePath;
        }
    }

    @Override
    protected void appendRootUri(StringBuffer buffer, boolean addPassword) {
        super.appendRootUri(buffer, addPassword);
        buffer.append(servicePath);
        buffer.append(SERVICE_AND_FILE_PATH_SEPARATOR);
    }

    @Override
    public FileName createName(String absPath, FileType type) {
        return new ProActiveFileName(getScheme(), getHostName(), getPort(), getUserName(), getPassword(),
            servicePath, getPath(), getType());
    }

    public String getServicePath() {
        return servicePath;
    }

    public String getServerURL() {
        if (serverURL == null) {
            synchronized (serverURLSync) {
                if (serverURL == null) {
                    serverURL = createServerURL();
                }
            }
        }
        return serverURL;
    }

    private String createServerURL() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getServerScheme());
        buffer.append("://");
        appendCredentials(buffer, true);
        buffer.append(getHostName());
        if (getPort() != getDefaultPort()) {
            buffer.append(':');
            buffer.append(getPort());
        }
        buffer.append(servicePath);

        return buffer.toString();
    }

    private String getServerScheme() {
        getScheme();
        // TODO
        return null;
    }
}
