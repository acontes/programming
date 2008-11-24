package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import java.util.Map;

import javax.xml.xpath.XPath;

import org.w3c.dom.Node;


public interface Application {
    /** 
     * Returns the name of the XML node associated to this application
     */
    public String getNodeName();

    /**
     * Parse the XML fragment related to this application
     * @param node the XML application node
     * @param xpath the XPath context
     * @param nodeProviders All the nodes providers defined in the GCM Application Descriptor
     * @throws Exception If any non recoverable error occurs during the parsing
     */
    public void parse(Node node, XPath xpath, Map<String, NodeProvider> nodeProviders) throws Exception;

    /**
     * The method is called by 
     * @param gcma
     * @throws Exception
     */
    public void configure(GCMApplicationInternal gcma) throws Exception;

    public void startDeployment();

    public void kill();

    public boolean isStarted();

    public void waitReady();
}
