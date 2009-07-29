package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import java.util.Random;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;


/**
 * Performs a random command. Each random command can be one of the three basic commands : join,
 * leave, search.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public class RandomCommand extends Command {
    private String[] availableActions = { "Join", "Leave", "Search" };

    /**
     * Constructor
     * 
     * @param manager
     *            the manager that will execute that command.
     */
    public RandomCommand(Manager manager) {
        super(manager, "Random", new String[] { "nbOperationsToPerform" },
                "Performs a random action among the three basics operation : join, leave, search.", "random",
                "rand", "r");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String... args) {
        int nbActionsToPerform = 0;
        Random rand = new Random();

        if (args.length == 0) {
            super.printInfo("You must specify the number of random actions to perform.");
        } else {
            try {
                nbActionsToPerform = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < nbActionsToPerform; i++) {
                super.getManager().getActions().get(
                        this.availableActions[rand.nextInt(this.availableActions.length)]).execute();
            }
        }
    }
}
