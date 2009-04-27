/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;


/**
 * Class that provides static methods for managing and accessing node and application specific Data
 * Spaces classes instances.
 * <p>
 * This class may be used for configuring Data Spaces nodes through {@link NodeConfigurator} and
 * accessing configured {@link DataSpacesImpl} instances.
 * 
 * @see DataSpacesImpl
 * @see NodeConfigurator
 */
public class DataSpacesNodes {

    private static Map<String, NodeConfigurator> nodeConfigurators = new HashMap<String, NodeConfigurator>();

    /**
     * Returns DataSpacesImpl instance for a node with configured application.
     * 
     * This method is usable after setting up this node with
     * {@link #configureNode(Node, SpaceConfiguration)} and
     * {@link #configureApplication(Node, String)} calls.
     * 
     * Returned instance is usable while node is kept configured for the application.
     * 
     * @param node
     *            node that is asked for Data Spaces implementation
     * @return configured Data Spaces implementation for application
     * @throws NotConfiguredException
     *             when node is not configured for Data Spaces or application-specific Data Spaces
     *             configuration is not applied on this node
     * @see NodeConfigurator#getDataSpacesImpl()
     */
    public static DataSpacesImpl getDataSpacesImpl(Node node) throws NotConfiguredException {
        final NodeConfigurator nodeConfig = getOrFailNodeConfigurator(node);
        return nodeConfig.getDataSpacesImpl();
    }

    /**
     * Configures Data Spaces on node and stores that configuration, so it can be later configured
     * for specific application by {@link #configureApplication(Node, String)} or closed by
     * {@link #closeNodeConfig(Node)}.
     * 
     * @param node
     *            node to be configured for Data Spaces
     * @param baseScratchConfiguration
     *            base configuration of scratch data space for a specified node
     * @throws FileSystemException
     *             when VFS configuration creation or scratch initialization fails
     * @throws ConfigurationException
     *             something failed during node scratch space configuration (ex. capabilities
     *             checking)
     * @see NodeConfigurator#configureNode(SpaceConfiguration, Node)
     */
    public static void configureNode(Node node, BaseScratchSpaceConfiguration baseScratchConfiguration)
            throws AlreadyConfiguredException, FileSystemException, ConfigurationException {
        final NodeConfigurator nodeConfig = getOrCreateNodeConfigurator(node);
        try {
            nodeConfig.configureNode(baseScratchConfiguration, node);
        } catch (AlreadyConfiguredException x) {
            // it should never happen in our usage
            throw new RuntimeException(x);
        }
    }

    /**
     * Configures Data Spaces node for a specific application and stores that configuration together
     * with Data Spaces implementation instance, so they can be later accessed by
     * {@link #getDataSpacesImpl(Node)} or closed through
     * {@link #tryCloseNodeApplicationConfig(Node)} or subsequent
     * {@link #configureApplication(Node, String)}.
     * 
     * This method can be called on an already configured node (see
     * {@link #configureNode(Node, SpaceConfiguration)}) or even already application-configured node
     * - in that case previous application configuration is closed before applying a new one.
     * 
     * @param node
     *            node to be configured for Data Spaces application
     * @param namingServiceURL
     *            URL of a Naming Service to connect to
     * @throws URISyntaxException
     *             when exception occurred on namingServiceURL parsing
     * @throws ProActiveException
     *             occurred during contacting with NamingService
     * @throws FileSystemException
     *             VFS related exception during scratch data space creation
     * @see NodeConfigurator#configureApplication(Node, long, String)
     */
    public static void configureApplication(Node node, String namingServiceURL) throws ProActiveException,
            URISyntaxException, FileSystemException {

        final NodeConfigurator nodeConfig = getOrFailNodeConfigurator(node);
        nodeConfig.configureApplication(namingServiceURL);
    }

    /**
     * Closes all node related configuration (possibly including application related node
     * configuration).
     * 
     * Subsequent calls for node that is not configured anymore may result in undefined behavior.
     * 
     * @param node
     *            node to be deconfigured for Data Spaces
     * @throws NotConfiguredException
     *             when node has not been configured yet
     */
    public static void closeNodeConfig(Node node) throws NotConfiguredException {
        final NodeConfigurator nodeConfig = removeOrFailNodeConfigurator(node);
        nodeConfig.close();
    }

    /**
     * Closes all application related configuration for node if there is one.
     * 
     * @param node
     *            node to be deconfigured for Data Spaces application
     */
    public static void tryCloseNodeApplicationConfig(Node node) {
        final NodeConfigurator nodeConfig = getNodeConfigurator(node);
        if (nodeConfig != null)
            nodeConfig.tryCloseAppConfigurator();
    }

    private static NodeConfigurator getNodeConfigurator(Node node) {
        final String name = Utils.getNodeId(node);

        synchronized (nodeConfigurators) {
            return nodeConfigurators.get(name);
        }
    }

    private static NodeConfigurator getOrCreateNodeConfigurator(Node node) {
        final String name = Utils.getNodeId(node);

        synchronized (nodeConfigurators) {
            final NodeConfigurator config = nodeConfigurators.get(name);
            if (config != null)
                return config;

            final NodeConfigurator newConfig = new NodeConfigurator();
            nodeConfigurators.put(name, newConfig);
            return newConfig;
        }
    }

    private static NodeConfigurator getOrFailNodeConfigurator(Node node) throws NotConfiguredException {
        final String name = Utils.getNodeId(node);

        synchronized (nodeConfigurators) {
            final NodeConfigurator config = nodeConfigurators.get(name);
            if (config == null)
                throw new NotConfiguredException("Node is not configured");

            return config;
        }
    }

    private static NodeConfigurator removeOrFailNodeConfigurator(Node node) throws NotConfiguredException {
        final String name = Utils.getNodeId(node);

        synchronized (nodeConfigurators) {
            final NodeConfigurator config = nodeConfigurators.remove(name);
            if (config == null)
                throw new NotConfiguredException("Node is not configured");

            return config;
        }
    }
}
