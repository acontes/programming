/**
 *
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileName;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.ProActiveInet;


/**
 * Static utilities methods.
 */
public class Utils {

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
        if (PAActiveObject.getStubOnThis() == null)
            throw new ProActiveRuntimeException("This method must be called from an active thread");

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

        // FIXME not implemented in ProActive... 
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
        if (hostname != null && hostname.equals(getHostname()) && path != null) {
            // FIXME what about relative paths and windows support?
            if (path.startsWith("/")) {
                return "file://" + path;
            } else {
                return "file:///" + path;
            }
        }
        return url;
    }

    /**
     * Appends subdirectories to provided base location (path or URL), handling file separators
     * (slashes) in appropriate way.
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

        // TODO what about windows-like paths?
        final StringBuilder sb = new StringBuilder(baseLocation);
        boolean skipFirst = baseLocation.charAt(baseLocation.length() - 1) == FileName.SEPARATOR_CHAR;
        for (final String subDir : subDirs) {
            if (skipFirst)
                skipFirst = false;
            else
                sb.append(FileName.SEPARATOR_CHAR);
            sb.append(subDir);
        }
        return sb.toString();
    }
}
