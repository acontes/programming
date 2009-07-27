package org.objectweb.proactive.examples.structuredp2p.launchers.actions;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;


/**
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public abstract class Action {
    private Manager manager;

    private String name;
    private String description;
    private List<String> commandShortcuts;

    public Action(Manager manager, String name, String description, String... commandShortcuts) {
        this.manager = manager;
        this.name = name;
        this.description = description;
        this.commandShortcuts = new ArrayList<String>();

        for (String shortcut : commandShortcuts) {
            this.commandShortcuts.add(shortcut);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getCommandShortcuts() {
        return this.commandShortcuts;
    }

    public abstract void execute(Object... args);

    public Manager getManager() {
        return this.manager;
    }

    public void printInformation(String info) {
        System.out.println("*** " + info);
    }
}
