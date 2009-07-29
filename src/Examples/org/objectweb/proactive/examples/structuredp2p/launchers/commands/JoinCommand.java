package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import java.util.List;
import java.util.Random;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * Performs a join command.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class JoinCommand extends Command {

    private int nbJoinActionPerformed = 0;

    /**
     * Constructor.
     * 
     * @param manager
     *            the manager that will execute this command.
     */
    public JoinCommand(Manager manager) {
        super(manager, "Join", "Add a new peer at a random position", "join", "j");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String... args) {
        Random rand = new Random();
        Peer peer = null;

        PeerLauncher peersLauncher = super.getManager().getPeersLauncher();

        List<Node> nodes = null;
        try {
            nodes = peersLauncher.getAvailableNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            peer = Peer.newActivePeer(OverlayType.CAN, nodes.get(rand.nextInt(nodes.size())));

            super.getManager().getPeersLauncher().getAvailablePeers().add(peer);

            if (peersLauncher.getAvailableNodes().size() == 1) {
                for (int i = 0; i < 100; i++) {
                    peer.addData();
                }
            }

            peersLauncher.getTrackersLauncher().getAvailableTrackers().get(
                    this.nbJoinActionPerformed %
                        peersLauncher.getTrackersLauncher().getAvailableTrackers().size()).addOnNetwork(peer);
            this.nbJoinActionPerformed++;
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }

        super.printInfo("Add peer managing " + peer.getStructuredOverlay());
    }

}
