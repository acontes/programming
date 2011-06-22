/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.gcmdeployment.technicalservice;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;
import functionalTests.GCMFunctionalTestDefaultNodes;


/**
 * Deployment descriptor technical services.
 */
public class TestOverriding extends FunctionalTest {
    private GCMApplication app;

    @Before
    public void before() throws ProActiveException {
        VariableContractImpl vContract = new VariableContractImpl();
        vContract.setVariableFromProgram(GCMFunctionalTestDefaultNodes.VAR_HOSTCAPACITY, "4",
                VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(GCMFunctionalTestDefaultNodes.VAR_VMCAPACITY, "1",
                VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(FunctionalTest.VAR_JVM_PARAMETERS, FunctionalTest.getJvmParameters()
                .toString(), VariableContractType.ProgramVariable);
        URL desc = this.getClass().getResource("TestOverridingApplication.xml");
        app = PAGCMDeployment.loadApplicationDescriptor(desc, vContract);
        app.startDeployment();
        app.waitReady();
    }

    @org.junit.Test
    public void action() throws Exception {
        GCMVirtualNode vn1 = app.getVirtualNode("VN1");
        GCMVirtualNode vn2 = app.getVirtualNode("VN2");
        GCMVirtualNode vn3 = app.getVirtualNode("VN3");
        GCMVirtualNode vn4 = app.getVirtualNode("VN4");

        Node node;

        node = vn1.getANode();
        Assert.assertEquals("application", node.getProperty("arg1"));

        node = vn2.getANode();
        Assert.assertEquals("VN2", node.getProperty("arg1"));

        node = vn3.getANode();
        Assert.assertEquals("NP1", node.getProperty("arg1"));

        node = vn4.getANode();
        Assert.assertEquals("NP1", node.getProperty("arg1"));
    }
}
