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

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.internalmsg.GlobalStateCompletion;
import org.objectweb.proactive.core.body.ft.internalmsg.OutputCommit;
import org.objectweb.proactive.core.body.ft.message.HistoryUpdater;
import org.objectweb.proactive.core.body.ft.message.MessageInfo;
import org.objectweb.proactive.core.body.ft.message.ReceptionHistory;
import org.objectweb.proactive.core.body.ft.protocols.cic.infos.CheckpointInfoCIC;
import org.objectweb.proactive.core.body.ft.protocols.gen.infos.CheckpointInfoGen;
import org.objectweb.proactive.core.body.ft.protocols.gen.infos.MessageInfoGen;
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
import org.objectweb.proactive.core.util.MutableLong;


/**
 * This class defines a checkpoint server for the CIC protocol.
 * @author The ProActive Team
 * @since 2.2
 */
public class CheckpointServerCIC extends CheckpointServerGen {

    /** Period of the checkpoints garbage collection (ms) */
    public static final int DEFAULT_GC_PERIOD = 40000;

    // handling histories
    protected Hashtable<UniqueID, ReceptionHistory> histories;

    // garbage collection
    private ActiveQueue gc;

    public CheckpointServerCIC(FTServer server) {
        super(server);

        this.histories = new Hashtable<UniqueID, ReceptionHistory>();

        // garbage collection
        this.gc = new ActiveQueue("ActiveQueue: GC");
        gc.start();
        gc.addJob(new GarbageCollectionJob(this, DEFAULT_GC_PERIOD));
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#storeCheckpoint(org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint, int)
     */
    public synchronized int storeCheckpoint(Checkpoint c, int incarnation) {
        if (incarnation < this.globalIncarnation) {
            logger.warn("** WARNING ** : Object with incarnation " + incarnation +
                " is trying to store checkpoint");
            return 0;
        }

        List<Checkpoint> ckptList = checkpointStorage.get(c.getBodyID());

        // the first checkpoint ...
        if (ckptList == null) {
            // new storage slot
            List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

            //dummy first checkpoint
            checkpoints.add(new Checkpoint());
            UniqueID id = c.getBodyID();
            checkpointStorage.put(id, checkpoints);
            checkpoints.add(c);
            // new history slot
            this.histories.put(c.getBodyID(), new ReceptionHistory());
            // new greatestHisto slot
            this.greatestCommitedHistory.put(c.getBodyID(), new MutableInteger(0));
        } else {
            //add checkpoint
            ckptList.add(c);
        }

        // updating monitoring
        int index = ((CheckpointInfoGen) (c.getCheckpointInfo())).checkpointIndex;
        if (index > this.lastRegisteredCkpt) {
            this.lastRegisteredCkpt = index;
        }
        MutableInteger currentGlobalState = (this.stateMonitor.get(new MutableInteger(index)));
        if (currentGlobalState == null) {
            // this is the first checkpoint store for the global state index
            this.stateMonitor.put(new MutableInteger(index), new MutableInteger(1));
        } else {
            currentGlobalState.add(1);
        }

        //this.checkLastGlobalState();
        logger.info("[CKPT] Receive checkpoint indexed " + index + " from body " + c.getBodyID() +
            " (used memory = " + this.getUsedMem() + " Kb)"); // + "[" + System.currentTimeMillis() + "]");

        if (displayCkptSize) {
            logger.info("[CKPT] Size of ckpt " + index + " before addInfo : " + this.getSize(c) + " bytes");
        }

        // broadcast history closure if a new globalState is built
        if (this.checkLastGlobalState()) {
            // send a GSC message to all
            for (UniqueID callee : this.checkpointStorage.keySet()) {
                this.server.submitJob(new GSCESender(this.server, callee, new GlobalStateCompletion(
                    this.lastGlobalState)));
            }
        }
        return this.lastGlobalState;
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#commitHistory(org.objectweb.proactive.core.body.ft.message.HistoryUpdater)
     **/
    public synchronized void commitHistory(HistoryUpdater rh) {
        if (rh.incarnation < this.globalIncarnation) {
            logger.warn("** WARNING ** : Object with incarnation " + rh.incarnation +
                " is trying to store checkpoint infos (Current inc = " + this.globalIncarnation + ")");
            return;
        }

        // update the histo if needed
        if (rh.elements != null) {
            ReceptionHistory ih = this.histories.get(rh.owner);
            ih.updateHistory(rh);
        }

        // update the recovery line monitoring
        MutableInteger greatestIndexSent = ((this.greatestCommitedHistory.get(rh.owner)));
        if (greatestIndexSent.getValue() < rh.checkpointIndex) {
            // must update rc monitoring
            greatestIndexSent.setValue(rh.checkpointIndex);
            // inc the rcv counter for the index indexOfCheckpoint
            MutableInteger counter = (this.recoveryLineMonitor.get(greatestIndexSent));
            if (counter == null) {
                // this is the first histo commit with index indexOfCkpt
                this.recoveryLineMonitor.put(new MutableInteger(rh.checkpointIndex), new MutableInteger(1));
            } else {
                counter.add(1);
            }

            // test if a new recovery line has been created
            // update histories if any
            this.checkRecoveryLine();
        }
    }

    //return true if the recoveryline has changed
    protected boolean checkRecoveryLine() {
        int systemSize = this.server.getSystemSize();
        MutableInteger nextPossible = (this.recoveryLineMonitor
                .get(new MutableInteger(this.recoveryLine + 1)));

        // THIS PART MUST BE ATOMIC
        if ((nextPossible != null) && (nextPossible.getValue() == systemSize)) {
            // a new recovery line has been created
            // update histories
            for (UniqueID key : this.histories.keySet()) {
                ReceptionHistory cur = (this.histories.get(key));
                long nextBase = ((CheckpointInfoGen) (this.getCheckpoint(key, this.recoveryLine + 1)
                        .getCheckpointInfo())).lastRcvdRequestIndex + 1;
                cur.goToNextBase(nextBase);
                cur.confirmLastUpdate();
            }

            // a new rec line has been created
            this.recoveryLine = this.recoveryLine + 1;
            logger.info("[CKPT] Recovery line is " + this.recoveryLine);
            return true;
        }
        return false;
    }

    // protected accessors
    protected void internalRecover(UniqueID failed) {
        try {
            int globalState = 0;
            synchronized (this) {
                globalState = this.recoveryLine;
                this.globalIncarnation++;
                logger.info("[RECOVERY] Recovering system from " + globalState + " with incarnation " +
                    this.globalIncarnation);

                this.lastGlobalState = globalState;
                this.lastRegisteredCkpt = globalState;
                this.recoveryLine = globalState;
                this.stateMonitor = new Hashtable<MutableInteger, MutableInteger>();
                this.recoveryLineMonitor = new Hashtable<MutableInteger, MutableInteger>();

                // delete unusable checkpoints
                for (List<Checkpoint> ckpts : this.checkpointStorage.values()) {
                    while (ckpts.size() > (globalState + 1)) {
                        ckpts.remove(globalState + 1);
                    }
                }

                // set all the system in recovery state
                for (UniqueID current : this.checkpointStorage.keySet()) {
                    this.server.updateState(current, RecoveryProcess.RECOVERING);
                }

                // reinit hisotries; delete not recoverable parts of histories
                for (ReceptionHistory hist : this.histories.values()) {
                    hist.compactHistory();
                }
            } // end synchronize

            // for waiting the end of the recovery
            Vector<JobBarrier> barriers = new Vector<JobBarrier>();

            // send checkpoints
            for (UniqueID current : this.checkpointStorage.keySet()) {

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
            for (JobBarrier barrier : barriers) {
                barrier.waitForJobCompletion();
            }
        } catch (NodeException e) {
            logger.error("[RECOVERY] **ERROR** Unable to send checkpoint for recovery");
            e.printStackTrace();
        }
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#outputCommit(org.objectweb.proactive.core.body.ft.message.MessageInfo)
     */
    public synchronized void outputCommit(MessageInfo mi) {
        Hashtable<UniqueID, MutableLong> vectorClock = ((MessageInfoGen) mi).vectorClock;

        // must store at least each histo up to vectorClock[id]
        // <ATOMIC>
        for (Entry<UniqueID, MutableLong> entry : vectorClock.entrySet()) {
            UniqueID id = entry.getKey();
            MutableLong ml = entry.getValue();
            ReceptionHistory ih = (this.histories.get(id));

            // first test if a history retreiving is necessary
            // i.e. if vc[id]<=histories[id].lastCommited
            long lastCommited = ih.getLastCommited();
            long index = ml.getValue();
            if (lastCommited < index) {
                try {
                    UniversalBody target = this.server.getLocation(id);
                    HistoryUpdater rh = (HistoryUpdater) (target.receiveFTMessage(new OutputCommit(
                        lastCommited + 1, index)));
                    ih.updateHistory(rh);
                } catch (RemoteException e) {
                    logger.error("**ERROR** Unable to retreive history of " + id);
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error("**ERROR** Unable to retreive history of " + id);
                    e.printStackTrace();
                }
            }
        }

        // </ATOMIC>
        // wait for completion of histo retreiving
        // here we can commit alteration on histories
        for (ReceptionHistory element : this.histories.values()) {
            element.confirmLastUpdate();
        }
    }

    /**
     * Reintialize the server.
     */
    @Override
    public void initialize() {
        super.initialize();

        this.histories = new Hashtable<UniqueID, ReceptionHistory>();
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
                for (List<Checkpoint> ckpts : server.checkpointStorage.values()) {
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
