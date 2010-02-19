/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.descriptor.fractaladl;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.etsi.uri.gcm.util.GCM;
import org.junit.After;
import org.junit.Assert;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.core.component.adl.Registry;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.util.OperatingSystem;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;

import functionalTests.ComponentTest;
import functionalTests.GCMFunctionalTest;
import functionalTests.GCMFunctionalTestDefaultNodes;
import functionalTests.component.I1Multicast;
import functionalTests.component.Message;
import functionalTests.component.PrimitiveComponentA;
import functionalTests.component.PrimitiveComponentB;


/**
 * For a graphical representation, open the MessagePassingExample.fractal with the fractal defaultGui
 *
 * This test verifies the parsing and building of a component system using a customized Fractal ADL,
 * and tests new features such as exportation of virtual nodes and cardinality of virtual nodes.
 * It mixes exported and non-exported nodes to make sure these work together.
 *
 * @author The ProActive Team
 */
public class Test extends ComponentTest {

    /**
     *
     */
    public static String MESSAGE = "-->m";
    private List<Message> messages;

    private GCMApplication newDeploymentDescriptor = null;
    private ProActiveDescriptor oldDeploymentDescriptor = null;

    public Test() {
        super("Virtual node exportation / composition in the Fractal ADL",
                "Virtual node exportation / composition in the Fractal ADL");
    }

    @org.junit.Test
    public void testOldDeployment() throws Exception {
        oldDeploymentDescriptor = PADeployment.getProactiveDescriptor(Test.class.getResource(
                "/functionalTests/component/descriptor/deploymentDescriptor.xml").getPath(),
                (VariableContractImpl) super.vContract.clone());

        useFractalADL(oldDeploymentDescriptor);
    }

    @org.junit.Test
    public void testGCMDeployment() throws Exception {
        URL descriptorPath = Test.class
                .getResource("/functionalTests/component/descriptor/applicationDescriptor.xml");

        vContract.setVariableFromProgram(GCMFunctionalTest.VAR_OS, OperatingSystem.getOperatingSystem()
                .name(), VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(GCMFunctionalTestDefaultNodes.VAR_HOSTCAPACITY, Integer.valueOf(4)
                .toString(), VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(GCMFunctionalTestDefaultNodes.VAR_VMCAPACITY, Integer.valueOf(1)
                .toString(), VariableContractType.DescriptorDefaultVariable);

        newDeploymentDescriptor = PAGCMDeployment.loadApplicationDescriptor(descriptorPath,
                (VariableContractImpl) super.vContract.clone());

        newDeploymentDescriptor.startDeployment();
        useFractalADL(newDeploymentDescriptor);
    }

    private void useFractalADL(Object deploymentDesc) throws Exception {
        Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<String, Object> context = new HashMap<String, Object>();

        context.put("deployment-descriptor", deploymentDesc);
        Component root = (Component) f.newComponent(
                "functionalTests.component.descriptor.fractaladl.MessagePassingExample", context);
        GCM.getGCMLifeCycleController(root).startFc();
        Component[] subComponents = GCM.getContentController(root).getFcSubComponents();
        for (Component component : subComponents) {
            if ("parallel".equals(GCM.getNameController(component).getFcName())) {
                // invoke method on composite
                I1Multicast i1Multicast = (I1Multicast) component.getFcInterface("i1");
                //I1 i1= (I1)p1.getFcInterface("i1");
                messages = i1Multicast.processInputMessage(new Message(MESSAGE));

                for (Message msg : messages) {
                    msg.append(MESSAGE);
                }
                break;
            }
        }
        StringBuffer resulting_msg = new StringBuffer();
        int nb_messages = append(resulting_msg, messages);

        //        System.out.println("*** received " + nb_messages + "  : " +
        //            resulting_msg.toString());
        //        System.out.println("***" + resulting_msg.toString());
        // this --> primitiveC --> primitiveA --> primitiveB--> primitiveA --> primitiveC --> this  (message goes through parallel and composite components)
        String single_message = Test.MESSAGE + PrimitiveComponentA.MESSAGE + PrimitiveComponentB.MESSAGE +
            PrimitiveComponentA.MESSAGE + Test.MESSAGE;

        // there should be 4 messages with the current configuration
        Assert.assertEquals(2, nb_messages);

        Assert.assertEquals(single_message + single_message, resulting_msg.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see testsuite.test.AbstractTest#endTest()
     */
    @After
    public void endTest() throws Exception {
        //        Launcher.killNodes(false);
        Registry.instance().clear();
        if (newDeploymentDescriptor != null)
            newDeploymentDescriptor.kill();

        if (oldDeploymentDescriptor != null)
            oldDeploymentDescriptor.killall(false);
    }

    private int append(StringBuffer buffer, List<Message> messages) {
        int nb_messages = 0;
        for (Message message : messages) {
            nb_messages++;
            buffer.append(message);
        }
        return nb_messages;
    }
}
