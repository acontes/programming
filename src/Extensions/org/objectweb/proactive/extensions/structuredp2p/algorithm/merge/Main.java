package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import javax.swing.SwingUtilities;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


public class Main {
    public static void main(String arv[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CANPeer peer = null;
                try {
                    peer = (CANPeer) PAActiveObject.newActive(CANPeer.class.getCanonicalName(),
                            new Object[] {});
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
