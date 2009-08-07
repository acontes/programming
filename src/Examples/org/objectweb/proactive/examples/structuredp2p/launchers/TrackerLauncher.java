package org.objectweb.proactive.examples.structuredp2p.launchers;

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


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class TrackerLauncher {
    private Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    private int nbTrackersToCreate = 1;
    public List<Tracker> trackers = new ArrayList<Tracker>();

    private GCMVirtualNode virtualNodeForTracker;

    public TrackerLauncher(String pathToGCMAFile) {
        try {
            Deployment.deploy(pathToGCMAFile);
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
            this.trackers.add(Tracker
                    .newActiveTracker(OverlayType.CAN, this.virtualNodeForTracker.getANode()));
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    public List<Tracker> getAvailableTrackers() {
        return this.trackers;
    }
}
