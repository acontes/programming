package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * Performs a quit command.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class QuitCommand extends Command {

    /**
     * Constructor.
     * 
     * @param manager
     *            the manager that will execute this command.
     */
    public QuitCommand(Manager manager) {
        super(manager, "Quit", "Quit the application.", "quit", "q");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String... args) {
        try {
            for (Peer p : super.getManager().getPeersLauncher().getAvailablePeers()) {
                p.leave();
            }

            Deployment.kill();
            super.getManager().stopExecution();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
