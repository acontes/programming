/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.masterworker.core;

import org.objectweb.proactive.extensions.masterworker.interfaces.WorkerMemory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.jini.space.JavaSpace;


/**
 * Implementation of the worker memory
 *
 * @author The ProActive Team
 */
public class WorkerMemoryImpl implements WorkerMemory {
    /**
    * The memory of the worker <br>
    * the worker can keep some data between different tasks executions <br>
    * e.g. connection to a database, file descriptor, etc ...
    */
    private Map<String, Object> memory;
    private JavaSpace space;

    public WorkerMemoryImpl(Map<String, Serializable> memory, JavaSpace space) {
        this.memory = new HashMap<String, Object>(memory);

        if (null != space)
            this.space = space;
        else
            this.space = null;
    }

    /**
     * {@inheritDoc}
     */
    public void save(final String dataName, final Object data) {

        if (null != space) {
            System.out.println("Writing an object to the space...");
            ShareMemoryEntry msg = new ShareMemoryEntry();
            msg.data = data;
            msg.dataName = dataName;
            try {
                space.write(msg, null, 60 * 60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            memory.put(dataName, data);
    }

    /**
         * {@inheritDoc}
         */
    public Object load(final String dataName) {
        if (null != space) {
            System.out.println("Reading an object from the space...");
            ShareMemoryEntry template = new ShareMemoryEntry();
            ShareMemoryEntry result = null;
            try {
                result = (ShareMemoryEntry) space.read(template, null, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.data;
        } else
            return memory.get(dataName);
    }

    /**
     * {@inheritDoc}
     */
    public void erase(final String dataName) {
        if (null != space) {
            System.out.println("Taking an object from the space...");
            ShareMemoryEntry template = new ShareMemoryEntry();
            try {
                space.take(template, null, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            memory.remove(dataName);
    }

    public void clear() {
        if (null != space)
            return;
        memory.clear();
    }

}
