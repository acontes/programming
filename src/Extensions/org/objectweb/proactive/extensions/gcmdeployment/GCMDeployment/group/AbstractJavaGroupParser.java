package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group;

import javax.xml.xpath.XPath;

import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.w3c.dom.Node;

public abstract class AbstractJavaGroupParser implements JavaGroupParser {

    @Override
    public AbstractJavaGroup parseGroupNode(Node groupNode, XPath xpath) {

        String id = GCMParserHelper.getAttributeValue(groupNode, "id");

        AbstractJavaGroup group = createGroup();

        group.setId(id);
        
        return group;
    }

    public abstract AbstractJavaGroup createGroup();
}
