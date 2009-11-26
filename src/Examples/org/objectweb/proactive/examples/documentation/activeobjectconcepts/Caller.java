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
//@snippet-start Caller_1
package org.objectweb.proactive.examples.documentation.activeobjectconcepts;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.examples.documentation.classes.Value;
import org.objectweb.proactive.examples.documentation.classes.Worker;

//@snippet-break Caller_1
import org.objectweb.proactive.core.body.exceptions.FutureMonitoringPingFailureException;


//@snippet-resume Caller_1

public class Caller {

    //@snippet-break Caller_1
    //@snippet-start Caller_2
    public static void synchronousCall() {
        //@snippet-break Caller_2
        System.out.println("===> Synchronous Call");
        //@snippet-resume Caller_2
        Worker charlie = new Worker(new IntWrapper(26), "Charlie");
        //@snippet-break Caller_2
        charlie.display();
        //@snippet-resume Caller_2
        Value v = charlie.foo();
        //@snippet-break Caller_2
        v.display();
        //@snippet-resume Caller_2
        v.bar();
        //@snippet-break Caller_2
        v.display();
        //@snippet-resume Caller_2
    }

    //@snippet-end Caller_2
    //@snippet-resume Caller_1
    public static void asynchronousCall() {
        try {

            //@snippet-break Caller_1
            System.out.println("===> Asynchronous Call");
            //@snippet-resume Caller_1
            Object[] params = new Object[] { new IntWrapper(26), "Charlie" };
            Worker charlie = (Worker) PAActiveObject.newActive(Worker.class.getName(), params);
            //@snippet-break Caller_1
            charlie.display();
            //@snippet-resume Caller_1
            Value v = charlie.foo();
            //@snippet-break Caller_1
            v.display();
            //@snippet-resume Caller_1
            v.bar();
            //@snippet-break Caller_1
            v.display();
            //@snippet-resume Caller_1

        } catch (ActiveObjectCreationException aoExcep) {
            System.err.println(aoExcep.getMessage());
        } catch (NodeException nodeExcep) {
            System.err.println(nodeExcep.getMessage());
        }
    }

    //@snippet-break Caller_1

    public static void continuation() {
        try {
            //@snippet-start Continuation_1
            Value v = new Value();
            Worker worker = (Worker) PAActiveObject.newActive(Worker.class.getName(), null);
            Value v1 = worker.foo(); //v1 is a future
            Value v2 = v.bar(v1, 1); //v1 is passed as parameter
            //@snippet-end Continuation_1

        } catch (ActiveObjectCreationException aoExcep) {
            System.err.println(aoExcep.getMessage());
        } catch (NodeException nodeExcep) {
            System.err.println(nodeExcep.getMessage());
        }
    }

    public static void goodPractice() {
        try {
            //@snippet-start Good_Practice_2
            Worker charlie = (Worker) PAActiveObject.newActive(Worker.class.getName(), null);
            Worker worker = charlie.getWorker();
            if (worker == null) {
                System.out.println("worker is null");
                worker.display();
            } else {
                System.out.println("worker is not null");
                worker.display();
            }
            //@snippet-end Good_Practice_2

            //@snippet-start Good_Practice_3
            Worker ao = (Worker) PAActiveObject.newActive(Worker.class.getName(), null);
            IntWrapper future = ao.getAge();
            String str;
            try {
                str = future.toString();
            } catch (FutureMonitoringPingFailureException fmpfe) {
                System.out.println("The active object 'ao' had a failure");
                fmpfe.printStackTrace();
            }
            //@snippet-end Good_Practice_3
        } catch (ActiveObjectCreationException aoExcep) {
            System.err.println(aoExcep.getMessage());
        } catch (NodeException nodeExcep) {
            System.err.println(nodeExcep.getMessage());
        }
    }

    //@snippet-resume Caller_1
    public static void main(String[] args) {
        //@snippet-break Caller_1
        synchronousCall();
        goodPractice();
        //@snippet-resume Caller_1
        asynchronousCall();
    }
}
//@snippet-end Caller_1
