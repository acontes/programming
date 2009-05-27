package org.objectweb.proactive.examples.structuredp2p.can;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.Tracker;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;


public class PeerLauncher {

    public static ArrayList<Peer> remotePeers = new ArrayList<Peer>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        int nbPeers = 1;
        String uri = "localhost";

        System.out.println(args);
        if (args.length == 0) {
            System.err.println("Usage : java " + PeerLauncher.class.getCanonicalName() + " " +
                "descriptor [nbPeers] [trackerURI] ");
            System.exit(1);
        }
        if (args.length > 1) {
            nbPeers = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            uri = args[2];
        }

        try {
            Deployment.deploy(args[0]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        try {
            Tracker tracker = (Tracker) PAActiveObject.lookupActive(Tracker.class.getName(), "//" + uri +
                "/CANTracker");

            List<Node> avaibleNodes = Deployment.getVirtualNode("CANOverlay").getNewNodes();

            int i;
            for (i = 0; i < nbPeers; i++) {
                Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                        new Object[] { OverlayType.CAN }, avaibleNodes.get(i % avaibleNodes.size()));

                remotePeers.add(peer);
                tracker.addOnNetwork(peer);
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
