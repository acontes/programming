package org.objectweb.proactive.examples.structuredp2p.launchers.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.Action;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.JoinAction;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.LeaveAction;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.SearchAction;


public class StressTestManager extends Manager {
    private List<String> operationsAllowed = new ArrayList<String>();

    public StressTestManager(PeerLauncher peerLauncher, boolean performJoin, boolean performLeave,
            boolean performSearch) {
        super(peerLauncher);

        if (performJoin) {
            this.operationsAllowed.add(new JoinAction(this).getName());
        }

        if (performLeave) {
            this.operationsAllowed.add(new LeaveAction(this).getName());
        }

        if (performSearch) {
            this.operationsAllowed.add(new SearchAction(this).getName());
        }
    }

    public void run() {
        new Thread(new KeyListenerThread(this)).start();

        while (super.isRunning()) {
            this.performARandomBasicOperation();
        }
    }

    public void performARandomBasicOperation() {
        Random rand = new Random();

        Action[] basicActions = super.getActions().values().toArray(new Action[] { null });
        Action selectedAction = basicActions[rand.nextInt(3)];

        if (this.operationsAllowed.contains(selectedAction.getName())) {
            selectedAction.execute();
        } else {
            this.performARandomBasicOperation();
        }
    }

    public class KeyListenerThread implements Runnable {

        private boolean running = true;
        private StressTestManager stressTestThread;

        public KeyListenerThread(StressTestManager stressTestThread) {
            this.stressTestThread = stressTestThread;
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (this.running) {
                if (scanner.next().equals("q")) {
                    scanner.close();
                    this.stressTestThread.stopExecution();
                    this.running = false;
                }
            }
        }
    }
}
