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
package org.objectweb.proactive.core.body.ft.protocols.cic.managers;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.objectweb.proactive.api.PAFaultTolerance;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.checkpointing.CheckpointInfo;
import org.objectweb.proactive.core.body.ft.message.ReplyLog;
import org.objectweb.proactive.core.body.ft.message.RequestLog;
import org.objectweb.proactive.core.body.ft.protocols.cic.infos.CheckpointInfoCIC;
import org.objectweb.proactive.core.body.ft.protocols.cic.infos.MessageInfoCIC;
import org.objectweb.proactive.core.body.ft.protocols.gen.infos.CheckpointInfoGen;
import org.objectweb.proactive.core.body.ft.protocols.gen.managers.FTManagerGen;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.request.AwaitedRequest;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;


/**
 * This class implements a Communication Induced Checkpointing protocol for ProActive.
 * This FTManager is linked to each fault-tolerant active object.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class FTManagerCIC extends FTManagerGen {

    @Override
    public int init(AbstractBody owner) throws ProActiveException {
        super.init(owner);
        this.forSentRequest = new MessageInfoCIC();
        this.forSentReply = new MessageInfoCIC();
        return 0;
    }

    /*
     * Perform a checkpoint with index = current + 1
     */
    @Override
    protected Checkpoint checkpoint(Request pendingRequest) {
        //stop accepting communication
        (owner).blockCommunication();
        // synchronized on hisotry to avoid hisot commit during checkpoint
        synchronized (this.historyLock) {
            Checkpoint c;

            //long start;
            //long end;
            //start = System.currentTimeMillis();
            //System.out.println("BEGIN CHECKPOINT : used mem = " + this.getUsedMem() );
            synchronized (this) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[CIC] Checkpointing with index = " + (this.checkpointIndex + 1));
                }

                // create infos for checkpoint
                CheckpointInfoCIC ci = new CheckpointInfoCIC();
                this.extendReplyLog(this.checkpointIndex + 1);
                this.extendRequestLog(this.checkpointIndex + 1);
                ci.replyToResend = (this.replyToResend.get(Integer.valueOf(this.checkpointIndex + 1)));
                ci.requestToResend = (this.requestToResend.get(Integer.valueOf(this.checkpointIndex + 1)));
                ci.pendingRequest = pendingRequest;
                ci.checkpointIndex = this.checkpointIndex + 1;

                // delete logs
                this.replyToResend.remove(Integer.valueOf(this.checkpointIndex + 1));
                this.requestToResend.remove(Integer.valueOf(this.checkpointIndex + 1));

                // inc checkpoint index
                this.checkpointIndex++;

                // Reset history only if OC is not possible
                if (!FTManagerGen.isOCEnable) {
                    this.history = new Vector<UniqueID>();
                    this.historyBaseIndex = this.deliveredRequestsCounter + 1;
                    this.lastCommitedIndex = this.deliveredRequestsCounter;
                }

                // current informations must not be stored in the checkpoint
                Hashtable<Integer, Vector<RequestLog>> requestToSendTMP = this.requestToResend;
                this.requestToResend = null;
                Hashtable<Integer, Vector<ReplyLog>> replyToSendTMP = this.replyToResend;
                this.replyToResend = null;
                Vector<UniqueID> historyTMP = this.history;
                this.history = null;
                Vector<AwaitedRequest> awaitedRequestTMP = this.awaitedRequests;
                this.awaitedRequests = null;

                // record the next history base index
                ci.lastRcvdRequestIndex = this.deliveredRequestsCounter;
                // checkpoint the active object
                this.setCheckpointTag(true);
                c = new Checkpoint(owner, this.additionalCodebase);
                // add info to checkpoint
                c.setCheckpointInfo(ci);

                // send it to server
                this.storage.storeCheckpoint(c, this.incarnation);
                this.setCheckpointTag(false);

                // restore current informations
                this.replyToResend = replyToSendTMP;
                this.requestToResend = requestToSendTMP;
                this.history = historyTMP;
                this.awaitedRequests = awaitedRequestTMP;

                // this checkpoint has to be completed with its minimal hisotry
                this.completingCheckpoint = true;

                // reninit checkpoint values
                this.checkpointTimer = System.currentTimeMillis();
            }

            //end = System.currentTimeMillis();
            //System.out.println("[BENCH] Cumulated Ckpt time at " + this.checkpointIndex + " : " + this.cumulatedCheckpointTime + " ms");// + System.currentTimeMillis() + "]");
            //System.out.println("END CHECKPOINTING : used mem = " + this.getUsedMem());
            (owner).acceptCommunication();
            return c;
        }
    }

    // Active Object is created but not started
    @Override
    public int beforeRestartAfterRecovery(CheckpointInfo ci, int inc) {
        CheckpointInfoGen cic = (CheckpointInfoGen) ci;
        BlockingRequestQueue queue = (owner).getRequestQueue();
        int index = cic.checkpointIndex;
        try {
            index = PAFaultTolerance.getInstance().getLastGlobalCheckpointNumber();
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //	reinit ft values
        this.history = new Vector<UniqueID>();
        this.completingCheckpoint = false;
        this.lastCommitedIndex = index;//cic.lastCommitedIndex;
        // historized requests are supposed to be "already received"
        this.deliveredRequestsCounter = index;//cic.lastCommitedIndex; //cic.lastRcvdRequestIndex;
        // new history then begin at the end of the history of the checkpoint

        this.historyBaseIndex = index;//cic.lastCommitedIndex + 1; //;cic.lastRcvdRequestIndex+1;

        // HERE, we need a proof that running in "histo mode" is equivalent that
        // running in normal mode from the end of the histo.
        this.awaitedRequests = new Vector<AwaitedRequest>();
        this.replyToResend = new Hashtable<Integer, Vector<ReplyLog>>();
        this.requestToResend = new Hashtable<Integer, Vector<RequestLog>>();
        this.checkpointIndex = index;
        this.nextMax = index;
        this.checkpointTimer = System.currentTimeMillis();
        this.historyIndex = index;
        this.lastRecovery = index;
        this.incarnation = inc;

        //add pending request to reuqestQueue
        Request pendingRequest = cic.pendingRequest;

        //pending request could be null with OOSPMD synchronization
        if (pendingRequest != null) {
            queue.addToFront(pendingRequest);
        }

        //add orphan-tagged requests in request queue
        //this requests are also added to this.awaitedRequests
        this.filterQueue(queue, cic);

        // building history
        // System.out.println(""+ this.ownerID + " History size : " + cic.history.size());
        Iterator<UniqueID> itHistory = cic.history.iterator();
        while (itHistory.hasNext()) {
            UniqueID cur = itHistory.next();
            AwaitedRequest currentAwaitedRequest = new AwaitedRequest(cur);
            queue.add(currentAwaitedRequest);
            this.awaitedRequests.add(currentAwaitedRequest);
        }

        //enable communication
        //System.out.println("[CIC] enable communication");
        (owner).acceptCommunication();

        // update servers
        this.location.updateLocation(ownerID, owner.getRemoteAdapter());
        this.recovery.updateState(ownerID, RecoveryProcess.RUNNING);

        // resend all in-transit message
        this.sendLogs((CheckpointInfoGen) ci);

        return 0;
    }

}
