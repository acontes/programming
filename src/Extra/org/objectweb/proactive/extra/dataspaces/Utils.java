/**
 *
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.regex.Pattern;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


/**
 * Static utilities methods.
 */
public class Utils {

    private static final Pattern WINDOWS_DRIVE_PATTERN = Pattern.compile("^[a-zA-Z]:\\\\.*");

    private Utils() {
    }

    /**
     * @see {@link ProActiveInet#getHostname()}
     * @return
     */
    public static String getHostname() {
        // InetAddress.getLocalHost().getHostName();
        return ProActiveInet.getInstance().getHostname();
    }

    /**
     * Returns an identifier of a Runtime of specified Node.
     * 
     * @param node
     * @return
     */
    public static String getRuntimeId(Node node) {
        final ProActiveRuntime rt = node.getProActiveRuntime();
        return rt.getVMInformation().getName();
    }

    /**
     * Returns an identifier of specified Node.
     * 
     * @param node
     * @return
     */
    public static String getNodeId(final Node node) {
        return node.getNodeInformation().getName();
    }

    /**
     * Returns identifier of an Active Object Body.
     * 
     * @return
     */
    public static String getActiveObjectId(Body body) {
        UniqueID uid = body.getID();
        return uid.toString();
    }

    /**
     * Returns Body of an Active Object of a current active thread.
     * 
     * @return
     * @throws ProActiveRuntimeException
     *             when not called from an active thread
     */
    public static Body getCurrentActiveObjectBody() throws ProActiveRuntimeException {
        // FIXME: depends on PROACTIVE-658
        if (PAActiveObject.getStubOnThis() == null)
            throw new ProActiveRuntimeException("This method must be called from an active thread");

        return PAActiveObject.getBodyOnThis();
    }

    /**
     * Returns Node for current active thread.
     * 
     * @return
     * @throws ProActiveRuntimeException
     *             when internal PA exception on node acquisition or not called from an active
     *             thread
     */
    public static Node getCurrentNode() throws ProActiveRuntimeException {
        if (PAActiveObject.getStubOnThis() == null) {
            // TODO: what about returning NodeFactory.getDefaultNode() in that case?
            // it may be useful on deployer's node
            throw new ProActiveRuntimeException("This method must be called from an active thread");
        }

        try {
            return PAActiveObject.getNode();
        } catch (NodeException e) {
            throw new ProActiveRuntimeException("DataSpaces catched exception that should not occure", e);
        }
    }

    /**
     * Returns an application id that is registered on a node.
     * 
     * @return
     */
    public static long getApplicationId(Node node) {

        // FIXME not implemented in ProActive... (depends on PROACTIVE-661 story)
        return 0;
    }

    /**
     * Determines local access URL for accessing some data, basing on provided remote access URL,
     * local access path and hostname specification.
     * <p>
     * If local access path is provided among with hostname, they are preferred over remote access
     * URL if local hostname determined by {@link #getHostname()} matches provided one.
     * 
     * @param url
     *            mandatory remote access URL
     * @param path
     *            path for local access on host with hostname as specified in hostname argument; may
     *            be <code>null</code> when local access path is unspecified
     * @param hostname
     *            hostname where local access path is valid; can be <code>null</code> only when
     *            local access path is unspecified
     * @return local access URL that should be used for this host
     */
    public static String getLocalAccessURL(final String url, final String path, final String hostname) {
        if (hostname != null && hostname.equals(getHostname()) && path != null)
            return path;
        return url;
    }

    /**
     * Appends subdirectories to provided base location (local path or URL), handling file
     * separators (slashes) in appropriate way.
     * <p>
     * Both Unix- and Windows-like paths are supported and should be recognized by looking for
     * Windows-like drive letter at the beginning of a path.
     * 
     * @param baseLocation
     *            Base location (path or URL) which is the root for appended subdirectories. Can be
     *            <code>null</code>.
     * @param subDirs
     *            Any number of subdirectories to be appended to provided location. Order of
     *            subdirectories corresponds to directories hierarchy and result path. None of it
     *            can be <code>null</code> .
     * @return location with appended subdirectories with appropriate slashes (separators).
     *         <code>null</code> if <code>basePath</code> is <code>null</code>.
     */
    public static String appendSubDirs(final String baseLocation, final String... subDirs) {
        if (baseLocation == null)
            return null;

        final char separator;
        if (isWindowsPath(baseLocation))
            separator = '\\';
        else
            separator = '/';

        final StringBuilder sb = new StringBuilder(baseLocation);
        boolean skipFirst = baseLocation.endsWith(Character.toString(separator));
        for (final String subDir : subDirs) {
            if (skipFirst)
                skipFirst = false;
            else
                sb.append(separator);
            sb.append(subDir);
        }
        return sb.toString();
    }

    /**
     * Assert that given file system has required capabilities. Throw an ConfigurationException if
     * it does not.
     *
     * @param expected
     *            array containing expected capabilities of the specified file system.
     * @param fs
     *            specified file system
     * @throws ConfigurationException
     *             when the file system does not have one of expected capabilities
     */
    public static void assertCapabilitiesMatch(Capability[] expected, FileSystem fs)
            throws ConfigurationException {

        for (int i = 0; i < expected.length; i++) {
            final Capability capability = expected[i];

            if (!fs.hasCapability(capability))
                throw new ConfigurationException(
                    "File system used to access data does not support capability: " + capability);
        }
    }

    /**
     * Assert that given FileObject's file system has required capabilities. Throw an
     * ConfigurationException if it does not.
     *
     * @param expected
     *            array containing expected capabilities of the specified FileObject's file system.
     * @param fo
     *            specified FileObject
     * @throws ConfigurationException
     *             when the FileObject's file system does not have one of expected capabilities
     */
    public static void assertCapabilitiesMatch(Capability[] expected, FileObject fo)
            throws ConfigurationException {

        assertCapabilitiesMatch(expected, fo.getFileSystem());
    }

    private static boolean isWindowsPath(String location) {
        return WINDOWS_DRIVE_PATTERN.matcher(location).matches();
    }
}
