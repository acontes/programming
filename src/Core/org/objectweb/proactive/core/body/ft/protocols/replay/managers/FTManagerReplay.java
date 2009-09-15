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
package org.objectweb.proactive.core.body.ft.protocols.replay.managers;

import java.util.Hashtable;
import java.util.Vector;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.message.ReplyLog;
import org.objectweb.proactive.core.body.ft.message.RequestLog;
import org.objectweb.proactive.core.body.ft.protocols.gen.managers.FTManagerGen;
import org.objectweb.proactive.core.body.ft.protocols.replay.infos.CheckpointInfoReplay;
import org.objectweb.proactive.core.body.ft.protocols.replay.infos.MessageInfoReplay;
import org.objectweb.proactive.core.body.request.AwaitedRequest;
import org.objectweb.proactive.core.body.request.Request;


/**
 * This class implements a Communication Induced Checkpointing protocol for ProActive.
 * This FTManager is linked to each fault-tolerant active object.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class FTManagerReplay extends FTManagerGen {

    @Override
    public int init(AbstractBody owner) throws ProActiveException {
        super.init(owner);
        this.forSentRequest = new MessageInfoReplay();
        this.forSentReply = new MessageInfoReplay();
        return 0;
    }

    /*
     * return true if this ao have to checkpoint
     */
    @Override
    protected boolean haveToCheckpoint() {
        int currentCheckpointIndex = this.checkpointIndex;
        int currentNextMax = this.nextMax;

        // force to trigger a checkpoint
        if (takeNext)
            return true;

        // do not trigger if we are in a replay phase
        if (incarnation > 1)
            return false;

        // checkpoint if next is greater than index
        if (currentNextMax > currentCheckpointIndex) {
            return true;
        }
        // checkpoint if TTC is elapsed
        else if ((this.checkpointTimer + this.ttc) < System.currentTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Perform a checkpoint with index = current + 1
     */
    @Override
    protected Checkpoint checkpoint(Request pendingRequest) {
        //stop accepting communication
        (owner).blockCommunication();
        takeNext = false;
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
                CheckpointInfoReplay ci = new CheckpointInfoReplay();
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

}
