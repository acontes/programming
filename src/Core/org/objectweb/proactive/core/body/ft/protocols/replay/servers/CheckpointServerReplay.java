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
package org.objectweb.proactive.core.body.ft.protocols.replay.servers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.message.ReceptionHistory;
import org.objectweb.proactive.core.body.ft.protocols.gen.servers.CheckpointServerGen;
import org.objectweb.proactive.core.body.ft.protocols.replay.infos.CheckpointInfoReplay;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryJob;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.util.JobBarrier;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.MutableInteger;


/**
 * This class defines a checkpoint server for the Replay protocol.
 * @author The ProActive Team
 * @since 2.2
 */
public class CheckpointServerReplay extends CheckpointServerGen {

    public CheckpointServerReplay(FTServer server) {
        super(server);
    }

    // protected accessors
    protected void internalRecover(UniqueID failed) {
        System.out.println("**********************");
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

                //                // delete unusable checkpoints
                //                Iterator<List<Checkpoint>> it = this.checkpointStorage.values().iterator();
                //                while (it.hasNext()) {
                //                    List<Checkpoint> ckpts = it.next();
                //                    while (ckpts.size() > (globalState + 1)) {
                //                        ckpts.remove(globalState + 1);
                //                    }
                //                }

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
                CheckpointInfoReplay cic = (CheckpointInfoReplay) (toSend.getCheckpointInfo());
                ReceptionHistory histo = ((this.histories.get(current)));
                cic.history = histo.getRecoverableHistory();
                // set the last commited index
                cic.lastCommitedIndex = histo.getLastRecoverable();

                UniversalBody toRecover = (this.server.getLocation(current));

                String nodeURL = toRecover.getNodeURL();
                Node node = NodeFactory.getNode(nodeURL);
                barriers.add(this.server.submitJobWithBarrier(new RecoveryJob(toSend, this.globalIncarnation,
                    node)));
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
        System.out.println("**********************");
    }

}
