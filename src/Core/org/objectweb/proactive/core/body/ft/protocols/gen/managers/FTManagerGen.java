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
package org.objectweb.proactive.core.body.ft.protocols.gen.managers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.exception.ProtocolErrorException;
import org.objectweb.proactive.core.body.ft.internalmsg.FTMessage;
import org.objectweb.proactive.core.body.ft.internalmsg.GlobalStateCompletion;
import org.objectweb.proactive.core.body.ft.internalmsg.OutputCommit;
import org.objectweb.proactive.core.body.ft.message.HistoryUpdater;
import org.objectweb.proactive.core.body.ft.message.MessageInfo;
import org.objectweb.proactive.core.body.ft.message.ReplyLog;
import org.objectweb.proactive.core.body.ft.message.RequestLog;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.protocols.gen.infos.CheckpointInfoGen;
import org.objectweb.proactive.core.body.ft.protocols.gen.infos.MessageInfoGen;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.body.message.Message;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.reply.ReplyImpl;
import org.objectweb.proactive.core.body.request.AwaitedRequest;
import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.BlockingRequestQueueImpl;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.mop.Utils;
import org.objectweb.proactive.core.security.exceptions.CommunicationForbiddenException;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.CircularArrayList;
import org.objectweb.proactive.core.util.MutableLong;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class implements a Communication Induced Checkpointing protocol for ProActive.
 * This FTManager is linked to each fault-tolerant active object.
 * @author The ProActive Team
 * @since ProActive 2.2
 */
public abstract class FTManagerGen extends FTManager {

    /**
     *
     */

    /** Value returned by an object if the recieved message must be send again */
    public static final int RESEND_MESSAGE = -3;

    /** Value returned by an object if the sender of the received message must recover asap */
    public static final int RECOVER = -4;

    //logger
    protected static Logger logger = ProActiveLogger.getLogger(Loggers.FAULT_TOLERANCE_CIC);

    // local runtime
    // private static final Runtime runtime = Runtime.getRuntime();
    // FT Values
    protected int incarnation;
    protected int lastRecovery; // index of the last ckpt used for recovery
    protected int checkpointIndex; //index of the latest perfomred checkpoint
    protected long checkpointTimer;
    protected int nextMax;

    // private int nextMin;
    protected int historyIndex; // index of the latest closed history

    // logged messages
    protected Hashtable<Integer, Vector<RequestLog>> requestToResend;
    protected int latestRequestLog;
    protected Hashtable<Integer, Vector<ReplyLog>> replyToResend;
    protected int latestReplyLog;

    // awaited request in owner request queue
    protected Vector<AwaitedRequest> awaitedRequests;

    // pool of MessageInfo
    protected MessageInfoGen forSentRequest;
    protected MessageInfoGen forSentReply;

    // history
    protected Vector<UniqueID> history;

    // cannot lock hisotry itself, because it is modified in synchronized blocks !
    protected final Character historyLock = Character.valueOf('l');

    // protocol for output commit
    protected long deliveredRequestsCounter;
    protected MutableLong lastServedRequestIndex; // reference to localVectorClock[this.ownerID]
    protected Hashtable<UniqueID, MutableLong> localVectorClock; // ids <-> lastServedRequest(local view)
    protected long historyBaseIndex;
    protected long lastCommitedIndex;
    protected boolean completingCheckpoint; // true if the latest checkpoint is still not completed with its minimal history

    // ****** static toggle for OC [TEST] *******
    protected final static boolean isOCEnable = false;

    @Override
    public int init(AbstractBody owner) throws ProActiveException {
        super.init(owner);
        this.incarnation = 1;
        this.checkpointIndex = 0;
        this.historyIndex = 0;
        this.nextMax = 1;
        //this.nextMin = 0;
        this.lastRecovery = 0;
        this.checkpointTimer = System.currentTimeMillis();
        this.requestToResend = new Hashtable<Integer, Vector<RequestLog>>();
        this.latestRequestLog = 0;
        this.replyToResend = new Hashtable<Integer, Vector<ReplyLog>>();
        this.latestReplyLog = 0;
        this.history = new Vector<UniqueID>();
        this.awaitedRequests = new Vector<AwaitedRequest>();
        this.deliveredRequestsCounter = -1;
        this.lastCommitedIndex = -1;
        this.lastServedRequestIndex = new MutableLong(0);
        this.historyBaseIndex = 0;
        this.completingCheckpoint = false;
        this.localVectorClock = new Hashtable<UniqueID, MutableLong>();
        this.localVectorClock.put(this.ownerID, this.lastServedRequestIndex);
        logger.info(" CIC fault-tolerance is enabled for body " + this.ownerID);
        return 0;
    }

    @Override
    public int onReceiveReply(Reply reply) {
        reply.setFTManager(this);
        return this.incarnationTest(reply);
    }

    @Override
    public int onReceiveRequest(Request request) {
        request.setFTManager(this);
        return this.incarnationTest(request);
    }

    /*
     * This method test if the message m has to be take into account by the receiver;
     * If not, set the ignore tag in m.
     */
    private int incarnationTest(Message m) {
        if (this.isSignificant(m)) {
            MessageInfoGen mi = (MessageInfoGen) m.getMessageInfo();
            int localInt = this.incarnation;
            int inc = mi.incarnation;
            if (inc > localInt) {
                // this body will recover
                m.setIgnoreIt(true);
                return FTManagerGen.RESEND_MESSAGE;
            } else if (inc < localInt) {
                // force the sender to recover and ignore this message
                m.setIgnoreIt(true);
                return FTManagerGen.RECOVER;
            }
        }
        return 0; //This value is not returned to the sender
    }

    @Override
    public synchronized int onDeliverReply(Reply reply) {
        int currentCheckpointIndex = this.checkpointIndex;
        if (this.isSignificant(reply)) {
            MessageInfoGen mi = (MessageInfoGen) reply.getMessageInfo();

            // history closure
            this.updateHistory(mi.historyIndex);
            // udpate checkpoint index
            if (mi.checkpointIndex > currentCheckpointIndex) {
                this.nextMax = Math.max(this.nextMax, mi.checkpointIndex);
            }
        }
        return currentCheckpointIndex;
    }

    @Override
    public synchronized int onDeliverRequest(Request request) {
        int currentCheckpointIndex = this.checkpointIndex;

        //System.out.println(""+ this.ownerID + " receive " + request);
        if (this.isSignificant(request)) {
            //System.out.println(""+ this.ownerID + " receive significant " + request);
            MessageInfoGen mi = (MessageInfoGen) request.getMessageInfo();

            // history closure
            this.updateHistory(mi.historyIndex);
            //is there any corresponding awaited request ?
            if (!(this.updateAwaitedRequests(request))) {
                if (FTManagerGen.isOCEnable || this.completingCheckpoint) {
                    synchronized (historyLock) {
                        // if no, an awaited request is added to history
                        history.add(request.getSourceBodyID());
                    }

                    // inc the historized index
                    this.deliveredRequestsCounter++;
                    // manage local vector clock only if needed
                    if (FTManagerGen.isOCEnable) {
                        // set the position in history of the request
                        mi.positionInHistory = this.deliveredRequestsCounter;
                        // update local vector clock
                        this.updateLocalVectorClock(mi.vectorClock);
                    }
                }
            } else {
                //this request must be then ignored...
                request.setIgnoreIt(true);
            }

            // udpate checkpoint index
            int ckptIndex = mi.checkpointIndex;
            if (ckptIndex > currentCheckpointIndex) {
                this.nextMax = Math.max(this.nextMax, ckptIndex);
                // mark the request that is orphan; it will be changed in awaited req in next ckpt
                // oprhan du indexCkpt+1 a mi.ckptIndex compris
                mi.isOrphanFor = (char) ckptIndex;
                //System.out.println("" + this.ownerID + " will have orphans in checkpoint " + ckptIndex);
            }
        }
        return currentCheckpointIndex;
    }

    /*
     * Close and commit the current history if needed.
     */
    private void updateHistory(int index) {
        if (index > this.historyIndex) {
            // commit minimal history to the server
            this.commitHistories(this.checkpointIndex, this.deliveredRequestsCounter, true, true);
            if (this.completingCheckpoint) {
                this.completingCheckpoint = false;
            }
        }
    }

    /*
     * Return true if the message m is significant for the protocol, i.e
     * if it's not sent by a non_ft body nor a half body
     */
    private boolean isSignificant(Message m) {
        return ((m.getMessageInfo() != null) && (!m.getMessageInfo().isFromHalfBody()));
    }

    /*
     * Update the local vector clock regarding the paramater.
     * if local[i]<param[i] or local[i] doesn't exist, then local[i]=param[i];
     */
    private void updateLocalVectorClock(Hashtable<UniqueID, MutableLong> vectorClock) {
        Enumeration<UniqueID> ids = vectorClock.keys();
        MutableLong localClock;
        MutableLong senderClock = null;
        UniqueID id = null;
        while (ids.hasMoreElements()) {
            id = ids.nextElement();
            localClock = (this.localVectorClock.get(id));
            senderClock = (vectorClock.get(id));
            if (localClock == null) {
                // there is no clock for the AO id
                this.localVectorClock.put(id, new MutableLong(senderClock.getValue()));
            } else if (localClock.isLessThan(senderClock)) {
                // local clock is not uptodate
                localClock.setValue(senderClock.getValue());
            }
        }
    }

    @Override
    public synchronized int onSendReplyBefore(Reply reply) {
        // set message info values
        this.forSentReply.checkpointIndex = (char) this.checkpointIndex;
        this.forSentReply.historyIndex = (char) this.historyIndex;
        this.forSentReply.incarnation = (char) this.incarnation;
        this.forSentReply.lastRecovery = (char) this.lastRecovery;
        this.forSentReply.isOrphanFor = Character.MAX_VALUE;
        this.forSentReply.fromHalfBody = false;
        this.forSentReply.vectorClock = null;
        reply.setMessageInfo(this.forSentReply);

        // output commit
        if (FTManagerGen.isOCEnable && this.isOutputCommit(reply)) {
            if (logger.isDebugEnabled()) {
                logger.debug(this.ownerID + " is output commiting for reply " + reply);
            }
            this.storage.outputCommit(this.forSentReply);
        }

        return 0;
    }

    @Override
    public synchronized int onSendRequestBefore(Request request) {
        // set message info values
        this.forSentRequest.checkpointIndex = (char) this.checkpointIndex;
        this.forSentRequest.historyIndex = (char) this.historyIndex;
        this.forSentRequest.incarnation = (char) this.incarnation;
        this.forSentRequest.lastRecovery = (char) this.lastRecovery;
        this.forSentRequest.isOrphanFor = Character.MAX_VALUE;
        this.forSentRequest.fromHalfBody = false;
        if (FTManagerGen.isOCEnable) {
            this.forSentRequest.vectorClock = this.localVectorClock;
        }
        request.setMessageInfo(this.forSentRequest);

        // output commit
        if (FTManagerGen.isOCEnable && this.isOutputCommit(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug(this.ownerID + " is output commiting for request " + request);
            }
            this.storage.outputCommit(this.forSentRequest);
        }
        return 0;
    }

    /*
     * Return true if the sending of the parameter message is an output commit
     * ** TEST IMPLEMENTATION **
     */
    private boolean isOutputCommit(Message m) {
        if (FTManagerGen.isOCEnable) {
            Request r = (Request) m;
            if (r.getMethodName().equals("logEvent")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public synchronized int onSendReplyAfter(Reply reply, int rdvValue, UniversalBody destination) {
        // if return value is RESEND, receiver have to recover --> resend the message
        if (rdvValue == FTManagerGen.RESEND_MESSAGE) {
            try {
                reply.setIgnoreIt(false);
                Thread.sleep(FTManager.TIME_TO_RESEND);
                int rdvValueBis = sendReply(reply, destination);
                return this.onSendReplyAfter(reply, rdvValueBis, destination);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int currentCheckpointIndex = this.checkpointIndex;

        // update checkpoint index
        if (rdvValue > currentCheckpointIndex) {
            this.nextMax = Math.max(this.nextMax, rdvValue);
            // log this in-transit message
            this.extendReplyLog(rdvValue);
            // must make a deep copy of result !
            try {
                Reply toLog = null;

                //try {
                //    toLog = new ReplyImpl(reply.getSourceBodyID(),
                //            reply.getSequenceNumber(), reply.getMethodName(),
                //            (MethodCallResult) Utils.makeDeepCopy(reply.getResult()),
                //            owner.getProActiveSecurityManager());
                //} catch (SecurityNotAvailableException e1) {
                toLog = new ReplyImpl(reply.getSourceBodyID(), reply.getSequenceNumber(), reply
                        .getMethodName(), (MethodCallResult) Utils.makeDeepCopy(reply.getResult()), null);
                //}
                ReplyLog log = new ReplyLog(toLog, destination.getRemoteAdapter());
                for (int i = currentCheckpointIndex + 1; i <= rdvValue; i++) {
                    (this.replyToResend.get(Integer.valueOf(i))).add(log);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public synchronized int onSendRequestAfter(Request request, int rdvValue, UniversalBody destination)
            throws RenegotiateSessionException, CommunicationForbiddenException {
        //	if return value is RESEDN, receiver have to recover --> resend the message
        if (rdvValue == FTManagerGen.RESEND_MESSAGE) {
            try {
                request.resetSendCounter();
                request.setIgnoreIt(false);
                Thread.sleep(FTManager.TIME_TO_RESEND);
                int rdvValueBis = sendRequest(request, destination);
                return this.onSendRequestAfter(request, rdvValueBis, destination);
            } catch (RenegotiateSessionException e1) {
                throw e1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int currentCheckpointIndex = this.checkpointIndex;

        // update checkpoint index
        if (rdvValue > currentCheckpointIndex) {
            this.nextMax = Math.max(this.nextMax, rdvValue);
            // log this in-transit message in the rdvValue-currentIndex next checkpoints
            this.extendRequestLog(rdvValue);
            try {
                //must make deep copy of paramteres
                request.getMethodCall().makeDeepCopyOfArguments();
                //must reset the send counter (this request has not been forwarded)
                request.resetSendCounter();
                RequestLog log = new RequestLog(request, destination.getRemoteAdapter());
                for (int i = currentCheckpointIndex + 1; i <= rdvValue; i++) {
                    //System.out.println(""+this.ownerID + " logs a request for " + destination.getID());
                    this.requestToResend.get(Integer.valueOf(i)).add(log);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int onServeRequestBefore(Request request) {
        // checkpoint if needed
        while (this.haveToCheckpoint()) {
            this.checkpoint(request);
        }

        // update the last served request index only if needed
        if (FTManagerGen.isOCEnable) {
            MessageInfo mi = request.getMessageInfo();
            if (mi != null) {
                long requestIndex = ((MessageInfoGen) (mi)).positionInHistory;
                if (this.lastServedRequestIndex.getValue() < requestIndex) {
                    this.lastServedRequestIndex.setValue(requestIndex);
                }
            }
        }
        return 0;
    }

    @Override
    public int onServeRequestAfter(Request request) {
        return 0;
    }

    @Override
    public void updateLocationAtServer(UniqueID ownerID, UniversalBody remoteBodyAdapter) {
        // update servers
        this.location.updateLocation(ownerID, remoteBodyAdapter);
        //            this.recovery.updateState(ownerID, RecoveryProcess.RUNNING);
    }

    /*
     * search for an awaited request from r.source.
     * if any, unfreeze ar and remove it from awaitedRequests list.
     * WARNING : this.awaitedRequests must be ordered. Do not use a map !
     */
    private boolean updateAwaitedRequests(Request r) {
        AwaitedRequest ar = null;
        for (AwaitedRequest arq : this.awaitedRequests) {
            if (arq.getAwaitedSender().equals(r.getSourceBodyID())) {
                ar = arq;
                break;
            }
        }
        if (ar != null) {
            //System.err.println(""+ this.ownerID + " Request is updated by " + r.getSourceBodyID());
            ar.setAwaitedRequest(r);
            this.awaitedRequests.remove(ar);
            return true;
        } else {
            return false;
        }
    }

    /*
     * return true if this ao have to checkpoint
     */
    protected boolean haveToCheckpoint() {
        int currentCheckpointIndex = this.checkpointIndex;
        int currentNextMax = this.nextMax;

        if (takeNext)
            return true;

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
    protected abstract Checkpoint checkpoint(Request pendingRequest);

    /*
     * Return by result and send to server (if sendToServer) the current hisotry, from the last commited
     * index up to upTo. IndexOfCkpt is the index of the checkpoint that must be completed with this hisotry.
     * IsMinimal is true if this commit correspond to an update of a checkpoint with its minimal history
     */
    private HistoryUpdater commitHistories(int indexOfCkpt, long upTo, boolean sendToServer, boolean isMinimal) {
        synchronized (this.historyLock) {
            if (isMinimal && (this.historyIndex >= indexOfCkpt)) {
                // this minimal commit has already be performed
                // by a message reception : nothing to do
                return null;
            }

            // HISTO COMMIT
            List<UniqueID> histoToCommit = this.getHistoryToCommit(this.lastCommitedIndex + 1, upTo);

            // histo to commit could be null (ckpting during histo mode)
            HistoryUpdater toSend = null;
            if (histoToCommit == null) {
                // must send an empty histo to the server to commit the new recovery line !
                toSend = new HistoryUpdater(histoToCommit, 0, 0, this.ownerID, indexOfCkpt, this.incarnation);
                this.historyIndex = this.checkpointIndex;
                // last commited does not change
            } else {
                toSend = new HistoryUpdater(histoToCommit, this.lastCommitedIndex + 1, upTo, this.ownerID,
                    indexOfCkpt, this.incarnation);
                this.historyIndex = this.checkpointIndex;
                this.lastCommitedIndex = upTo;
                // delete commited history
                this.deleteCommitedHistory(toSend.base, toSend.last);
            }

            // send to the server if asked
            if (sendToServer) {
                this.storage.commitHistory(toSend);
            }
            return toSend;
        }
    }

    /*
     * Return the current history from from up to upto
     */
    private List<UniqueID> getHistoryToCommit(long from, long upTo) {
        if (from == (upTo + 1)) {
            // Activity is still in histo mode: no commit is needed
            return null;
        } else {
            int translatedFrom = (int) (from - this.historyBaseIndex);
            int translatedUpTo = (int) (upTo - this.historyBaseIndex);
            Vector<UniqueID> toRet = new Vector<UniqueID>(translatedUpTo - translatedFrom);
            Iterator<UniqueID> itHisto = this.history.iterator();
            for (int i = 0; i < translatedFrom; i++) {
                itHisto.next();
            }
            for (int i = translatedFrom; i <= translatedUpTo; i++) {
                toRet.add(itHisto.next());
            }
            return toRet;
        }
    }

    /*
     * Delete history from from up to upto
     */
    private void deleteCommitedHistory(long from, long upTo) {
        if ((upTo < this.historyBaseIndex) || (from < this.historyBaseIndex)) {
            throw new ProtocolErrorException("Deleting from " + from + " up to " + upTo +
                " while local is from " + this.historyBaseIndex + " up to " + this.deliveredRequestsCounter);
        } else {
            if (!this.history.isEmpty()) {
                this.history.subList((int) (from - this.historyBaseIndex),
                        (int) ((upTo - this.historyBaseIndex) + 1)).clear();
                this.historyBaseIndex = upTo + 1;
            }
        }
    }

    /*
     * send logged messages before recovery
     * !!! ALL FT-STATE VARAIBLES MUST BE SET !!!
     */
    protected void sendLogs(CheckpointInfoGen ci) {
        //send replies
        //System.out.println("[CIC] Sending logged messages...");
        Vector<ReplyLog> replies = ci.replyToResend;
        Iterator<ReplyLog> itReplies = replies.iterator();
        while (itReplies.hasNext()) {
            //System.out.println( this.owner.getID() + "      SEND REPLY");
            UniversalBody destination = null;
            Reply r = null;
            ReplyLog rl = (itReplies.next());
            r = rl.getReply();
            destination = rl.getDestination();
            this.sendReply(r, destination);
        }

        //send requests
        Vector<RequestLog> requests = ci.requestToResend;
        Iterator<RequestLog> itRequests = requests.iterator();
        while (itRequests.hasNext()) {
            try {
                //System.out.println( this.owner.getID() + "      SEND REQUEST");
                UniversalBody destination = null;
                RequestLog lr = itRequests.next();
                Request loggedRequest = lr.getRequest();
                destination = lr.getDestination();
                // must create a new req : the sender must be this.owner
                Request r = new RequestImpl(loggedRequest.getMethodCall(), this.owner.getRemoteAdapter(),
                    loggedRequest.isOneWay(), loggedRequest.getSequenceNumber());
                this.sendRequest(r, destination);
            } catch (RenegotiateSessionException e) {
                e.printStackTrace();
            } catch (CommunicationForbiddenException e) {
                e.printStackTrace();
            }
        }
    }

    // replace request that are orphan for cic.checkpointIndex by awaitedRequest
    // and identify existing AwRq in the request queue
    protected void filterQueue(BlockingRequestQueue queue, CheckpointInfoGen cic) {
        CircularArrayList<Request> internalQueue = ((BlockingRequestQueueImpl) queue).getInternalQueue();
        ListIterator<Request> itQueue = internalQueue.listIterator();
        while (itQueue.hasNext()) {
            Request current = (itQueue.next());
            MessageInfoGen mi = (MessageInfoGen) current.getMessageInfo();
            if (mi == null) {
                // is request an awaited or a non ft ?
                if (current instanceof AwaitedRequest) {
                    // current is an awaited request that is not updated
                    this.awaitedRequests.add((AwaitedRequest) current);
                }
            } else if (mi.isOrphanFor <= cic.checkpointIndex) {
                // current is an orpahn request
                // System.out.println("" + this.ownerID + " is filtering some orphan requests ...");
                AwaitedRequest ar = new AwaitedRequest(current.getSourceBodyID());
                itQueue.set(ar);
                this.awaitedRequests.add(ar);
            }
        }
    }

    /*
     * Adapt the log size
     */
    protected void extendRequestLog(int size) {
        if (this.latestRequestLog < size) {
            //the log vector must grow
            for (int j = this.latestRequestLog + 1; j <= size; j++) {
                this.requestToResend.put(Integer.valueOf(j), new Vector<RequestLog>());
            }
            this.latestRequestLog = size;
        }
    }

    /*
     * Adapt the log size
     */
    protected void extendReplyLog(int size) {
        if (this.latestReplyLog < size) {
            //the log vector must grow
            for (int j = this.latestReplyLog + 1; j <= size; j++) {
                this.replyToResend.put(Integer.valueOf(j), new Vector<ReplyLog>());
            }
            this.latestReplyLog = size;
        }
    }

    @Override
    public String toString() {
        String ret = " Incarnation = ";
        ret += this.incarnation;
        return ret;
    }

    /*
     * Return the memory actually used
     * For debugging stuff.
     */

    //private long getUsedMem() {
    //    return (FTManagerCIC.runtime.totalMemory() -
    //    FTManagerCIC.runtime.freeMemory()) / 1024;
    //}
    //////////////////////////////////////////////////////////////////////////////////
    ///////// HANDLING EVENTS ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Double Dispatch pattern
    @Override
    public Object handleFTMessage(FTMessage fte) {
        return fte.handleFTMessage(this);
    }

    /**
     * Commiting history because a new recovery line has occured
     * @param fte Message that contains the last complete global state
     * @return the current history
     */
    public HistoryUpdater handlingGSCEEvent(GlobalStateCompletion fte) {
        // we commit the ENTIRE history, upto the last element
        // the hisotry is NOT sent by this.commitHitories
        HistoryUpdater rh = this.commitHistories(this.checkpointIndex, this.deliveredRequestsCounter, false,
                true);

        // this commit close the completingCheckpoint state, indeed the
        // minimal history for the latest checkpoint has been sent.
        if (this.completingCheckpoint) {
            this.completingCheckpoint = false;
        }
        return rh;
    }

    /**
     * Commit history from fte.firstIndex up to the index fte.lastIndex because of an
     * output commit.
     * @param fte
     * @return the history from the base up to fte.lastIndex
     */
    public HistoryUpdater handlingOCEvent(OutputCommit fte) {
        // commit history up to upTo
        long upTo = fte.getLastIndexToRetreive();

        // this hisotry must be attached to the current checkpoint, except if the current
        // checkpoint is not completed with its minimal history
        int attachedIndex = (this.completingCheckpoint) ? (this.checkpointIndex - 1) : (this.checkpointIndex);
        HistoryUpdater toSend = this.commitHistories(attachedIndex, upTo, false, false);
        return toSend;
    }
}
