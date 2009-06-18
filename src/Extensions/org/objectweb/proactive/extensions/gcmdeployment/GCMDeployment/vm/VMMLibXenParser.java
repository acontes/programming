package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;


public class VMMLibXenParser extends AbstractVMMParser {

    static final String NODE_NAME = "libxen";

    @Override
    public AbstractVMM createVMM() {
        return new VMMLibXen();
    }

    @Override
    public String getNodeName() {
        return NODE_NAME;
    }

    public AbstractVMM parseVMMNode(Node vmmNode, XPath xpath) throws XPathExpressionException {

        //gathering hypervisor, user, pwd, image info
        AbstractVMM vmm = parseVMMNodeCommonInfo(vmmNode, xpath);

        //update vm info in case of PVM boot
        ArrayList<VMBean> vms = vmm.getVms();
        String userName = vmm.getUser();
        String userPwd = vmm.getPwd();
        for (VMBean vm : vms) {
            vm.setUserName(userName);
            vm.setUserPwd(userPwd);
        }
        return vmm;
    }
}
