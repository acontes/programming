package org.objectweb.proactive.examples.structuredp2p.launchers.managers;

import java.util.Map;
import java.util.TreeMap;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.Command;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.JoinCommand;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.LeaveCommand;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.LookupQueryCommand;


/**
 * Manager is an abstract class that must be extends by all managers. A manager is used in order to
 * launch some various kind of tests on a structured peer-to-peer network.
 * 
 * @author Pellegrino Laurent
 * @version 0.1.1, 07/29/2009
 */
public abstract class Manager extends Thread {

    private Map<String, Command> actions = new TreeMap<String, Command>();

    private boolean isRunning = false;

    private PeerLauncher peersLauncher;

    public Manager(PeerLauncher peerLauncher) {
        this.peersLauncher = peerLauncher;

        this.addAction(new JoinCommand(this));
        this.addAction(new LeaveCommand(this));
        this.addAction(new LookupQueryCommand(this));
    }

    public void addAction(Command action) {
        this.actions.put(action.getName(), action);
    }

    public Map<String, Command> getActions() {
        return this.actions;
    }

    public PeerLauncher getPeersLauncher() {
        return this.peersLauncher;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void removeAction(Command action) {
        this.actions.remove(action.getName());
    }

    public abstract void run();

    public void startExecution() {
        this.isRunning = true;
        super.start();
    }

    public void stopExecution() {
        this.isRunning = false;
    }
}
