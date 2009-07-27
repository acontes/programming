package org.objectweb.proactive.examples.structuredp2p.launchers.actions;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;
import org.objectweb.proactive.examples.structuredp2p.util.Deployment;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


public class QuitAction extends Action {

    public QuitAction(Manager manager) {
        super(manager, "Quit", "Quit the application.", "quit", "q");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Object... args) {
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
