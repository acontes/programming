/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.core.node.Node;

/**
 * Class that provides static method for accessing node and application specific
 * class instances. Also helps with configuring node and / or application for a
 * node.
 */
public class DataSpacesNodes {

	private static Map<String, NodeConfigurator> nodeConfigurators = new HashMap<String, NodeConfigurator>();

	private static Map<String, DataSpacesImpl> dataSpacesImpls = new HashMap<String, DataSpacesImpl>();

	/**
	 * Returns NodeConfigurator for a specified node or creates a new one if not
	 * already created.
	 *
	 * @param node
	 * @return {@link NodeConfigurator}
	 */
	public static synchronized NodeConfigurator getNodeConfigurator(Node node) {
		final String name = extractNodeId(node);

		if (nodeConfigurators.containsKey(name))
			return nodeConfigurators.get(name);

		final NodeConfigurator configurator = new NodeConfigurator();
		nodeConfigurators.put(name, configurator);
		return configurator;
	}

	/**
	 * Returns DataSpacesImpl instances for a node with configured application.
	 * This should be called after obtaining NodeConfigurator for a node with
	 * <code>getNodeConfigurator</code>, configuring it and applying settings
	 * for application on that node.
	 *
	 * @param node
	 *            specified node
	 * @return {@link DataSpacesImpl}
	 * @throws IllegalArgumentException
	 *             when NodeConfigurator has not been created for a specified
	 *             node or no application has been configured for a specified
	 *             node.
	 */
	public static synchronized DataSpacesImpl getDataSpacesImpl(Node node) throws IllegalArgumentException {
		final String name = extractNodeId(node);

		if (dataSpacesImpls.containsKey(name))
			return dataSpacesImpls.get(name);

		if (!nodeConfigurators.containsKey(name))
			throw new IllegalArgumentException("NodeConfigurator for given node not found.");

		final NodeConfigurator nodeConfigurator = nodeConfigurators.get(name);
		final NodeApplicationConfigurator appConfigurator = nodeConfigurator.getNodeApplicationConfigurator();

		if (appConfigurator == null)
			throw new IllegalArgumentException("Specified node does not containe configured application.");

		final DataSpacesImpl impl = appConfigurator.getDataSpaceImpl();
		dataSpacesImpls.put(name, impl);
		return impl;
	}

	/**
	 * Helper method that configures a node. @see {@link NodeConfigurator}
	 *
	 * @param node
	 *            Node instance to configure
	 * @param spaceConfiguration
	 *            Configuration of scratch data space for a specified node
	 *
	 */
	public static void doConfigureNode(Node node, SpaceConfiguration spaceConfiguration) {
		final NodeConfigurator nconfig = getNodeConfigurator(node);
		nconfig.configureNode(spaceConfiguration);
	}

	/**
	 * Helper method that configures an application on an already configured
	 * node (@see {@link #doConfigureNode(Node, SpaceConfiguration)}). Created
	 * {@link DataSpaces} can be obtained by calling
	 * {@link #getDataSpacesImpl(Node)}.
	 *
	 * @param node
	 *            Node instance to configure application on
	 * @param appid
	 *            Identifier of an application
	 * @param namingServiceURL
	 *            URL of a Naming Service to connect to
	 */
	public static void doConfigureApplication(Node node, long appid, String namingServiceURL) {
		final NodeConfigurator nconfig = getNodeConfigurator(node);
		final DataSpacesImpl dsImpl = nconfig.configureApplication(appid, namingServiceURL);
		final String nname = extractNodeId(node);

		putDataSpaceImplSynch(nname, dsImpl);
	}

	/**
	 * Synchronized <code>map.put</code> wrapper.
	 *
	 * @param nname
	 * @param impl
	 */
	private static synchronized void putDataSpaceImplSynch(String nname, DataSpacesImpl impl) {
		dataSpacesImpls.put(nname, impl);
	}

	private static String extractNodeId(final Node node) {
		return node.getNodeInformation().getName();
	}
}
