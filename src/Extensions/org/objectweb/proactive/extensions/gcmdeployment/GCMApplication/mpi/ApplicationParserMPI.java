package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.mpi;

import java.util.Collections;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ApplicationParserMPI {

    private static final String XPATH_NODE_PROVIDER = "app:nodeProvider";
    private static final String XPATH_PATH = "app:path";
    private static final String XPATH_COMMAND = "app:command";
    private static final String XPATH_ARG = "app:arg";


    final ApplicationMPIBean configBean;
    final Map<String, NodeProvider> nodeProviders;
    
    public ApplicationParserMPI(final ApplicationMPIBean configBean, final Map<String, NodeProvider> nodeProviders) {
        this.configBean = configBean;
        this.nodeProviders = Collections.unmodifiableMap(nodeProviders);
    }


    
    public void parseMPINode(Node appNode, XPath xpath)
            throws Exception {

        NodeList nodeProviderNodes;
        nodeProviderNodes = (NodeList) xpath.evaluate(XPATH_NODE_PROVIDER, appNode, XPathConstants.NODESET);
        for (int i = 0; i < nodeProviderNodes.getLength(); ++i) {
            Node npNode = nodeProviderNodes.item(i);
            String refid = GCMParserHelper.getAttributeValue(npNode, "refid");
            NodeProvider nodeProvider = nodeProviders.get(refid);
            configBean.addProvider(nodeProvider);
        }

        // path tag is optional
        Node pathNode = (Node) xpath.evaluate(XPATH_PATH, appNode, XPathConstants.NODE);
        if (pathNode != null) {
            configBean.setPath(GCMParserHelper.parsePathElementNode(pathNode));
        }

        // name
        Node commandNode = (Node) xpath.evaluate(XPATH_COMMAND, appNode, XPathConstants.NODE);
        String name = GCMParserHelper.getAttributeValue(commandNode, "name");
        configBean.setCommand(name);

        // command args
        NodeList argNodes = (NodeList) xpath.evaluate(XPATH_ARG, commandNode, XPathConstants.NODESET);
        for (int i = 0; i < argNodes.getLength(); ++i) {
            Node argNode = argNodes.item(i);
            String argVal = argNode.getFirstChild().getNodeValue();
            configBean.addArg(argVal);
        }
    }
}