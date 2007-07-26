package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.BridgeParsers;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.bridge.AbstractBridge;
import org.w3c.dom.Node;


public abstract class AbstractBridgeParser implements BridgeParser {
    protected AbstractBridge bridge;

    public AbstractBridgeParser() {
        bridge = createBridge();
    }

    public void parseBridgeNode(Node bridgeNode, XPath xpath) {
        String id = GCMParserHelper.getAttributeValue(bridgeNode, "id");
        bridge.setId(id);
    }

    public Bridge getBridge() {
        return bridge;
    }

    public abstract AbstractBridge createBridge();
}
