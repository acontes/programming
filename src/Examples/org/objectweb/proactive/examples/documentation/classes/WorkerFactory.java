/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
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
 */
//@snippet-start class_WorkerFactory
package org.objectweb.proactive.examples.documentation.classes;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;


/**
 * @author ProActive Team
 *
 * Factory used to instantiate Worker active objects
 */
public class WorkerFactory extends Worker {

    /**
     * Create a new Worker active object
     *
     * @param age
     * @param name
     * @param node
     * @return the Worker active object
     */
    public static Worker createActiveWorker(int age, String name, Node node) {
        Object[] params = new Object[] { new IntWrapper(age), name };
        try {
            return (Worker) PAActiveObject.newActive(Worker.class.getName(), params, node);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
            return null;
        } catch (NodeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
//@snippet-end class_WorkerFactory