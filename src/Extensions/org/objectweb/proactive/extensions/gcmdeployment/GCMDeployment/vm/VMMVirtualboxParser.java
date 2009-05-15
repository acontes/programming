package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;


public class VMMVirtualboxParser extends AbstractVMMParser {

    static final String NODE_NAME = "virtualbox-ws";

    public String getNodeName() {
        return NODE_NAME;
    }

    public AbstractVMM parseVMMNode(Node vmmNode, XPath xpath) throws XPathExpressionException {

        //gathering hypervisor, user, pwd, image info
        AbstractVMM vmm = parseVMMNodeCommonInfo(vmmNode, xpath);

        return vmm;
    }

    @Override
    public AbstractVMM createVMM() {
        return new VMMVirtualbox();
    }

}
