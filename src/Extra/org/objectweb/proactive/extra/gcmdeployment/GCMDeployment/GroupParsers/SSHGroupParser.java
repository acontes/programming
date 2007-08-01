package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GroupParsers;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.process.group.AbstractGroup;
import org.objectweb.proactive.extra.gcmdeployment.process.group.GroupSSH;
import org.w3c.dom.Node;


public class SSHGroupParser extends AbstractGroupParser {
    public void parseGroupNode(Node groupNode, XPath xpath) {
        super.parseGroupNode(groupNode, xpath);

        GroupSSH groupSSH = (GroupSSH) getGroup();

        String hostList = GCMParserHelper.getAttributeValue(groupNode,
                "hostList");

        groupSSH.setHostList(hostList);

        String domain = GCMParserHelper.getAttributeValue(groupNode, "domain");

        groupSSH.setDomain(domain);

        String username = GCMParserHelper.getAttributeValue(groupNode,
                "username");

        groupSSH.setUsername(username);

        String commandPath = GCMParserHelper.getAttributeValue(groupNode,
                "commandPath");

        groupSSH.setCommandPath(commandPath);
    }

    @Override
    public AbstractGroup createGroup() {
        return new GroupSSH();
    }
}
