package functionalTests.security.ruleCheck;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Policy;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

import sun.security.provider.PolicyFile;


public class Main2 {
    public static void main(String[] args) {
        Policy policy = null;
        String path = System.getProperty("user.dir") + "/jaas-cfg/";

        // sets the policy to be used by the new jvms
        System.setProperty("java.security.policy", path + "allPerm.policy");

        // sets the policy to be used for the current thread
        try {
            policy = new PolicyFile(new URL("file:" + path + "allPerm.policy"));
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            System.exit(-1);
        }
        Policy.setPolicy(policy);

        // enables security for the current thread
        System.setSecurityManager(new SecurityManager());

        try {
            ProActiveDescriptor descriptor = ProActive.getProactiveDescriptor(
                    "descriptors/security/simple2.xml");
            descriptor.activateMappings(); // Acquire the resources
            VirtualNode virtualNode = descriptor.getVirtualNode("rvn2");
            Node node2 = virtualNode.getNodes()[0];
            B b = (B) ProActive.newActive(B.class.getName(), new Object[] {  });

            A a = (A) ProActive.lookupActive(A.class.getName(),
                    "//localhost/objectA");

            System.out.println("\n" + b.makeADoStuff(a).get());
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n=======oki2=======");
    }
}
