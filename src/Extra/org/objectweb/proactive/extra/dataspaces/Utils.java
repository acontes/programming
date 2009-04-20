/**
 *
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.ProActiveInet;

// FIXME make these methods signatures more consistent
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

	public static Node getNodeForThis() {
		// FIXME what if not called from AO?
		try {
			return PAActiveObject.getNode();
		} catch (NodeException e) {
			throw new ProActiveRuntimeException("DataSpaces catched exception that should not occure", e);
		}
	}

	public static NamingService createNamingServiceStub(String url) {
		// TODO
		return new NamingService();
	}

	public static void closeNamingServiceStub(NamingService stub) {
		// TODO Auto-generated method stub
	}
}