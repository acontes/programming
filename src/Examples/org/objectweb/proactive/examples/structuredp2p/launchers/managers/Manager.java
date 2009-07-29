package org.objectweb.proactive.examples.structuredp2p.launchers.managers;

import java.util.Map;
import java.util.TreeMap;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.Action;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.JoinAction;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.LeaveAction;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.SearchAction;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public abstract class Manager extends Thread {

    private Map<String, Action> actions = new TreeMap<String, Action>();

    private PeerLauncher peersLauncher;

    private boolean isRunning = false;

    public Manager(PeerLauncher peerLauncher) {
        this.peersLauncher = peerLauncher;

        this.addAction(new JoinAction(this));
        this.addAction(new LeaveAction(this));
        this.addAction(new SearchAction(this));
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public PeerLauncher getPeersLauncher() {
        return this.peersLauncher;
    }

    public void addAction(Action action) {
        this.actions.put(action.getName(), action);
    }

    public void removeAction(Action action) {
        this.actions.remove(action.getName());
    }

    public Map<String, Action> getActions() {
        return this.actions;
    }

    public abstract void run();

    public void stopExecution() {
        this.isRunning = false;
    }

    public void startExecution() {
        this.isRunning = true;
        super.start();
    }
}
