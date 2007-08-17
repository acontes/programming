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
package org.objectweb.proactive.extra.masterslave.core;

import java.io.Serializable;

import org.objectweb.proactive.extra.masterslave.interfaces.SlaveMemory;
import org.objectweb.proactive.extra.masterslave.interfaces.Task;
import org.objectweb.proactive.extra.masterslave.interfaces.internal.Identifiable;
import org.objectweb.proactive.extra.masterslave.interfaces.internal.TaskIntern;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * The internal version of a task, contains an internal ID and the task itself
 * @author fviale
 *
 */
public class TaskWrapperImpl implements TaskIntern {

    /**
     * The id of the task
     */
    protected long id = NULL_TASK_ID;

    /**
     * The actual task object
     */
    protected Task realTask = null;

    /**
     *
     */
    public TaskWrapperImpl() { // null task
    }

    /**
     * Creates a wrapper with the given task and id
     * @param id id of the task
     * @param realTask the user task
     */
    public TaskWrapperImpl(final long id, final Task realTask) {
        this.id = id;
        this.realTask = realTask;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof Identifiable) {
            return id == ((Identifiable) obj).getId();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Task getTask() {
        return realTask;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (int) id;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNull() {
        return realTask == null;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable run(final SlaveMemory memory) throws Exception {
        return this.realTask.run(memory);
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        } else if (o instanceof Identifiable) {
            return (int) (id - ((Identifiable) o).getId());
        } else {
            throw new IllegalArgumentException("" + o);
        }
    }
}
