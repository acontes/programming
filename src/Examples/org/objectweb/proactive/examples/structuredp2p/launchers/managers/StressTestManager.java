package org.objectweb.proactive.examples.structuredp2p.launchers.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.Command;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.JoinCommand;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.LeaveCommand;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.SearchCommand;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class StressTestManager extends Manager {
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

    private List<String> operationsAllowed = new ArrayList<String>();

    public StressTestManager(PeerLauncher peerLauncher, boolean performJoin, boolean performLeave,
            boolean performSearch) {
        super(peerLauncher);

        if (performJoin) {
            this.operationsAllowed.add(new JoinCommand(this).getName());
        }

        if (performLeave) {
            this.operationsAllowed.add(new LeaveCommand(this).getName());
        }

        if (performSearch) {
            this.operationsAllowed.add(new SearchCommand(this).getName());
        }
    }

    public void performARandomBasicOperation() {
        Random rand = new Random();

        Command[] basicActions = super.getActions().values().toArray(new Command[] { null });
        Command selectedAction = basicActions[rand.nextInt(3)];

        if (this.operationsAllowed.contains(selectedAction.getName())) {
            selectedAction.execute();
        } else {
            // this.performARandomBasicOperation();
        }
    }

    public void run() {
        new Thread(new KeyListenerThread(this)).start();

        while (super.isRunning()) {
            this.performARandomBasicOperation();
        }
    }
}
