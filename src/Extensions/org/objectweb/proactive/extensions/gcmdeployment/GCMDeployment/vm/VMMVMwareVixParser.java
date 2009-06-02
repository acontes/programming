package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM.Service;
import org.w3c.dom.Node;


public class VMMVMwareVixParser extends AbstractVMMParser {

    static final String NODE_NAME = "vmware-vix";
    static final String PA_HYPERVISOR_PORT = "port";
    static final String PA_HYPERVISOR_SERVICE = "service";
    static final String PA_SERVICE_SERVER = "server";
    static final String PA_SERVICE_WORKSTATION = "workstation";
    static final String PA_SERVICE_VI = "vi";

    @Override
    public String getNodeName() {
        return NODE_NAME;
    }

    @Override
    public AbstractVMM createVMM() {
        return new VMMVMwareVix();
    }

    public AbstractVMM parseVMMNode(Node vmmNode, XPath xpath) throws XPathExpressionException {

        //gathering hypervisor, user, pwd, image info
        AbstractVMM vmm = parseVMMNodeCommonInfo(vmmNode, xpath);

        VMMVMwareVix vmmvix = (VMMVMwareVix) vmm;
        String port = GCMParserHelper.getAttributeValue(vmmNode, PA_HYPERVISOR_PORT);
        String service = GCMParserHelper.getAttributeValue(vmmNode, PA_HYPERVISOR_SERVICE);
        if (port != null)
            vmmvix.setPort(Integer.parseInt(port));
        if (service != null) {
            if (service.equals(PA_SERVICE_SERVER))
                vmmvix.setService(Service.vmwareServer);
            if (service.equals(PA_SERVICE_WORKSTATION))
                vmmvix.setService(Service.vmwareWorkstation);
            if (service.equals(PA_SERVICE_VI))
                vmmvix.setService(Service.vmwareServerVI);
        } else
            vmmvix.setService(Service.vmwareDefault);

        return vmmvix;
    }

}
