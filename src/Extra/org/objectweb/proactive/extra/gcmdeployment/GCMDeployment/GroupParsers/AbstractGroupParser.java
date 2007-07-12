package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GroupParsers;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extra.gcmdeployment.process.group.AbstractGroup;
import org.w3c.dom.Node;

public interface AbstractGroupParser {
    
    public void parseGroupNode(Node groupNode, XPath xpath);

    public AbstractGroup getGroup();
}
