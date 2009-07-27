package org.objectweb.proactive.examples.structuredp2p.launchers.managers;

import java.util.List;
import java.util.Scanner;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.Action;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.ListAction;
import org.objectweb.proactive.examples.structuredp2p.launchers.actions.QuitAction;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class InteractiveManager extends Manager {

    public InteractiveManager(PeerLauncher peerLauncher) {
        super(peerLauncher);

        super.addAction(new ListAction(this));
        super.addAction(new QuitAction(this));
    }

    public InteractiveManager(PeerLauncher peerLauncher, List<Action> actionsToAdd) {
        this(peerLauncher);

        for (Action action : actionsToAdd) {
            super.getActions().put(action.getName(), action);
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String inputLine;

        this.printActionsMenu();
        while (super.isRunning()) {
            inputLine = scanner.nextLine();

            String[] splittedInputLine = inputLine.split(" ");
            String command = splittedInputLine[0];

            Object[] args = new String[splittedInputLine.length - 1];
            for (int i = 1; i < splittedInputLine.length; i++) {
                args[i - 1] = splittedInputLine[i];
            }

            for (Action action : super.getActions().values()) {
                if (action.getCommandShortcuts().contains(command)) {
                    action.execute(args);
                    break;
                }
            }

            this.printActionsMenu();
        }
    }

    /**
     * Print app menu option on the standard output.
     */
    public void printActionsMenu() {
        StringBuffer buf = new StringBuffer();
        buf.append("[ Select an action to perform ]\n");

        for (Action action : super.getActions().values()) {
            buf.append(" > Type in '");
            buf.append(action.getCommandShortcuts().get(0));
            buf.append("' : ");
            buf.append(action.getDescription());
            buf.append("\n");
        }

        System.out.println(buf.toString());
    }
}
