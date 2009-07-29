package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import java.util.Random;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;


/**
 * Performs a leave command.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class LeaveCommand extends Command {

    /**
     * Constructor.
     * 
     * @param manager
     *            the manager that will execute this command.
     */
    public LeaveCommand(Manager manager) {
        super(manager, "Leave", "Force a random peer to quit the network", "leave", "l");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String... args) {
        int nbPeers = super.getManager().getPeersLauncher().getAvailablePeers().size();

        if (nbPeers == 0) {
            super.printInfo("Impossible to perform leave action, the network has no peer.");
        } else {
            Random rand = new Random();
            Peer peer = super.getManager().getPeersLauncher().getAvailablePeers().remove(
                    rand.nextInt(nbPeers));
            super.printInfo("Remove peer managing " + ((CANOverlay) peer.getStructuredOverlay()).getZone());
            peer.leave();
        }
    }
}
