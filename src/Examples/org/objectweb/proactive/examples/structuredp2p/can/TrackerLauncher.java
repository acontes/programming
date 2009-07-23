package org.objectweb.proactive.examples.structuredp2p.can;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Tracker;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class TrackerLauncher {
    private Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    private GCMVirtualNode virtualNodeForTracker;
    private int nbTrackersToCreate = 1;

    public static List<Tracker> trackers = new ArrayList<Tracker>();

    public TrackerLauncher(String[] args) {
        try {
            Deployment.deploy(args[0]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        this.virtualNodeForTracker = Deployment.getVirtualNode("Tracker");
        this.nbTrackersToCreate = this.virtualNodeForTracker.getCurrentNodes().size();

        for (int i = 0; i < this.nbTrackersToCreate; i++) {
            this.createNewTracker();
        }

        this.logger.info("[STRUCTURED P2P] " + this.nbTrackersToCreate + " tracker(s) created");
    }

    private void createNewTracker() {
        try {
            TrackerLauncher.trackers.add(Tracker.newActiveTracker(OverlayType.CAN, this.virtualNodeForTracker
                    .getANode()));
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }
}
