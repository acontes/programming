package org.objectweb.proactive.examples.structuredp2p.launchers.actions;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class ListAction extends Action {

    public ListAction(Manager manager) {
        super(manager, "List", "List the current peers available on the network", "list", "ll");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Object... args) {
        if (super.getManager().getPeersLauncher().getAvailablePeers().size() == 0) {
            super.printInformation("There is no peer on the network.");
        } else {
            StringBuffer buf = new StringBuffer();
            int i = 1;
            for (Peer peer : super.getManager().getPeersLauncher().getAvailablePeers()) {
                buf.append("    " + i + ". ");
                buf.append(peer);
                buf.append("\n");
                i++;
            }

            super.printInformation("The following peer(s) are on the network :\n\n" + buf.toString());
        }
    }

}
