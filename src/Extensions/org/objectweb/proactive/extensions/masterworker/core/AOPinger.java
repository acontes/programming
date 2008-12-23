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

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.body.exceptions.BodyTerminatedRequestException;
import org.objectweb.proactive.core.body.exceptions.SendRequestCommunicationException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerDeadListener;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerWatcher;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map.Entry;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * The Pinger Active Object is responsible for watching workers'activity. <br>
 * It reports workers failure to the Master<br>
 *
 * @author The ProActive Team
 */
public class AOPinger implements WorkerWatcher, RunActive, InitActive, Serializable {

    /**
     *
     */

    /** pinger log4j logger */
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_PINGER);
    private static final boolean debug = logger.isDebugEnabled();

    /** Stub on the active object */
    private AOPinger stubOnThis;

    /** is this active object terminated */
    private boolean terminated;

    /** interval when workers are sent a ping message */
    private long pingPeriod;

    /** Who will be notified when workers are dead (in general : the master) */
    private WorkerDeadListener listener;

    /** Worker group */
    private HashMap<String, Worker> workerGroup;

    /** for internal use */
    private transient Thread localThread;

    /** ProActive empty constructor */
    public AOPinger() {
    }

    /**
     * Creates a pinger with the given listener
     *
     * @param listener object which will be notified when a worker is dead
     */
    public AOPinger(final WorkerDeadListener listener) {
        this.listener = listener;
        terminated = false;
        pingPeriod = Long.parseLong(PAProperties.PA_MASTERWORKER_PINGPERIOD.getValue());

        workerGroup = new HashMap<String, Worker>();
    }

    /** {@inheritDoc} */
    public void addWorkerToWatch(final Worker worker, final String workerName) {
        workerGroup.put(workerName, worker);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void initActivity(final Body body) {

        stubOnThis = (AOPinger) PAActiveObject.getStubOnThis();
        body.setImmediateService("terminate");

    }

    /** {@inheritDoc} */
    public void removeWorkerToWatch(final String workerName) {
        if (!terminated) {

            if (workerGroup.containsKey(workerName)) {
                workerGroup.remove(workerName);
            }
        }

    }

    /** {@inheritDoc} */
    public void runActivity(final Body body) {
        localThread = Thread.currentThread();
        Service service = new Service(body);
        try {
            while (!terminated) {

                long checkpoint1 = System.currentTimeMillis();

                Set<String> keySet = new HashSet<String>(workerGroup.keySet());

                for (String workerName : keySet) {
                    try {
                        if (debug) {
                            logger.debug("Pinging " + workerName);
                        }
                        (workerGroup.get(workerName)).heartBeat();
                    } catch (Exception e) {
                        if (debug) {
                            logger.debug("Misfunctioning worker " + workerName + ", investigating...");
                        }
                        //                        while (service.hasRequestToServe()) {
                        //                        	service.serveOldest();
                        //                        }
                        if (!terminated) {
                            try {
                                stubOnThis.workerMissing(workerName);
                            } catch (Exception exp) {
                                if (debug) {
                                    logger.debug("Pinging " + workerName +
                                        " has a error! Terminate the Pinger");
                                    terminated = true;
                                }
                            }
                        }

                        //                        try{
                        //                        	if(!terminated && null != stubOnThis && null != workerName){
                        //                        		try{
                        //                        		
                        //                        		} catch (NullPointerException exp){
                        //                            		if (debug) {
                        //                            			logger.debug("Pinging " + workerName + " has a error! Terminate the Pinger");
                        //                                        terminated = true;
                        //                            		}
                        //                        		}
                        //                        	}
                        //                        	else{
                        //                        		terminated = true;
                        //                        		break;
                        //                        	}
                        //                        } catch (SendRequestCommunicationException exp) {
                        //                            if (debug) {
                        //                                logger.debug("listener has already been freed.");
                        //                            }
                        //                        } catch (BodyTerminatedRequestException exp1){
                        //                        	if (debug) {
                        //                                logger.debug("listener has already been terminaterd.");
                        //                            }
                        //                        } catch (Exception exp2){
                        //                    		if (debug) {
                        //                    			logger.debug("Zhen ta ma yumen, daodi shui shi null a?.");
                        //                    			exp2.printStackTrace();
                        //                    		}
                        //                    	}
                    }
                }

                long checkpoint2 = System.currentTimeMillis();
                if (pingPeriod > (checkpoint2 - checkpoint1)) {
                    Thread.sleep(pingPeriod - (checkpoint2 - checkpoint1));
                }

                // we serve everything
                while (!terminated && service.hasRequestToServe()) {
                    service.serveOldest();
                }

            }
        } catch (InterruptedException ex) {
            // do not print message, pinger is terminating

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (debug) {
            logger.debug("Pinger Terminated...");
        }

        stubOnThis = null;
        // we clear the service to avoid dirty pending requests
        service.flushAll();
        // we block the communications because a getTask request might still be coming from a worker created just before the master termination
        body.blockCommunication();
        // we finally terminate the pinger
        body.terminate();
    }

    /** {@inheritDoc} */
    public void setPingPeriod(long periodMillis) {
        this.pingPeriod = periodMillis;
    }

    /**
     * Reports that a worker is missing
     *
     * @param worker the missing worker
     */
    public void workerMissing(final String workerName) {
        if (!terminated) {
            if (debug) {
                logger.debug("A worker" + workerName + " is missing...reporting back to the Master");
            }

            try {
                if (workerGroup.containsKey(workerName)) {
                    listener.isDead(workerName);
                    workerGroup.remove(workerName);
                }
            } catch (SendRequestCommunicationException exp) {
                if (debug) {
                    logger.debug("listener has already been freed.");
                }
            } catch (BodyTerminatedRequestException exp1) {
                if (debug) {
                    logger.debug("listener has already been terminaterd.");
                }
            } catch (Exception exp2) {
                if (debug) {
                    logger.debug("Exception occurs when report the missing of " + workerName +
                        "to listener.\n");
                    exp2.printStackTrace();
                }
            }
        }
    }

    /** {@inheritDoc} */
    public BooleanWrapper terminate() {
        if (debug) {
            logger.debug("Terminating Pinger...");
        }
        this.terminated = true;

        workerGroup.clear();
        workerGroup = null;

        localThread.interrupt();
        localThread = null;

        return new BooleanWrapper(true);
    }

}
