package org.objectweb.proactive.examples.structuredp2p.launchers.actions;

import java.util.Random;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;


public class LeaveAction extends Action {

    public LeaveAction(Manager manager) {
        super(manager, "Leave", "Force a random peer to quit the network", "leave", "l");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Object... args) {
        int nbPeers = super.getManager().getPeersLauncher().getAvailablePeers().size();

        if (nbPeers == 0) {
            super.printInformation("Impossible to perform leave action, the network has no peer.");
        } else {
            Random rand = new Random();
            Peer peer = super.getManager().getPeersLauncher().getAvailablePeers().remove(
                    rand.nextInt(nbPeers));
            super.printInformation("Remove peer managing " +
                ((CANOverlay) peer.getStructuredOverlay()).getZone());
            peer.leave();
        }
    }
}
