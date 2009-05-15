package org.objectweb.proactive.extensions.structuredp2p.examples.canoverlay;

import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


public class EntryPoint {

    /**
     * @param args
     *            [0 : GCMA file]
     */
    public static void main(String[] args) {
        try {
            Peer entryPoint = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });

            // Binds the entry point to a specific URL on the RMI registry
            PAActiveObject.registerByName(entryPoint, "EntryPoint");
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
