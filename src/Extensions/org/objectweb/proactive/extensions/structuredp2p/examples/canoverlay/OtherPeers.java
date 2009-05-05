package org.objectweb.proactive.extensions.structuredp2p.examples.canoverlay;

import java.io.IOException;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;


public class OtherPeers {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int nbPeers = 1;
        String uri = "localhost";

        if (args.length > 2)
            nbPeers = Integer.getInteger(args[0]);
        if (args.length > 1)
            uri = args[1];

        try {
            Deployment.deploy(args[2]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        try {
            Peer entryPoint = (Peer) PAActiveObject.lookupActive(Peer.class.getName(), "//" + uri +
                "/EntryPoint");

            List<Node> avaibleNodes = Deployment.getVirtualNode("Grid2D").getNewNodes();

            int i;
            for (i = 0; i < nbPeers; i++) {
                Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                        new Object[] { OverlayType.CAN }, avaibleNodes.get(i % avaibleNodes.size()));

                peer.join(entryPoint);
            }
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
