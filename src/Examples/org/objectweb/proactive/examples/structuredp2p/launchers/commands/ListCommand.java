package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;


/**
 * Performs a list command. This kind of command will list all the peers that are on the network by
 * indicating the Zone they are managing.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class ListCommand extends Command {

    /**
     * Constructor.
     * 
     * @param manager
     *            the manager that will execute this command.
     */
    public ListCommand(Manager manager) {
        super(manager, "List", "List the current peers available on the network", "list", "ll");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String... args) {
        if (super.getManager().getPeersLauncher().getAvailablePeers().size() == 0) {
            super.printInfo("There is no peer on the network.");
        } else {
            StringBuffer buf = new StringBuffer();
            int i = 1;
            for (Peer peer : super.getManager().getPeersLauncher().getAvailablePeers()) {
                buf.append("    " + i + ". ");
                buf.append(peer);
                buf.append("\n");
                for (Statement stmt : peer.query(new StatementImpl(null, null, null))) {
                    buf.append("         - <");
                    buf.append(stmt.getSubject());
                    buf.append(", ");
                    buf.append(stmt.getPredicate());
                    buf.append(", ");
                    buf.append(stmt.getObject());
                    buf.append(">\n");
                }
                buf.append("\n");
                i++;
            }

            super.printInfo("The following peer(s) are on the network :\n\n" + buf.toString());
        }
    }

}
