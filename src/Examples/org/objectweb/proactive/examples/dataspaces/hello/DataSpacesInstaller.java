package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.Serializable;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ObjectForSynchro;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.core.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.core.DataSpacesNodes;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;


/**
 * Class that can be instantiated as an ActiveObject on a ProActive node to support configuration of
 * Data Spaces on that node.
 * 
 * @deprecated Data Spaces configuration is integrated into GCM Deployment.
 */
@ActiveObject
public class DataSpacesInstaller implements Serializable {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    public DataSpacesInstaller() {
    }

    /**
     * Configures Data Spaces for an application on a current node with given scratch configuration
     * and naming service URL.
     * <p>
     * This method assumes that application id was already assigned to its node.
     * 
     * @param baseScratchConfiguration
     *            scratch space configuration for a node, may be <code>null</code>
     * @param namingServiceURL
     *            valid Naming Service URL
     * @return object for synchronization
     * @throws FileSystemException
     * @throws ProActiveException
     * @throws URISyntaxException
     */
    public ObjectForSynchro startDataSpaces(BaseScratchSpaceConfiguration baseScratchConfiguration,
            String namingServiceURL) throws FileSystemException, ProActiveException, URISyntaxException {
        logger.info(getNameForLoggers() + " starts data spaces");

        final Node node = Utils.getCurrentNode();
        // these two calls could be possibly separated to 2 differents methods
        DataSpacesNodes.configureNode(node, baseScratchConfiguration);
        DataSpacesNodes.configureApplication(node, namingServiceURL);

        return new ObjectForSynchro();
    }

    /**
     * Stops Data Spaces completely (node and application-specific configuration) on a current node.
     * 
     * @return object for synchronization
     */
    public ObjectForSynchro stopDataSpaces() throws NotConfiguredException {
        logger.info(getNameForLoggers() + " stops data spaces");

        DataSpacesNodes.closeNodeConfig(Utils.getCurrentNode());

        return new ObjectForSynchro();
    }

    private String getNameForLoggers() {
        final Node node = Utils.getCurrentNode();
        final String rtId = Utils.getRuntimeId(node);
        final String nodeId = Utils.getNodeId(node);
        final long appId = Utils.getApplicationId(node);
        return String.format("DataSpacesInstaller on runtime %s / node %s / application %d", rtId, nodeId,
                appId);
    }
}
