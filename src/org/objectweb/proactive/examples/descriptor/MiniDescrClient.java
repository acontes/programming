package org.objectweb.proactive.examples.descriptor;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

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

        try {
            Node[] nodes = virtualnode.getNodes();
            Object[] param = null;

            for (int i = 0; i < nodes.length; i++) {
            	for (int j = 0; j < 100; j++) {
            		MiniDescrActive desc = (MiniDescrActive)ProActive.newActive(MiniDescrActive.class.getName(), param, nodes[i]);
            		for (int k = 0; k < 10; k++) {
            			Message msg = desc.getComputerInfo();
            			logger.info("-+-+-+-+-+-+-+- " + msg + " -+-+-+-+-+-+-+-");
            		}
            	}
            }
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
    	if (args.length < 1)
    		new MiniDescrClient(MiniDescrClient.class.getResource("minidescriptor_client.xml").getPath());
    	else
    		new MiniDescrClient(args[0]);
    	System.exit(0);
    }
}
