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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.objectweb.proactive.api.PAFaultTolerance;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.BodyImpl;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.checkpointing.CheckpointInfo;
import org.objectweb.proactive.core.body.ft.message.ReplyLog;
import org.objectweb.proactive.core.body.ft.message.RequestLog;
import org.objectweb.proactive.core.body.ft.protocols.gen.infos.CheckpointInfoGen;
import org.objectweb.proactive.core.body.ft.protocols.gen.managers.FTManagerGen;
import org.objectweb.proactive.core.body.ft.protocols.replay.infos.CheckpointInfoReplay;
import org.objectweb.proactive.core.body.ft.protocols.replay.infos.MessageInfoReplay;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.request.AwaitedRequest;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestImpl;


/**
 * This class implements a Communication Induced Checkpointing protocol for ProActive.
 * This FTManager is linked to each fault-tolerant active object.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public class FTManagerReplay extends FTManagerGen {
    private static final long serialVersionUID = -6984273235123773473L;

    protected boolean replayMode;
    protected boolean isKilled;
    protected Map<Integer, List<UniqueID>> requestReceived;

    @Override
    public int init(AbstractBody owner) throws ProActiveException {
        super.init(owner);
        this.forSentRequest = new MessageInfoReplay();
        this.forSentReply = new MessageInfoReplay();
        replayMode = false;
        isKilled = false;
        requestReceived = new HashMap<Integer, List<UniqueID>>();
        return 0;
    }

    public void startReplayMode() {
        replayMode = true;
    }

    public void stopReplayMode() {
        replayMode = false;
    }

    public void toggleReplayMode() {
        replayMode = !replayMode;
    }

    public boolean getReplayMode() {
        return replayMode;
    }

    /*
     * return true if this ao have to checkpoint
     */
    @Override
    protected boolean haveToCheckpoint() {
        int currentCheckpointIndex = this.checkpointIndex;
        int currentNextMax = this.nextMax;

        // force to trigger a checkpoint
        if (takeNext > currentCheckpointIndex) {
            return true;
        }

        // do not trigger if we are in a replay phase
        if (replayMode) {
            return false;
        }

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

    public int getIncarnation() {
        return incarnation;
    }

    public static class HistoryInfo implements Serializable {
        private static final long serialVersionUID = 1804783174683221164L;
        public int index;
        public List<UniqueID> hist;

        public HistoryInfo(int index, List<UniqueID> hist) {
            this.index = index;
            this.hist = hist;
        }
    }

    public HistoryInfo getLastHistory() {
        HistoryInfo info;
        synchronized (this.historyLock) {
            info = new HistoryInfo(checkpointIndex, requestReceived.get(checkpointIndex));
        }
        return info;
    }

    private long lastRequestReceived;

    @Override
    public int onReceiveRequest(Request request) {
        request.setFTManager(this);
        int res = this.incarnationTest(request);
        if (!request.ignoreIt()) {
            lastRequestReceived = request.getSequenceNumber();
            // store the request in the history
            synchronized (this.historyLock) {
                List<UniqueID> histList = requestReceived.get(checkpointIndex);
                if (histList == null) {
                    histList = new ArrayList<UniqueID>();
                    requestReceived.put(checkpointIndex, histList);
                }
                histList.add(request.getSourceBodyID());
            }
        }
        return res;
    }

    /*
     * Perform a checkpoint with index = current + 1
     */
    @Override
    protected Checkpoint checkpoint(Request pendingRequest) {
        //stop accepting communication
        (owner).blockCommunication();
        // synchronized on history to avoid histo commit during checkpoint
        synchronized (this.historyLock) {
            Checkpoint c;

            //long start;
            //long end;
            //start = System.currentTimeMillis();
            //System.out.println("BEGIN CHECKPOINT : used mem = " + this.getUsedMem() );
            synchronized (this) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[Replay] Checkpointing with index = " + (this.checkpointIndex + 1));
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

                // remove the pending request from the history
                if (lastRequestReceived == pendingRequest.getSequenceNumber()) {
                    List<UniqueID> hist = requestReceived.get(this.checkpointIndex);
                    if (!hist.isEmpty()) {
                        UniqueID lastID = hist.get(hist.size() - 1);
                        // different requests from different sources can have same sequence number
                        if (lastID.equals(pendingRequest.getSourceBodyID())) {
                            hist.remove(hist.size() - 1);
                        }
                    }
                }

                // store the history
                ((FTServer) storage).storeHistory(requestReceived.get(this.checkpointIndex), this.ownerID,
                        this.checkpointIndex);

                // inc checkpoint index
                this.checkpointIndex++;

                requestReceived.put(this.checkpointIndex, new ArrayList<UniqueID>());

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

                // reinit checkpoint values
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
            e.printStackTrace();
        }

        //	reinit ft values
        this.history = new Vector<UniqueID>();
        this.completingCheckpoint = false;
        this.lastCommitedIndex = cic.lastCommitedIndex;
        // historized requests are supposed to be "already received"
        this.deliveredRequestsCounter = cic.lastCommitedIndex; //cic.lastRcvdRequestIndex;
        // new history then begin at the end of the history of the checkpoint

        this.historyBaseIndex = cic.lastCommitedIndex + 1; //;cic.lastRcvdRequestIndex+1;

        // HERE, we need a proof that running in "histo mode" is equivalent that
        // running in normal mode from the end of the histo.
        this.awaitedRequests = new Vector<AwaitedRequest>();
        this.replyToResend = new Hashtable<Integer, Vector<ReplyLog>>();
        this.requestToResend = new Hashtable<Integer, Vector<RequestLog>>();
        List<UniqueID> histList;
        synchronized (this.historyLock) {
            // must be performed BEFORE changing checkpointIndex
            requestReceived.clear();
            histList = ((FTServer) storage).getHistory(ownerID, index);
            if (histList != null) {
                requestReceived.put(index, histList);
            }
        }
        this.checkpointIndex = index;
        this.nextMax = index;
        this.checkpointTimer = System.currentTimeMillis();
        this.historyIndex = index;
        this.lastRecovery = index;
        this.incarnation = inc;

        //add pending request to requestQueue
        Request pendingRequest = cic.pendingRequest;

        //pending request could be null with OOSPMD synchronization
        if (pendingRequest != null) {
            queue.addToFront(pendingRequest);
        }

        //add orphan-tagged requests in request queue
        //this requests are also added to this.awaitedRequests
        this.filterQueue(queue, cic);

        // building history
        /*/
        // System.out.println(""+ this.ownerID + " History size : " + cic.history.size());
        Iterator<UniqueID> itHistory = cic.history.iterator();
        while (itHistory.hasNext()) {
            UniqueID cur = itHistory.next();
            AwaitedRequest currentAwaitedRequest = new AwaitedRequest(cur);
            queue.add(currentAwaitedRequest);
            this.awaitedRequests.add(currentAwaitedRequest);
        }
        /* /
        if (histList != null) {
            System.out.println("Replay: " + ownerID + " add history: " + histList);
            for (UniqueID cur : histList) {
                AwaitedRequest currentAwaitedRequest = new AwaitedRequest(cur);
                queue.add(currentAwaitedRequest);
                this.awaitedRequests.add(currentAwaitedRequest);
            }
        }
        /**/

        //enable communication
        //System.out.println("[CIC] enable communication");
        owner.acceptCommunication();

        owner.registerToJMX();

        // update servers
        this.location.updateLocation(ownerID, owner.getRemoteAdapter());
        this.recovery.updateState(ownerID, RecoveryProcess.RUNNING);

        // resend all in-transit message
        this.sendLogs((CheckpointInfoReplay) ci);

        return 0;
    }

    public void updateLogsLocations(Map<UniversalBody, UniversalBody> newLocations) {
        for (Vector<RequestLog> requests : requestToResend.values()) {
            for (RequestLog request : requests) {
                try {
                    UniversalBody from = request.getDestination();
                    UniversalBody to = newLocations.get(from);
                    request.setDestination(to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Vector<ReplyLog> replies : replyToResend.values()) {
            for (ReplyLog reply : replies) {
                try {
                    UniversalBody from = reply.getDestination();
                    UniversalBody to = newLocations.get(from);
                    reply.setDestination(to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Request request : owner.getRequestQueue()) {
            if (request instanceof RequestImpl) {
                try {
                    UniversalBody from = request.getSender();
                    UniversalBody to = newLocations.get(from);
                    ((RequestImpl) request).setSender(to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
