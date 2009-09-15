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
package org.objectweb.proactive.core.body.ft.protocols.gen.servers;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.checkpointing.CheckpointInfo;
import org.objectweb.proactive.core.body.ft.exception.NotImplementedException;
import org.objectweb.proactive.core.body.ft.internalmsg.GlobalStateCompletion;
import org.objectweb.proactive.core.body.ft.message.HistoryUpdater;
import org.objectweb.proactive.core.body.ft.servers.FTServer;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServerImpl;
import org.objectweb.proactive.core.body.ft.servers.util.ActiveQueueJob;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.util.MutableInteger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class defines a checkpoint server for the CIC protocol.
 * @author The ProActive Team
 * @since 2.2
 */
public abstract class CheckpointServerGen extends CheckpointServerImpl {

    //logger
    protected static Logger logger = ProActiveLogger.getLogger(Loggers.FAULT_TOLERANCE_CIC);

    //monitoring latest global state
    protected Hashtable<MutableInteger, MutableInteger> stateMonitor; //ckpt index -> number of stored checkpoint
    protected int lastGlobalState;
    protected int lastRegisteredCkpt;

    // current incarnation
    protected int globalIncarnation;

    // monitoring recovery line
    protected Hashtable<UniqueID, MutableInteger> greatestCommitedHistory; // ids <-> index of the greatest commited histo
    protected Hashtable<MutableInteger, MutableInteger> recoveryLineMonitor; // ckpt index <-> number of completed checkpoints
    protected int recoveryLine;

    // profiling
    protected boolean displayCkptSize;

    public CheckpointServerGen(FTServer server) {
        super(server);

        this.stateMonitor = new Hashtable<MutableInteger, MutableInteger>();
        this.lastGlobalState = 0;
        this.greatestCommitedHistory = new Hashtable<UniqueID, MutableInteger>();
        this.recoveryLineMonitor = new Hashtable<MutableInteger, MutableInteger>();
        this.recoveryLine = 0;
        this.lastRegisteredCkpt = 0;
        this.globalIncarnation = 1;

        this.displayCkptSize = false; // debugging stuff
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#getCheckpoint(org.objectweb.proactive.core.UniqueID, int)
     */
    public Checkpoint getCheckpoint(UniqueID id, int sequenceNumber) {
        // TODO : checkpoints with multiple index ??
        return checkpointStorage.get(id).get(sequenceNumber);
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#getLastCheckpoint(org.objectweb.proactive.core.UniqueID)
     */
    public Checkpoint getLastCheckpoint(UniqueID id) {
        List<Checkpoint> checkpoints = checkpointStorage.get(id);
        int size = checkpoints.size();
        return (checkpoints.get(size - 1));
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#getLastState(org.objectweb.proactive.core.UniqueID)
     */
    public int getLastState(UniqueID id) {
        List<Checkpoint> checkpoints = checkpointStorage.get(id);
        int size = checkpoints.size();
        return size;
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#getLastGlobalState()
     */
    public int getLastGlobalState() {
        return lastGlobalState;
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#addInfoToCheckpoint(org.objectweb.proactive.core.body.ft.checkpointing.CheckpointInfo, org.objectweb.proactive.core.UniqueID, int, int)
     */
    public synchronized void addInfoToCheckpoint(CheckpointInfo ci, UniqueID id, int sequenceNumber,
            int incarnation) {
    }

    /**
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#getInfoFromCheckpoint(org.objectweb.proactive.core.UniqueID, int)
     */
    public CheckpointInfo getInfoFromCheckpoint(UniqueID id, int sequenceNumber) {
        throw new NotImplementedException();
    }

    /**
     * Not implemented for the CIC protocol
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#storeRequest(org.objectweb.proactive.core.UniqueID, org.objectweb.proactive.core.body.request.Request)
     */
    public void storeRequest(UniqueID receiverId, Request request) {
        throw new NotImplementedException();
    }

    /**
     * Not implemented for the CIC protocol
     * @see org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer#storeReply(org.objectweb.proactive.core.UniqueID, org.objectweb.proactive.core.body.reply.Reply)
     */
    public void storeReply(UniqueID receiverID, Reply reply) {
        throw new NotImplementedException();
    }

    // return true if a new globalState is found
    protected boolean checkLastGlobalState() {
        //logger.info("[CKPT] Checking last global state...");
        int systemSize = this.server.getSystemSize();
        int lastGB = this.lastGlobalState;
        int lastCkpt = this.lastRegisteredCkpt;
        MutableInteger mi = new MutableInteger(lastCkpt);
        for (int i = lastCkpt; i > lastGB; i--, mi.add(-1)) {
            int numRegistered = ((this.stateMonitor.get(mi))).getValue();
            if (numRegistered == systemSize) {
                this.lastGlobalState = i;
                return true;
            }
        }
        return false;
    }

    // protected accessors
    protected abstract void internalRecover(UniqueID failed);

    /**
     * Reintialize the server.
     */
    @Override
    public void initialize() {
        super.initialize();
        this.stateMonitor = new Hashtable<MutableInteger, MutableInteger>();
        this.lastGlobalState = 0;
        this.greatestCommitedHistory = new Hashtable<UniqueID, MutableInteger>();
        this.recoveryLineMonitor = new Hashtable<MutableInteger, MutableInteger>();
        this.recoveryLine = 0;
        this.lastRegisteredCkpt = 0;
        this.globalIncarnation = 1;
    }

    /*
     * This class define a job for sending a global state completion notification.
     */
    public static class GSCESender implements ActiveQueueJob {
        private FTServer server;
        private UniqueID callee;
        private GlobalStateCompletion toSend;

        public GSCESender(FTServer s, UniqueID c, GlobalStateCompletion ts) {
            this.server = s;
            this.callee = c;
            this.toSend = ts;
        }

        public void doTheJob() {
            try {
                UniversalBody destination = server.getLocation(callee);

                // THIS CALL MUST BE FT !!!!
                HistoryUpdater histo = (HistoryUpdater) (destination.receiveFTMessage(toSend));

                // histo could be null : nothing to commit
                if (histo != null) {
                    server.commitHistory(histo);
                }
            } catch (IOException e) {
                server.forceDetection();
            }
        }
    }
}
