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
package org.objectweb.proactive.extensions.calcium.skeletons;

import java.util.Stack;

import org.objectweb.proactive.extensions.calcium.Task;
import org.objectweb.proactive.extensions.calcium.exceptions.EnvironmentException;
import org.objectweb.proactive.extensions.calcium.muscle.Execute;
import org.objectweb.proactive.extensions.calcium.statistics.Timer;


/**
 * The Seq skeleton is a wrapper for the user inputed
 * sequential code. This class allows the code to be nested
 * inside other skeletons.
 *
 * @author The ProActive Team (mleyton)
 *
 * @param <P>
 */
public class Seq<P, R> implements Skeleton<P, R>, Instruction<P, R> {
    Execute<P, R> secCode;
    int muscleId;

    public Seq(Execute<P, R> secCode) {
        this.secCode = secCode;
        muscleId = 0;
    }

    public Stack<Instruction> getInstructionStack() {
        Stack<Instruction> v = new Stack<Instruction>();
        v.add(this);
        return v;
    }

    public Task<R> compute(Task<P> t)
        throws RuntimeException, EnvironmentException {
        Timer timer = new Timer();
        R resultObject = secCode.execute(t.getObject());
        timer.stop();

        Task<R> newtask = t.reBirth(resultObject);

        t.getStats().getWorkout().track(secCode, timer);
        return newtask;
    }

    @Override
    public String toString() {
        return "Seq(" + this.secCode.getClass() + ")";
    }
}
