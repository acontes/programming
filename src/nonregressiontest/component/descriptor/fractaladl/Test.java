/*
 * Created on Apr 22, 2004
 * author : Matthieu Morel
 */
package nonregressiontest.component.descriptor.fractaladl;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;

//import org.objectweb.fractal.adl.Registry;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.component.adl.Launcher;
import org.objectweb.proactive.core.component.adl.Registry;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.group.ProActiveGroup;

import nonregressiontest.component.ComponentTest;
import nonregressiontest.component.I1;
import nonregressiontest.component.Message;
import nonregressiontest.component.PrimitiveComponentA;
import nonregressiontest.component.PrimitiveComponentB;


/**
 * For a graphical representation, open the MessagePassingExample.fractal with the fractal gui
 *
 * This test verifies the parsing and building of a component system using a customized Fractal ADL,
 * and tests new features such as exportation of virtual nodes and cardinality of virtual nodes.
 * It mixes exported and non-exported nodes to make sure these work together.
 *
 * @author Matthieu Morel
 */
public class Test extends ComponentTest {
    public static String MESSAGE = "-->m";
    private Message message;

    //ComponentsCache componentsCache;
    ProActiveDescriptor deploymentDescriptor;

    public Test() {
        super("Virtual node exportation / composition in the Fractal ADL",
            "Virtual node exportation / composition in the Fractal ADL");
    }

    /* (non-Javadoc)
     * @see testsuite.test.FunctionalTest#action()
     */
    public void action() throws Exception {
//        if (!"enable".equals(System.getProperty("proactive.future.ac"))) {
//            throw new Exception("automatic continuations are not set");
//        }
//        org.objectweb.proactive.core.component.adl.Launcher.main(new String[] {
//                "-fractal",
//                "nonregressiontest.component.descriptor.fractaladl.MessagePassingExample",
//                "",
//                Test.class.getResource(
//                    "/nonregressiontest/component/descriptor/deploymentDescriptor.xml")
//                          .getPath()
//            });
//
//        Component c = Registry.instance().getComponent("parallel");
        
        Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map context = new HashMap();
        context.put("deployment-descriptor", ProActive.getProactiveDescriptor(Test.class.getResource(
        "/nonregressiontest/component/descriptor/deploymentDescriptor.xml")
        .getPath()));
        Component root = (Component) f.newComponent("nonregressiontest.component.descriptor.fractaladl.MessagePassingExample",context);
        Fractal.getLifeCycleController(root).startFc();
        Component[] subComponents = Fractal.getContentController(root).getFcSubComponents();
        for (Component component : subComponents) {
            if ("parallel".equals(Fractal.getNameController(component).getFcName())) {
                // invoke method on composite
                I1 i1 = (I1) component.getFcInterface("i1");
                //I1 i1= (I1)p1.getFcInterface("i1");
                message = i1.processInputMessage(new Message(MESSAGE)).append(MESSAGE);
                break;
            }
            
        }


    }

    /**
     * @see testsuite.test.AbstractTest#initTest()
     */
    public void initTest() throws Exception {
    }

    /* (non-Javadoc)
     * @see testsuite.test.AbstractTest#endTest()
     */
    public void endTest() throws Exception {
//        Launcher.killNodes(false);
        Registry.instance().clear();
    }

    public boolean postConditions() throws Exception {
        //        		System.out.println("\nMESSAGE IS : ");
        //        		System.out.println("-------------------------------------------------");
        //        		message.printToStream(System.out);
        //        		System.out.println("-------------------------------------------------");
        StringBuffer resulting_msg = new StringBuffer();
        int nb_messages = append(resulting_msg, message);

        //System.out.println("*** received " + nb_messages + "  : " +
        //    resulting_msg.toString());
        //System.out.println("***" + resulting_msg.toString());
        // this --> primitiveC --> primitiveA --> primitiveB--> primitiveA --> primitiveC --> this  (message goes through parallel and composite components)
        String single_message = Test.MESSAGE + PrimitiveComponentA.MESSAGE +
            PrimitiveComponentB.MESSAGE + PrimitiveComponentA.MESSAGE +
            Test.MESSAGE;

        // there should be 6 messages with the current configuration
        return resulting_msg.toString().equals(single_message + single_message +
            single_message + single_message + single_message + single_message);
    }

    private int append(StringBuffer buffer, Message message) {
        int nb_messages = 0;
        if (ProActiveGroup.isGroup(message)) {
            for (int i = 0; i < ProActiveGroup.size(message); i++) {
                nb_messages += append(buffer,
                    (Message) ProActiveGroup.get(message, i));
            }
        } else {
            buffer.append(message.getMessage());
            nb_messages++;
        }
        return nb_messages;
    }

    public static void main(String[] args) {
        
        System.setProperty("fractal.provider", "org.objectweb.proactive.core.component.Fractive");
        System.setProperty("java.security.policy", System.getProperty("user.dir")+"/proactive.java.policy");
        System.setProperty("log4j.configuration", System.getProperty("user.dir")+"/proactive-log4j");
        System.setProperty("log4j.configuration", "file:" + System.getProperty("user.dir")+"/proactive-log4j");
        System.setProperty("nonregressiontest.descriptor.defaultnodes.file", "/nonregressiontest/descriptor/defaultnodes/NodesLocal.xml");
        Test test = new Test();
        try {
            test.action();
            test.postConditions();
            System.out.println("SUCCESS!");
        } catch (Exception e) {
            System.out.println("FAILED!");
            e.printStackTrace();
        } finally {
            try {
                test.endTest();
                System.exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
