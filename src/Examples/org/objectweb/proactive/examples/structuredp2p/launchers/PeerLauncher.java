package org.objectweb.proactive.examples.structuredp2p.launchers;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.structuredp2p.launchers.managers.InteractiveManager;
import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.examples.structuredp2p.launchers.managers.StressTestManager;
import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class PeerLauncher extends Launcher {

    private List<Node> availableNodes;
    private List<Peer> availablePeers = new ArrayList<Peer>();

    private Manager manager;
    private TrackerLauncher trackerLauncher;

    public PeerLauncher(TrackerLauncher trackerLauncher, String pathToGCMAFile, String managerType,
            String managerTypeArgs, int nbPeersToInitialize) {

        this.trackerLauncher = trackerLauncher;

        try {
            Deployment.deploy(pathToGCMAFile);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        if (managerType.equals("i")) {
            this.manager = new InteractiveManager(this);
        } else if (managerType.equals("st")) {
            this.manager = new StressTestManager(this, managerTypeArgs.contains("j"), managerTypeArgs
                    .contains("l"), managerTypeArgs.contains("s"));
        }

        this.availableNodes = Deployment.getVirtualNode("Peer").getCurrentNodes();

        for (int index = 0; index < nbPeersToInitialize; index++) {
            this.manager.getActions().get("Join").execute();
        }

        this.manager.startExecution();
    }

    public List<Node> getAvailableNodes() {
        return this.availableNodes;
    }

    public List<Peer> getAvailablePeers() {
        return this.availablePeers;
    }

    public TrackerLauncher getTrackersLauncher() {
        return this.trackerLauncher;
    }
}
