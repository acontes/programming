package org.objectweb.proactive.examples.descriptor;

import org.apache.log4j.Logger;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

import java.io.IOException;


/**
 *
 *
 * @author Jerome+Sylvain
 */
public class MiniDescrClient {
    static Logger logger = Logger.getLogger(MiniDescrClient.class);
    MiniDescrActive minidesca = null;

    public MiniDescrClient(String location) {
        VirtualNode virtualnode = null;

        ProActiveDescriptor pad = null;
        logger.info("-+-+-+-+-+-+-+- MiniDescrClient launched -+-+-+-+-+-+-+-");

        try {
            pad = ProActive.getProactiveDescriptor(location);
            virtualnode = pad.getVirtualNode("MiniVNServer");
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        virtualnode.activate();

        Node[] nodes = null;

        try {
            nodes = virtualnode.getNodes();

            for (int i = 0; i < nodes.length; i++) {
                for (int j = 0; j < 10; j++) {
                    new Thread(new Bomber(nodes[i])).start();
                }
            }
        } catch (NodeException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            new MiniDescrClient(MiniDescrClient.class.getResource(
                    "minidescriptor_client.xml").getPath());
        } else {
            new MiniDescrClient(args[0]);
        }
    }

    class Bomber implements Runnable {
        Node n;

        Bomber(Node n) {
            this.n = n;
        }

        public void run() {
            try {
                MiniDescrActive desc = (MiniDescrActive) ProActive.newActive(MiniDescrActive.class.getName(),
                        null, n);

                for (int k = 0; k < 10; k++) {
                    Message msg = desc.getComputerInfo();
                    logger.info("-+-+-+-+-+-+-+- " + msg + " -+-+-+-+-+-+-+-");
                }
            } catch (ActiveObjectCreationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
