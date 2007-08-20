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
package org.objectweb.proactive.extensions.calcium;

import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.calcium.exceptions.PanicException;
import org.objectweb.proactive.extensions.calcium.futures.Future;
import org.objectweb.proactive.extensions.calcium.futures.FutureImpl;
import org.objectweb.proactive.extensions.calcium.skeletons.Instruction;
import org.objectweb.proactive.extensions.calcium.skeletons.Skeleton;


public class Stream<T, R> {
    static Logger logger = ProActiveLogger.getLogger(Loggers.SKELETONS_KERNEL);
    private int streamId;
    private Facade facade;
    private Skeleton<T, R> skeleton;
    private int streamPriority;

    protected Stream(Facade facade, Skeleton<T, R> skeleton) {
        this.streamId = (int) (Math.random() * Integer.MAX_VALUE);
        this.skeleton = skeleton;
        this.facade = facade;
        this.streamPriority = Task.DEFAULT_PRIORITY;
    }

    /**
     * Inputs a new T to be computed.
     * @param param The T to be computed.
     * @throws PanicException
     * @throws InterruptedException
     */
    public Future<R> input(T param) throws InterruptedException, PanicException {
        //Put the parameters in a Task container
        Task<T> task = new Task<T>(param);

        Stack<Instruction> instructionStack = skeleton.getInstructionStack();
        task.setStack(instructionStack);
        task.setPriority(streamPriority);

        FutureImpl<R> future = new FutureImpl<R>(task.getId());
        facade.putTask(task, future);

        return (Future<R>) future;
    }

    /**
     * Inputs a vector of T to be computed.
     * @param paramV A vector containing the T.
     * @throws PanicException
     * @throws InterruptedException
     */
    public Vector<Future<R>> input(Vector<T> paramV)
        throws InterruptedException, PanicException {
        Vector<Future<R>> vector = new Vector<Future<R>>(paramV.size());
        for (T param : paramV)
            vector.add(input(param));

        return vector;
    }
}
