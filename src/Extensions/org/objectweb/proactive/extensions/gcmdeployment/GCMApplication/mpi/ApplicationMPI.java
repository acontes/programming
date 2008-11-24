package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.mpi;

import java.util.Map;

import javax.xml.xpath.XPath;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.w3c.dom.Node;


public class ApplicationMPI implements Application {
    protected static final Logger logger = ProActiveLogger.getLogger(GCMDeploymentLoggers.GCM_APPLICATION +
        ".mpi");

    private static final String NODE_NAME = "mpi";

    final private ApplicationMPIBean configBean;

    public ApplicationMPI() {
        configBean = new ApplicationMPIBean();
    }

    public String getNodeName() {
        return NODE_NAME;
    }

    public void parse(Node node, XPath xpath, Map<String, NodeProvider> nodeProviders) throws Exception {
        ApplicationParserMPI parser = new ApplicationParserMPI(configBean, nodeProviders);
        parser.parseMPINode(node, xpath);
    }

}
