/**
 *
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.ProActiveInet;

/**
 * Static utilities methods.
 */
public class Utils {

	private Utils() {
	}

	public static String getHostnameForThis() {
		// InetAddress.getLocalHost().getHostName();
		return ProActiveInet.getInstance().getInetAddress().getHostName();
	}

	/**
	 * @param node
	 * @return
	 */
	public static String extractNodeId(final Node node) {
		return node.getNodeInformation().getName();
	}

	/**
	 * @return
	 */
	public static String extractAOId() {
		PAActiveObject.getJobId();
		return null;
	}

}
