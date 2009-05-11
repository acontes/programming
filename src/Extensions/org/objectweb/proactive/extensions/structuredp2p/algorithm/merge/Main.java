package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import javax.swing.SwingUtilities;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


public class Main {
    public static void main(String arv[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Peer peer = null;
                try {
                    peer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                            new Object[] { OverlayType.CAN });
                } catch (ActiveObjectCreationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                GraphicalUserInterface g = new GraphicalUserInterface(peer);
                g.setVisible(true);
            }
        });

    }
}
