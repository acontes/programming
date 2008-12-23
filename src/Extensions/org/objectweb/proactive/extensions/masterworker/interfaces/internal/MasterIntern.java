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
package org.objectweb.proactive.extensions.masterworker.interfaces.internal;

import org.objectweb.proactive.extensions.masterworker.TaskException;
import org.objectweb.proactive.extensions.masterworker.core.IsClearingError;
import org.objectweb.proactive.extensions.masterworker.interfaces.SubMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.Task;

import java.io.Serializable;
import java.util.List;


public interface MasterIntern {

    /**
    * Internal version of the solve method
    * @param tasks tasks to compute
    */
    //@snippet-start masterworker_solve
    public void solveIntern(final String originatorName, long divisibleTaskId, long taskIdCounter,
            final List<? extends Task<? extends Serializable>> tasks) throws IsClearingError;

    //@snippet-end masterworker_solve
    //@snippet-start masterworker_collection
    /**
     * Wait for all results, will block until all results are computed <br>
     * The ordering of the results depends on the result reception mode in use <br>
     * @return a collection of objects containing the result
     * @param originatorName name of the worker initiating the call
     * @throws org.objectweb.proactive.extensions.masterworker.TaskException if a task threw an Exception
     */
    List<Serializable> waitAllResults(final String originatorName, final int waitId) throws TaskException,
            IsClearingError;

    /**
     * Wait for the first result available <br>
     * Will block until at least one Result is available. <br>
     * Note that in SubmittedOrder mode, the method will block until the next result in submission order is available<br>
     * @param originatorName name of the worker initiating the call
     * @return an object containing the result
     * @throws TaskException if the task threw an Exception
     */
    Serializable waitOneResult(final String originatorName, final int waitId) throws TaskException,
            IsClearingError;

    /**
    * Wait for at least one result is available <br>
    * If there are more results availables at the time the request is executed, then every currently available results are returned
    * Note that in SubmittedOrder mode, the method will block until the next result in submission order is available and will return
    * as many successive results as possible<br>
    * @param originatorName name of the worker initiating the call
    * @return a collection of objects containing the results
    * @throws TaskException if the task threw an Exception
    */
    List<Serializable> waitSomeResults(final String originatorName, final int waitId) throws TaskException;

    /**
     * Wait for a number of results<br>
     * Will block until at least k results are available. <br>
     * The ordering of the results depends on the result reception mode in use <br>
     * @param k the number of results to wait for
     * @param originatorName name of the worker initiating the call
     * @return a collection of objects containing the results
     * @throws TaskException if the task threw an Exception
     */
    List<Serializable> waitKResults(final String originatorName, final int waitId, int k)
            throws TaskException, IsClearingError;

    //@snippet-end masterworker_collection

    /**
     * Tells if the master is completely empty (i.e. has no result to provide and no tasks submitted)
     * @param originatorName name of the worker initiating the call (null, if it's the main client)
     * @return the answer
     */
    boolean isEmpty(final String originatorName) throws IsClearingError;

    /**
     * Tells how many tasks have been submitted to the master
     * @param originatorName name of the worker initiating the call (null, if it's the main client)
     * @return number of tasks submitted
     * @throws org.objectweb.proactive.extensions.masterworker.core.IsClearingError
     */
    int countPending(final String originatorName) throws IsClearingError;

    /**
     * Returns the number of available results <br/>
     * @param originatorName name of the worker initiating the call (null, if it's the main client)
     * @return the answer
     */
    int countAvailableResults(final String originatorName) throws IsClearingError;

    /**
     * Sets the current ordering mode <br/>
     * If reception mode is switched while computations are in progress,<br/>
     * then subsequent calls to waitResults methods will be done according to the new mode.<br/>
     * @param mode the new mode for result gathering
     */
    void setResultReceptionOrder(final String originatorName, final SubMaster.OrderingMode mode)
            throws IsClearingError;

}
