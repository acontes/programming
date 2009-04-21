/**
 *
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URI;
import java.net.URISyntaxException;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
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
	 * Returns an identifier of specified Node.
	 * 
	 * @param node
	 * @return
	 */
	public static String getNodeId(final Node node) {
		return node.getNodeInformation().getName();
	}

	/**
	 * Returns identifier of an Active Object of current active thread.
	 * 
	 * @return
	 * @throws ProActiveRuntimeException
	 *             when not called from an active thread
	 */
	public static String getCurrentActiveObjectId() throws ProActiveRuntimeException {
		if (PAActiveObject.getStubOnThis() == null)
			throw new ProActiveRuntimeException("This method must be called from an active thread");

		UniqueID uid = PAActiveObject.getBodyOnThis().getID();
		return uid.toString();
	}

	/**
	 * Returns Node for current active thread.
	 * 
	 * @return
	 * @throws ProActiveRuntimeException
	 *             when internal PA exception on node acquisition or not called
	 *             from an active thread
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
	 * Connects to a remote NamingService object under specified URL.
	 * 
	 * @param url
	 *            to connect
	 * @return stub
	 * @throws ProActiveException
	 *             when PA exception occurs (communication error)
	 * @throws URISyntaxException
	 *             when URL cannot be parsed
	 */
	public static NamingService createNamingServiceStub(String url) throws ProActiveException,
			URISyntaxException {

		NamingService stub = (NamingService) PARemoteObject.lookup(new URI(url));
		return stub;
		// return new NamingService();
	}

	/**
	 * Empty.
	 * 
	 * @param stub
	 */
	public static void closeNamingServiceStub(NamingService stub) {
		// nothing here
	}
}
