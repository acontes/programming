package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.Serializable;
import java.net.URISyntaxException;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ObjectForSynchro;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.DataSpacesNodes;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;


/**
 * Class that can be instantiated as an ActiveObject on a ProActive node to configure Data Spaces
 * support on that node.
 */
@ActiveObject
public class DataSpacesInstaller implements Serializable {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    private String namingServiceURL = null;
    private String nameForLoggers;

    public DataSpacesInstaller() {
    }

    /**
     * Constructor with Naming Service URL argument.
     *
     * @param url
     *            valid Naming Service URL
     */
    public DataSpacesInstaller(String url) {
        namingServiceURL = url;
    }

    /**
     * Start Data Spaces on a current node. This method can be separated into two - configure node
     * and then node for application.
     *
     * @param baseScratchConfiguration
     *            scratch space configuration for a node
     * @return object for synchronization
     */
    public ObjectForSynchro startDataSpaces(BaseScratchSpaceConfiguration baseScratchConfiguration)
            throws FileSystemException, NotConfiguredException, ProActiveException, URISyntaxException {

        final Node node = Utils.getCurrentNode();
        buildNameForLoggers(node);
        final String logMsg = nameForLoggers + " starts data spaces";
        logger.info(logMsg);

        DataSpacesNodes.configureNode(node, baseScratchConfiguration);
        DataSpacesNodes.configureApplication(node, namingServiceURL);

        return new ObjectForSynchro();
    }

    /**
     * Stops Data Spaces on a current node.
     *
     * @return object for synchronization
     */
    public ObjectForSynchro stopDataSpaces() throws NotConfiguredException {
        final Node node = Utils.getCurrentNode();
        final String logMsg = nameForLoggers + " stops data spaces";
        logger.info(logMsg);
        DataSpacesNodes.closeNodeConfig(node);

        return new ObjectForSynchro();
    }

    private void buildNameForLoggers(final Node node) {
        final String rtid = Utils.getRuntimeId(node);
        final String nodeid = Utils.getNodeId(node);
        final StringBuffer sb = new StringBuffer();

        sb.append("DataSpacesInstaller on ").append(rtid).append(" / ").append(nodeid);
        nameForLoggers = sb.toString();
    }
}
