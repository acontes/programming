package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.executable;

import java.util.Map;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.w3c.dom.Node;

public class ApplicationExecutable implements Application {
    protected static final String NODE_NAME = "executable";

    final private ApplicationExecutableBean configBean;
    
    public ApplicationExecutable() {
        configBean = new ApplicationExecutableBean();
    }
    
    public String getNodeName() {
        return NODE_NAME;
    }

    public void parse(Node node, XPath xpath, Map<String, NodeProvider> nodeProviders) throws Exception {
        ApplicationParserExecutable parser = new ApplicationParserExecutable(configBean, nodeProviders);
        parser.parseExecutableNode(node, xpath);
    }

}
