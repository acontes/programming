package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.BridgeParsers;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.process.bridge.AbstractBridge;
import org.objectweb.proactive.extra.gcmdeployment.process.bridge.BridgeSSH;
import org.w3c.dom.Node;


public class SSHBridgeParser extends AbstractBridgeParser {
    @Override
    public void parseBridgeNode(Node bridgeNode, XPath xpath) {
        super.parseBridgeNode(bridgeNode, xpath);
        
        String hostname = GCMParserHelper.getAttributeValue(bridgeNode, "hostname");
        String username = GCMParserHelper.getAttributeValue(bridgeNode, "username");
        
        BridgeSSH bridgeSSH = ((BridgeSSH)bridge);
        bridgeSSH.setHostname(hostname);
        bridgeSSH.setUsername(username);
    }

    @Override
    public AbstractBridge createBridge() {
        return new BridgeSSH();
    }
}
