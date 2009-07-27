package org.objectweb.proactive.examples.structuredp2p.launchers.actions;

import java.util.Random;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.LexicographicCoordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.QueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can.RDFQuery;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class SearchAction extends Action {

    public SearchAction(Manager manager) {
        super(manager, "Search", "Send a lookup message to a random peer", "search", "s");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Object... args) {
        Random rand = new Random();
        Coordinate[] coordinatesToFind = new Coordinate[CANOverlay.NB_DIMENSIONS];

        String buf = "(";
        for (int i = 0; i < coordinatesToFind.length; i++) {
            coordinatesToFind[i] = LexicographicCoordinate.random(1 + rand.nextInt(1));
            // coordinatesToFind[i] = BigDecimalCoordinate.random();
            if (i != 0) {
                buf += ",";
            }
            buf += coordinatesToFind[i];
        }
        buf += ")";

        Peer sender = super.getManager().getPeersLauncher().getAvailablePeers().get(
                rand.nextInt(super.getManager().getPeersLauncher().getAvailablePeers().size()));

        QueryResponse response = sender.search(new RDFQuery(sender, coordinatesToFind));

        super.printInformation("Lookup for peer managing " + buf + ".\n    Lookup start from peer managing " +
            ((CANOverlay) sender.getStructuredOverlay()).getZone() + ".\n    Peer found in " +
            response.getLatency() + "ms with " + response.getNbSteps() + " steps.");
    }

}
