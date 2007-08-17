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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.examples.masterslave;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectweb.proactive.extra.masterslave.ProActiveMaster;
import org.objectweb.proactive.extra.masterslave.TaskAlreadySubmittedException;
import org.objectweb.proactive.extra.masterslave.TaskException;
import org.objectweb.proactive.extra.masterslave.tasks.NativeTask;


/**
 * This simple test class is an example on how to launch Native commands using the Master/Slave API
 * The program launches the command "hostname" on a set of remote machines and display the results.
 * @author fviale
 *
 */
public class NativeExample extends AbstractExample {
    ProActiveMaster<SimpleNativeTask, String[]> master;

    /**
     * @param args
     * @throws TaskAlreadySubmittedException
     */
    public static void main(String[] args)
        throws MalformedURLException, TaskAlreadySubmittedException {
        NativeExample instance = new NativeExample();

        // Getting command line parameters
        instance.init(args);

        // Creating the Master
        instance.master = new ProActiveMaster<SimpleNativeTask, String[]>();
        instance.registerHook();
        instance.master.addResources(instance.descriptor_url, instance.vn_name);

        // Creating the tasks to be solved
        List<SimpleNativeTask> tasks = new ArrayList<SimpleNativeTask>();
        for (int i = 0; i < 20; i++) {
            tasks.add(new SimpleNativeTask("hostname"));
        }

        // Submitting the tasks
        instance.master.solve(tasks);
        Collection<String[]> results = null;

        // Collecting the results
        try {
            results = instance.master.waitAllResults();
        } catch (TaskException e) {
            // We catch user exceptions
            e.printStackTrace();
        }
        for (String[] result : results) {
            for (String line : result) {
                System.out.println(line);
            }
        }

        System.exit(0);
    }

    /**
     * A task executing a native command
     * @author fviale
     *
     */
    public static class SimpleNativeTask extends NativeTask {
        public SimpleNativeTask(String command) {
            super(command);
        }
    }

    @Override
    protected void init_specialized(String[] args) {
        // nothing to do
    }

    @Override
    protected ProActiveMaster getMaster() {
        return master;
    }
}
