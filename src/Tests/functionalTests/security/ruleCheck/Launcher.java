package functionalTests.security.ruleCheck;

import java.net.URL;
import java.security.Policy;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.security.exceptions.RuntimeSecurityException;

import sun.security.provider.PolicyFile;


public class Launcher {
    public static void main(String[] args) {
        Policy policy = null;
        URL policyFile = Launcher.class.getResource("allPerm.policy");

        // sets the policy to be used by the new jvms
        System.setProperty("java.security.policy", policyFile.toString());

        // sets the policy to be used for the current thread
        policy = new PolicyFile(policyFile);

        Policy.setPolicy(policy);

        // enables security for the current thread
        System.setSecurityManager(new SecurityManager());

        try {
            ProActiveDescriptor descriptor1 = ProActive.getProactiveDescriptor(
                    "descriptors/security/simple1.xml");
            descriptor1.activateMappings();
            VirtualNode virtualNode1 = descriptor1.getVirtualNode("vn1");
            Node node1 = virtualNode1.getNodes()[0];
            SampleObject a = (SampleObject) ProActive.newActive(SampleObject.class.getName(),
                    new Object[] { "CN=Garden1" }, node1);

            ProActiveDescriptor descriptor2 = ProActive.getProactiveDescriptor(
                    "descriptors/security/simple2.xml");
            descriptor2.activateMappings();
            VirtualNode virtualNode2 = descriptor2.getVirtualNode("vn2");
            Node node2 = virtualNode2.getNodes()[0];
            SampleObject b = (SampleObject) ProActive.newActive(SampleObject.class.getName(),
                    new Object[] { "CN=Garden2" }, node2);

            ProActiveDescriptor descriptor3 = ProActive.getProactiveDescriptor(
                    "descriptors/security/simple3.xml");
            descriptor3.activateMappings();
            VirtualNode virtualNode3 = descriptor3.getVirtualNode("vn3");
            Node node3 = virtualNode3.getNodes()[0];
            SampleObject c = (SampleObject) ProActive.newActive(SampleObject.class.getName(),
                    new Object[] { "CN=Garden3" }, node3);

            //			a.makeTargetDoSomething(a);
            //            System.out.println("==");
            //a.makeTargetDoSomething(b);
            //            System.out.println("==");
            //a.makeTargetDoSomething(c);

            //            System.out.println("//////////////////");

            //            b.makeTargetDoSomething(a);
            //            System.out.println("==");
            //            b.makeTargetDoSomething(b);
            //            System.out.println("==");
            b.makeTargetDoSomething(c);

            //            System.out.println("//////////////////");
            //a.makeTargetDoSomething(b);
            try {
                String s = b.sayhello(c).get();
                System.out.println("s : " + s);
            } catch (RuntimeSecurityException ex) {
                System.out.println("wwwwwwwaaaouuuuu");
            }
            try {
                b.makeTargetDoSomething(c);
            } catch (RuntimeSecurityException ex) {
                System.out.println("void ");
            }

            //            c.makeTargetDoSomething(a);
            //            System.out.println("==");
            //            c.makeTargetDoSomething(b);
            //            System.out.println("==");
            //            c.makeTargetDoSomething(c);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }
}
