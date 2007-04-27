package org.objectweb.proactive.examples.basic;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;



public class GroovyTest {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java " + GroovyTest.class.getName() +
                " descriptor.groovy <virtualNode>");
            System.exit(0);
        }
        ProActiveConfiguration.load();

        ProActiveDescriptor pa;
        try {
            pa = ProActive.getProactiveDescriptor("file:" + args[0]);
            pa.activateMappings();
            //  VirtualNode vn = pa.getVirtualNode("testNode");
            VirtualNode vn = pa.getVirtualNode(args[1]);
            //  VirtualNode vn2 = pa.getVirtualNode("Nodes");
            System.out.println(" Test() node count  " + vn.getNumberOfCreatedNodesAfterDeployment());
            // System.out.println(" Test() node1  " + vn.getNode().getNodeInformation().getURL());
            Node[] nodes = vn.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                Node node = nodes[i];
                System.out.println(" Test() node " +
                    node.getNodeInformation().getURL());
            }

            Thread.sleep(4000);
            System.out.println("PAD killing descriptor");
            pa.killall(false);
            System.out.println("-------------- DONE ----------------");
            System.out.println("Test.main() calling exit");
            System.exit(0);
            System.out.println("Exit called");
        } catch (ProActiveException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
