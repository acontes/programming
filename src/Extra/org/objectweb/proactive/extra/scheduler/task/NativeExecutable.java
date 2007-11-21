/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;

import org.objectweb.proactive.extra.scheduler.common.scripting.GenerationScript;
import org.objectweb.proactive.extra.scheduler.common.task.Executable;
import org.objectweb.proactive.extra.scheduler.common.task.TaskResult;


/**
 * This is the execution entry point for the native task.
 * The execute(TaskResult...) method will be override by the scheduler to launch the native process.
 * This class provide a getProcess method that will return the current running native process.
 *
 * @author jlscheef - ProActiveTeam
 * @version 1.0, Aug 21, 2007
 * @since ProActive 3.2
 */
public class NativeExecutable extends Executable {

    /** Serial version UID */
    private static final long serialVersionUID = -8244644159419804669L;

    /** Process that start the native task */
    private Process process;

    /** Command that should be executed */
    private String command;

    /** Command generated by a script */
    private GenerationScript generated = null;

    /**
     * Create a new native task that execute command.
     * @param command the command to be executed.
     * @param generated generation script if the command is generated by a script
     */
    public NativeExecutable(String command, GenerationScript generated) {
        this.command = command;
        this.generated = generated;
    }

    /**
     * Create a new native task that execute command.
     * @param command the command to be executed.
     */
    public NativeExecutable(String command) {
        this.command = command;
    }

    /**
     * Return the current native running process.
     * It is used by the scheduler to allow it to kill the process.
     *
     * @return the current native running process.
     */
    public Process getProcess() {
        return this.process;
    }

    /**
     * Return the generation script if any, null otherwise.
     * It is used by the task launcher to generate the command.
     *
     * @return the generation script.
     */
    public GenerationScript getGenerationScript() {
        return generated;
    }

    /**
     * Sets the command to the given command value.
     *
     * @param command the command to set.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @see org.objectweb.proactive.extra.scheduler.common.task.Executable#execute(org.objectweb.proactive.extra.scheduler.task.TaskResult[])
     */
    public Object execute(TaskResult... results) {
        try {
            process = Runtime.getRuntime().exec(this.command);

            // redirect streams
            BufferedReader sout = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
            BufferedReader serr = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));
            Thread tsout = new Thread(new ThreadReader(sout, System.out));
            Thread tserr = new Thread(new ThreadReader(serr, System.err));
            tsout.start();
            tserr.start();
            // wait for process completion
            process.waitFor();
            // wait for log flush
            tsout.join();
            tserr.join();

            return process.exitValue();
        } catch (Exception e) {
            //TODO send the exception or error to the user ?
            e.printStackTrace();

            return 255;
        }
    }

    /**
     * @see org.objectweb.proactive.extra.scheduler.common.task.Executable#init(java.util.Map)
     */
    @Override
    public final void init(Map<String, Object> args) throws Exception {
        throw new RuntimeException(
            "This method should have NEVER been called in this context !!");
    }

    /** Pipe between two streams */
    protected class ThreadReader implements Runnable {
        private BufferedReader in;
        private PrintStream out;

        public ThreadReader(BufferedReader in, PrintStream out) {
            this.in = in;
            this.out = out;
        }

        public void run() {
            String str = null;

            try {
                while ((str = in.readLine()) != null) {
                    out.println(str);
                }
            } catch (IOException e) {
                //FIXME cdelbe gros vilain tu dois throw exception
                e.printStackTrace();
            }
        }
    }
}
