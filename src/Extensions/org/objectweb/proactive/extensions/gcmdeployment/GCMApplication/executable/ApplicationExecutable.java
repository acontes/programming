package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.executable;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.Application;
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

    public void parse(Node node, XPath xpath) throws Exception {
        ApplicationParserExecutable parser = new ApplicationParserExecutable(configBean);
        parser.parseExecutableNode(node, xpath);
    }

}
