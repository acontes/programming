package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Tracker;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class TrackerLauncher {

    private GCMVirtualNode trackerVirtualNode;
    private int nbTrackers = 0;

    public static List<Tracker> trackers = new ArrayList<Tracker>();

    public TrackerLauncher(String[] args) {
        try {
            Deployment.deploy(args[0]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        this.trackerVirtualNode = Deployment.getVirtualNode("Tracker");
        this.nbTrackers = this.trackerVirtualNode.getCurrentNodes().size();
        System.out.println();
        System.out.println(this.nbTrackers + "TRACKERS TO CREATE");
        System.out.println();
        for (int i = 0; i < this.nbTrackers; i++) {
            this.createNewTracker();
        }
    }

    private void createNewTracker() {
        try {
            TrackerLauncher.trackers.add((Tracker) PAActiveObject.newActive(Tracker.class.getCanonicalName(),
                    new Object[] { OverlayType.CAN }, this.trackerVirtualNode.getANode()));
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
}
