package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GroupParsers;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.w3c.dom.Node;


public class SSHGroupParser extends AbstractGroupParser {
    public Group getGroup() {
        // TODO Auto-generated method stub
        return null;
    }

    public void parseGroupNode(Node groupNode, XPath xpath) {
        super.parseGroupNode(groupNode, xpath);

        String hostlist = GCMParserHelper.getAttributeValue(groupNode,
                "hostList");
        String domain = GCMParserHelper.getAttributeValue(groupNode, "domain");
        String username = GCMParserHelper.getAttributeValue(groupNode,
                "username");
        new PathElement(GCMParserHelper.getAttributeValue(groupNode,
                "commandPath"));

        // TODO
    }
}
