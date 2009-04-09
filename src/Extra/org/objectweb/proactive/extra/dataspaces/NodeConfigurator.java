/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.core.node.Node;

/**
 * resp: - maintaines node immutable configuration (scratch configuration): 1.
 * configures VFS manager 2. stores scratch SpaceConfiguration 3. initializes &
 * tests scratch data space - for node configuration for application (app id,
 * naming service) creates and initializes NodeApplicationConfigurator - obtains
 * DataSpacesImpl from NodeApplicationConfigurator
 * 
 */
public class NodeConfigurator {

	/**
	 * init?
	 * 
	 * @param config
	 *            scratch data space configuration
	 */
	public void configureNode(SpaceConfiguration config) {

	}

	public DataSpacesImpl configureApplication(long appid, String namingServiceURL) {
		return null;
	}

	public NodeApplicationConfigurator getNodeApplicationConfigurator() {
		return null;
	}

	public DataSpacesImpl getDataSpacesImpl(Node n) {
		return null;
	}

	public void close() {

	}
}
