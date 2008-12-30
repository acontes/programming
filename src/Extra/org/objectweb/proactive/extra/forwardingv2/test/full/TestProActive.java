package org.objectweb.proactive.extra.forwardingv2.test.full;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.forwardingv2.registry.ForwardingRegistry;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;


public class TestProActive extends FunctionalTest {
    ForwardingRegistry forwarder;
    Node remoteNode;

    @Before
    public void b() throws ProActiveException {
        System.err.println("\ntiti\n");
        this.forwarder = new ForwardingRegistry(0, true);
        System.err.println("\ntoto\n");

        PAProperties.PA_COMMUNICATION_PROTOCOL.setValue("pamr");
        //		PAProperties.PA_NET_ROUTER_ADDRESS.setValue(this.forwarder.getInetAddress().getCanonicalHostName());
        PAProperties.PA_NET_ROUTER_ADDRESS.setValue("localhost");
        PAProperties.PA_NET_ROUTER_PORT.setValue(this.forwarder.getLocalPort());

        VariableContractImpl vc = new VariableContractImpl();
        vc.setVariableFromProgram("router.address", PAProperties.PA_NET_ROUTER_ADDRESS.getValue(),
                VariableContractType.ProgramVariable);
        vc.setVariableFromProgram("router.port", PAProperties.PA_NET_ROUTER_PORT.getValue(),
                VariableContractType.ProgramVariable);

        URL gcmaUrl = this.getClass().getResource("gcma.xml");
        GCMApplication gcma = PAGCMDeployment.loadApplicationDescriptor(gcmaUrl, vc);
        gcma.startDeployment();
        GCMVirtualNode vn = gcma.getVirtualNode("vn");
        this.remoteNode = vn.getANode();
    }

    @Test
    public void test() {
        Assert.assertNotNull(this.remoteNode);
    }
}
