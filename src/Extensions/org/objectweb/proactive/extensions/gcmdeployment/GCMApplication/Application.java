package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import javax.xml.xpath.XPath;

import org.w3c.dom.Node;

public interface Application {

    public String getNodeName();
    
    public void parse(Node node, XPath xpath) throws Exception;
}
