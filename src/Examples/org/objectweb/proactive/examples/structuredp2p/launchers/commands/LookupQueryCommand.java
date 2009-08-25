package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import java.util.Random;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.LexicographicCoordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQuery;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQueryResponse;


/**
 * Performs a lookup query command : a lookup query command performs a {@link LookupQuery} which
 * consists in searching a peer on the network and to return the reference of the peer found to the
 * sender of the request in order to print some information.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class LookupQueryCommand extends Command {

    /**
     * Constructor.
     * 
     * @param manager
     *            the manager that will execute this command.
     */
    public LookupQueryCommand(Manager manager) {
        super(manager, "Search", "Send a lookup message to a random peer", "search", "s");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String... args) {
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

        LookupQueryResponse response = (LookupQueryResponse) PAFuture.getFutureValue(sender
                .search(new LookupQuery(sender, coordinatesToFind)));

        super
                .printInfo("Lookup for peer managing " + buf + ".\n    Lookup start from peer managing " +
                    ((CANOverlay) sender.getStructuredOverlay()).getZone() + ".\n    Peer found in " +
                    response.getLatency() + "ms with " + response.getTotalNumberOfSteps() + " steps (" +
                    response.getNbStepsForSend() + " for send, " + response.getNbStepsForReceipt() +
                    " for receipt).");
    }

}
