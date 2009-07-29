package org.objectweb.proactive.examples.structuredp2p.launchers.managers;

import java.util.List;
import java.util.Scanner;

import org.objectweb.proactive.examples.structuredp2p.launchers.PeerLauncher;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.Command;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.ListCommand;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.QuitCommand;
import org.objectweb.proactive.examples.structuredp2p.launchers.commands.RandomCommand;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class InteractiveManager extends Manager {

    public InteractiveManager(PeerLauncher peerLauncher) {
        super(peerLauncher);

        super.addAction(new ListCommand(this));
        super.addAction(new QuitCommand(this));
        super.addAction(new RandomCommand(this));
    }

    public InteractiveManager(PeerLauncher peerLauncher, List<Command> actionsToAdd) {
        this(peerLauncher);

        for (Command action : actionsToAdd) {
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

            String[] args = new String[splittedInputLine.length - 1];
            for (int i = 1; i < splittedInputLine.length; i++) {
                args[i - 1] = splittedInputLine[i];
            }

            for (Command action : super.getActions().values()) {
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

        for (Command action : super.getActions().values()) {
            buf.append(" > Type in '");
            buf.append(action.getCommandShortcuts().get(0));

            for (String arg : action.getArguments()) {
                buf.append(" ");
                buf.append(arg);
            }

            buf.append("' : ");
            buf.append(action.getDescription());
            buf.append("\n");
        }

        System.out.println(buf.toString());
    }
}
