package org.objectweb.proactive.examples.structuredp2p.can;

import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Tracker;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


public class TrackerLauncher {

    public static void main(String[] args) {
        try {
            Tracker tracker = (Tracker) PAActiveObject.newActive(Tracker.class.getName(),
                    new Object[] { OverlayType.CAN });

            // Binds the entry point to a specific URL on the RMI registry
            PAActiveObject.registerByName(tracker, "CANTracker");
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
