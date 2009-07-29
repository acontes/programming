package org.objectweb.proactive.examples.structuredp2p.launchers.commands;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.examples.structuredp2p.launchers.managers.Manager;


/**
 * A Command is a set of operations that a {@link Manager} can execute.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/27/2009
 */
public abstract class Command {
    private Manager manager;

    private String name;
    private String[] args = new String[] {};
    private String description;
    private List<String> commandShortcuts;

    public Command(Manager manager, String name, String description, String... commandShortcuts) {
        this.manager = manager;
        this.name = name;
        this.description = description;
        this.commandShortcuts = new ArrayList<String>();

        for (String shortcut : commandShortcuts) {
            this.commandShortcuts.add(shortcut);
        }
    }

    public Command(Manager manager, String name, String[] args, String description, String... commandShortcut) {
        this(manager, name, description, commandShortcut);
        this.args = args;
    }

    public String[] getArguments() {
        return this.args;
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

    public abstract void execute(String... args);

    public Manager getManager() {
        return this.manager;
    }

    public void printInfo(String info) {
        System.out.print("*** ");
        System.out.println(info);
        System.out.println();
    }
}
