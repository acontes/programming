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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
//@snippet-start primes_distributedmw_task
//@snippet-start primes_distributedmw_task_skeleton
package org.objectweb.proactive.examples.userguide.primes.distributedmw;

import org.objectweb.proactive.extensions.masterworker.interfaces.Task;
import org.objectweb.proactive.extensions.masterworker.interfaces.WorkerMemory;


/**
 * Task to find if any number in a specified interval divides the given
 * candidate
 * 
 * @author The ProActive Team
 * 
 */
public class FindPrimeTask implements Task<Boolean> {

    private long begin;
    private long end;
    private long taskCandidate;

    //TODO 1. Write the constructor for this task
    public FindPrimeTask(long taskCandidate, long begin, long end) {
        //@snippet-break primes_distributedmw_task_skeleton
        this.begin = begin;
        this.end = end;
        this.taskCandidate = taskCandidate;
        //@snippet-resume primes_distributedmw_task_skeleton
    }

    //TOOD 2. Fill the code that checks if the taskCandidate
    // is prime. Note that no wrappers are needed !
    public Boolean run(WorkerMemory memory) {
        //@snippet-break primes_distributedmw_task_skeleton
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (long divider = begin; divider < end; divider++) {
            if ((taskCandidate % divider) == 0) {
                return Boolean.valueOf(false);
            }
        }
        //@snippet-resume primes_distributedmw_task_skeleton
        return Boolean.valueOf(true);
    }
}
//@snippet-end primes_distributedmw_task
//@snippet-end primes_distributedmw_task_skeleton