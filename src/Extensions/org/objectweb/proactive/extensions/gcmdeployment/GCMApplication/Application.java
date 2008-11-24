package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import java.util.Map;

import javax.xml.xpath.XPath;

import org.w3c.dom.Node;

public interface Application {

    public String getNodeName();
    
    public void parse(Node node, XPath xpath, Map<String, NodeProvider> nodeProviders) throws Exception;
    
    
}
