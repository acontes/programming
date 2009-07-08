package org.objectweb.proactive.extra.vfsprovider.client;

import java.net.URISyntaxException;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.GenericFileName;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;


/**
 * Representation of VFS file name for ProActive file access protocol, served by
 * {@link FileSystemServer}.
 * <p>
 * File name representation is a bit unusual for that protocol, although it bases on URL, with
 * particular scheme and specific path part interpretation.<br>
 * Scheme part of URL is one of supported protocols scheme defined in
 * {@link ProActiveProviderScheme} with prefix defined by {@link #VFS_PREFIX}. Prefix discriminates
 * scheme of pure transport protocol (like RMI) from scheme of ProActive file access protocol using
 * that transport (like file access over RMI). e.g. <code>paphttp://</code>, <code>paprmi://</code><br>
 * Path part of URL is divided into two subparts separated by
 * {@link #SERVICE_AND_FILE_PATH_SEPARATOR}. First part defines path to the service implementing
 * {@link FileSystemServer} - see {@link #getServicePath()}. While second part defines file path
 * within that remote file system - see {@link #getPath()} or {@link #getPathDecoded()}. e.g. of
 * complete path part of URL consisting of two subparts:
 * <code>/nodeX/fileSystemServer?proactive_vfs_provider_path=/dir/file.txt</code>.
 * <p>
 * Given that structure of file name/URL, having such an URL it is possible to determine URL of pure
 * {@link FileSystemServer} service and path within that server. e.g. of complete URL is:
 * <code>paprmi://host.com/nodeX/fileSystemServer?proactive_vfs_provider_path=/dir/file.txt</code>.
 * Corresponding URL of {@link FileSystemServer} service is
 * <code>rmi://host.com/nodeX/fileSystemServer</code>, while path described by that URL is
 * <code>/dir/file.txt</code>.
 */
public class ProActiveFileName extends GenericFileName {
    /**
     * String used in path part of URL, to separate service path and file path.
     */
    public static final String SERVICE_AND_FILE_PATH_SEPARATOR = "?proactive_vfs_provider_path=";

    /**
     * Prefix for scheme of transport protocol used by ProActive file access protocol.
     */
    public static final String VFS_PREFIX = "pap";

    /**
     * Supported protocol scheme with default port, defining both scheme for pure transport protocol
     * and scheme with prefix {@link ProActiveFileName#VFS_PREFIX}, representing ProActive file
     * access protocol based on that transport.
     */
    public enum ProActiveProviderScheme {
        RMI(-1), RMISSH(-1), HTTP(80), IBIS(-1);

        private final int defaultPort;

        private ProActiveProviderScheme(int defaultPort) {
            this.defaultPort = defaultPort;
        }

        /**
         * @return pure transport scheme (without prefix)
         */
        public String getServerScheme() {
            return name().toLowerCase();
        }

        /**
         * @return ProActive file access protocol scheme (with prefix)
         */
        public String getVFSScheme() {
            return VFS_PREFIX + getServerScheme();
        }

        /**
         * @return default port for transport protocol; <code>-1</code> if undefined/unknown.
         */
        public int getDefaultPort() {
            return defaultPort;
        }

        /**
         * Gives ProActiveProviderScheme instance for given transport protocol scheme.
         * 
         * @param serverScheme
         *            transport protocol scheme
         * @return corresponding ProActiveProviderScheme instance
         * @throws IllegalArgumentException
         *             when this protocol is not supported
         */
        public static ProActiveProviderScheme forServerScheme(String serverScheme) {
            return ProActiveProviderScheme.valueOf(serverScheme.toUpperCase());
        }

        /**
         * Gives ProActiveProviderScheme instance for given ProActive file access protocol scheme.
         * 
         * @param serverScheme
         *            ProActive file access protocol scheme
         * @return corresponding ProActiveProviderScheme instance
         * @throws IllegalArgumentException
         *             when this protocol is not supported
         */
        public static ProActiveProviderScheme forVFSScheme(String vfsScheme) {
            if (!vfsScheme.startsWith(VFS_PREFIX)) {
                throw new IllegalArgumentException(vfsScheme + " is not a valid VFS server scheme");
            }
            final String strippedScheme = vfsScheme.substring(VFS_PREFIX.length());
            return ProActiveProviderScheme.valueOf(strippedScheme.toUpperCase());
        }
    }

    /**
     * Creates VFS URL of a root file for given {@link FileSystemServer} URL.
     * 
     * @param serverURL
     *            {@link FileSystemServer} URL
     * @return VFS URL of root of remote file system exposed by provided server
     * @throws URISyntaxException
     *             when given URL does not conform to expected URL syntax (no scheme defined)
     * @throws IllegalArgumentException
     *             when scheme of given URL is not supported, i.e. is not one of
     *             {@link ProActiveProviderScheme}
     */
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

    private final String servicePath;

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
            servicePath, absPath, type);
    }

    /**
     * @return path of a {@link FileSystemServer} service. Not including file path.
     */
    public String getServicePath() {
        return servicePath;
    }

    /**
     * @return URL of {@link FileSystemServer} for that file name.
     */
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
