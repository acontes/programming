package org.objectweb.proactive.extra.vfsprovider.client;

import java.net.URISyntaxException;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.GenericFileName;


public class ProActiveFileName extends GenericFileName {
    public static final String SERVICE_AND_FILE_PATH_SEPARATOR = "?proactive_vfs_provider_path=";

    public static final String VFS_PREFIX = "pap";

    public enum ProActiveProviderScheme {
        RMI(-1), RMISSH(-1), HTTP(80), IBIS(-1);

        private final int defaultPort;

        private ProActiveProviderScheme(int defaultPort) {
            this.defaultPort = defaultPort;
        }

        public String getServerScheme() {
            return name().toLowerCase();
        }

        public String getVFSScheme() {
            return VFS_PREFIX + getServerScheme();
        }

        public int getDefaultPort() {
            return defaultPort;
        }

        public static ProActiveProviderScheme forServerScheme(String serverScheme) {
            return ProActiveProviderScheme.valueOf(serverScheme);
        }

        public static ProActiveProviderScheme forVFSScheme(String vfsScheme) {
            if (!vfsScheme.startsWith(VFS_PREFIX)) {
                throw new IllegalArgumentException(vfsScheme + " is not a valid VFS server scheme");
            }
            final String strippedScheme = vfsScheme.substring(VFS_PREFIX.length());
            return ProActiveProviderScheme.valueOf(strippedScheme);
        }
    }

    public static String getServerVFSRootURL(String serverURL) throws URISyntaxException {
        final int dotIndex = serverURL.indexOf(':');
        if (dotIndex == -1) {
            throw new URISyntaxException(serverURL, "Could not find URL scheme");
        }
        final String schemeString = serverURL.substring(0, dotIndex);
        final String remainingPart = serverURL.substring(dotIndex);
        final ProActiveProviderScheme scheme = ProActiveProviderScheme.forServerScheme(schemeString);
        return scheme.getVFSScheme() + remainingPart + SERVICE_AND_FILE_PATH_SEPARATOR + SEPARATOR_CHAR;
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
        super(scheme, hostName, port, ProActiveProviderScheme.forVFSScheme(scheme).getDefaultPort(),
                userName, password, path, type);
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
        buffer.append(ProActiveProviderScheme.forVFSScheme(getScheme()).getServerScheme());
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
}
