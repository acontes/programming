/*
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */

/**
 *
 *
 * @author walzouab
 *
 */
package org.objectweb.proactive.examples.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.objectweb.proactive.extra.scheduler.core.AdminScheduler;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthenticationInterface;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerConnection;


public class AdminCommunicator {
    private static AdminScheduler scheduler;
    private static final String START_CMD = "start";
    private static final String STOP_CMD = "stop";
    private static final String PAUSE_CMD = "pause";
    private static final String RESUME_CMD = "resume";
    private static final String SHUTDOWN_SOFT_CMD = "shutdown";
    private static final String EXIT_CMD = "exit";
    private static boolean stopCommunicator;

    /**
     * @param args
     */
    private static void output(String message) {
        System.out.print(message);
    }

    private static void error(String message) {
        System.err.print(message);
    }

    public static void main(String[] args) {
        try {
        	SchedulerAuthenticationInterface auth;
        	if (args.length > 0)
        		auth = SchedulerConnection.join(args[0]);
        	else
        		auth = SchedulerConnection.join(null);
        	scheduler = auth.logAsAdmin("jl", "jl");
        	stopCommunicator = false;
            startCommandListener();
        } catch (Exception e) {
            error("A fatal error has occured : " + e.getMessage() +
                "\n Will shut down communicator.\n");
            System.exit(1);
        }

        // if execution reaches this point this means it must exit
        System.exit(0);
    }

    private static void handleCommand(String command) {
        if (command.equals("")) {
        } else if (command.equals(EXIT_CMD)) {
            output("Communicator will exit.\n");
            stopCommunicator = true;
        } else if (command.equals("?") || command.equals("help")) {
            helpScreen();
        } else if (command.equals(START_CMD)) {
            boolean success = scheduler.start();
            if (success) {
                output("Scheduler started.\n");
            } else {
                output("Scheduler already started!!\n");
            }
        } else if (command.equals(STOP_CMD)) {
            boolean success = scheduler.stop();
            if (success) {
                output("Scheduler stopped.\n");
            } else {
                output("Scheduler already stopped!!\n");
            }
        } else if (command.equals(SHUTDOWN_SOFT_CMD)) {
            if (scheduler.shutdown()){
            	output("Shutdown sequence initialized, it might take a while to finish all executions, communicator will exit.\n");
            	stopCommunicator = true;
            } else {
            	output("Scheduler is running, plz stop it before shutting it down.\n");
            }
        } else {
            output("UNKNOWN COMMAND!!... Please type '?' or 'help' to see the list of commands\n");
        }
    }

    private static void startCommandListener() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!stopCommunicator) {
            output(" > ");
            String line = reader.readLine();
            try {
            	handleCommand(line);
            } catch(NumberFormatException e){
            	error("Id error !!\n");
            }
        }
    }

    private static void helpScreen() {
        String out = "";
        out += "Communicator Commands are:\n\n";
        out += String.format(" %1$-18s\t Starts scheduler\n", START_CMD);
        out += String.format(" %1$-18s\t Stops scheduler\n", STOP_CMD);
        out += String.format(" %1$-18s\t pauses all running tasks\n", PAUSE_CMD);
        out += String.format(" %1$-18s\t resumes all queued tasks\n", RESUME_CMD);
        out += String.format(" %1$-18s\t Waits for running tasks to finish and shutsdown\n",SHUTDOWN_SOFT_CMD);
        out += String.format(" %1$-18s\t Exits Communicator\n", EXIT_CMD);
        output(out);
    }
}
