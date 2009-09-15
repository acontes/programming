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
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.body.ft.protocols.cic.servers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.message.ReceptionHistory;
import org.objectweb.proactive.core.body.ft.protocols.cic.infos.CheckpointInfoCIC;
import org.objectweb.proactive.core.body.ft.protocols.gen.servers.CheckpointServerGen;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryJob;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.util.ActiveQueue;
import org.objectweb.proactive.core.body.ft.servers.util.ActiveQueueJob;
import org.objectweb.proactive.core.body.ft.servers.util.JobBarrier;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.MutableInteger;


/**
 * This class defines a checkpoint server for the CIC protocol.
 * @author The ProActive Team
 * @since 2.2
 */
public class CheckpointServerCIC extends CheckpointServerGen {

    /** Period of the checkpoints garbage collection (ms) */
    public static final int DEFAULT_GC_PERIOD = 40000;

    // garbage collection
    private ActiveQueue gc;

    public CheckpointServerCIC(FTServer server) {
        super(server);

        // garbage collection
        this.gc = new ActiveQueue("ActiveQueue: GC");
        gc.start();
        gc.addJob(new GarbageCollectionJob(this, DEFAULT_GC_PERIOD));

    }

    // protected accessors
    protected void internalRecover(UniqueID failed) {
        try {
            Enumeration<UniqueID> itBodies = null;
            int globalState = 0;
            synchronized (this) {
                globalState = this.recoveryLine;
                this.globalIncarnation++;
                logger.info("[RECOVERY] Recovering system from " + globalState + " with incarnation " +
                    this.globalIncarnation);

                itBodies = this.checkpointStorage.keys();
                this.lastGlobalState = globalState;
                this.lastRegisteredCkpt = globalState;
                this.recoveryLine = globalState;
                this.stateMonitor = new Hashtable<MutableInteger, MutableInteger>();
                this.recoveryLineMonitor = new Hashtable<MutableInteger, MutableInteger>();

                // delete unusable checkpoints
                Iterator<List<Checkpoint>> it = this.checkpointStorage.values().iterator();
                while (it.hasNext()) {
                    List<Checkpoint> ckpts = it.next();
                    while (ckpts.size() > (globalState + 1)) {
                        ckpts.remove(globalState + 1);
                    }
                }

                // set all the system in recovery state
                while (itBodies.hasMoreElements()) {
                    UniqueID current = (itBodies.nextElement());
                    this.server.updateState(current, RecoveryProcess.RECOVERING);
                }

                //reinit the iterator
                itBodies = this.checkpointStorage.keys();

                // reinit hisotries; delete not recoverable parts of histories
                Enumeration<ReceptionHistory> itHistories = this.histories.elements();
                while (itHistories.hasMoreElements()) {
                    itHistories.nextElement().compactHistory();
                }
            } // end synchronize

            // for waiting the end of the recovery
            Vector<JobBarrier> barriers = new Vector<JobBarrier>();

            // send checkpoints
            while (itBodies.hasMoreElements()) {
                UniqueID current = (itBodies.nextElement());

                //Checkpoint toSend = this.server.getCheckpoint(current,globalState);
                Checkpoint toSend = this.getCheckpoint(current, globalState);

                // update history of toSend
                CheckpointInfoCIC cic = (CheckpointInfoCIC) (toSend.getCheckpointInfo());
                ReceptionHistory histo = ((this.histories.get(current)));
                cic.history = histo.getRecoverableHistory();
                // set the last commited index
                cic.lastCommitedIndex = histo.getLastRecoverable();

                if (current.equals(failed)) {
                    //look for a new Runtime for this oa
                    Node node = this.server.getFreeNode();

                    //if (node==null)return;
                    barriers.add(this.server.submitJobWithBarrier(new RecoveryJob(toSend,
                        this.globalIncarnation, node)));
                } else {
                    UniversalBody toRecover = (this.server.getLocation(current));

                    // test current OA so as to handle mutliple failures
                    boolean isDead = false;
                    try {
                        isDead = this.server.isUnreachable(toRecover);
                    } catch (Exception e) {
                    }
                    if (isDead) {
                        Node node = this.server.getFreeNode();

                        //if (node==null)return;
                        barriers.add(this.server.submitJobWithBarrier(new RecoveryJob(toSend,
                            this.globalIncarnation, node)));
                    } else {
                        String nodeURL = toRecover.getNodeURL();
                        Node node = NodeFactory.getNode(nodeURL);
                        barriers.add(this.server.submitJobWithBarrier(new RecoveryJob(toSend,
                            this.globalIncarnation, node)));
                    }
                }
            }

            // MUST WAIT THE TERMINAISON OF THE RECOVERY !
            // FaultDetection thread wait for the completion of the recovery
            // If a failure occurs during rec, it will be detected by an active object
            Iterator<JobBarrier> itBarriers = barriers.iterator();
            while (itBarriers.hasNext()) {
                (itBarriers.next()).waitForJobCompletion();
            }
        } catch (NodeException e) {
            logger.error("[RECOVERY] **ERROR** Unable to send checkpoint for recovery");
            e.printStackTrace();
        }
    }

    /**
     * Reintialize the server.
     */
    @Override
    public void initialize() {
        super.initialize();
        // kill GC thread
        gc.killMe();
        gc = new ActiveQueue("ActiveQueue: GC");
        gc.start();
        gc.addJob(new GarbageCollectionJob(this, DEFAULT_GC_PERIOD));
    }

    //////////////////////////////////////////
    ////// JOBS FOR ACTIVE QUEUE /////////////
    //////////////////////////////////////////
    private static class GarbageCollectionJob implements ActiveQueueJob {
        // this job is CIC specific
        private CheckpointServerCIC server;

        // period of garbage collection
        private int period;

        // constructor
        protected GarbageCollectionJob(CheckpointServerCIC server, int period) {
            this.server = server;
            this.period = period;
        }

        /**
         * Perform garbage collection : Delete unsable checkpoints,
         * i.e. index < currentRecoveryLine.
         * NOTE : this job is an infinite job.
         */
        public void doTheJob() {
            while (true) {
                try {
                    Thread.sleep(period);
                    CheckpointServerCIC.logger.info("[CKPT] Performing Garbage Collection...");
                    this.garbageCollection();
                    CheckpointServerCIC.logger.info("[CKPT] Garbage Collection done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Delete unsable checkpoints, i.e. index < currentRecoveryLine
        protected void garbageCollection() {
            boolean hasGarbaged = false;
            synchronized (server) {
                int recLine = server.recoveryLine;
                Iterator<List<Checkpoint>> it = server.checkpointStorage.values().iterator();
                while (it.hasNext()) {
                    List<Checkpoint> ckpts = it.next();
                    for (int i = 0; i < recLine; i++) {
                        if (ckpts.get(i) != null) {
                            hasGarbaged = true;
                            ckpts.remove(i);
                            ckpts.add(i, null);
                        }
                    }
                }
            }
            if (hasGarbaged) {
                System.gc();
            }
        }
    }
}
