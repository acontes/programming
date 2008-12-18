package org.objectweb.proactive.extensions.masterworker.interfaces.internal;

import java.util.Collection;
import java.util.Map;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


/**
 * WorkerPeer is used for workers to generate new submaster
 * It is mainly used for elect algorithm
 */
public interface WorkerPeer extends WorkerDeadListener{

    /**
     * Return whether it can be SubMaster or not
     */
    public abstract BooleanWrapper canBeSubMaster();

    /**
     * The SubMaster informs a new peer join the group.
     * The worker peer adds a new peer to the worker peer list
     */
    public abstract BooleanWrapper addWorkerPeer(long peerid, String workername, WorkerPeer workerpeer);

    /**
     * The worker peer inform the all the workers that he is the new SubMaster
     * When a new submaster is generated, he send the peerlist to all the workers
     * The workers update the peerlist of the peer
     */
    public abstract BooleanWrapper iAmSubmaster(WorkerMaster submaster, final String subMasterName, final Map<Long, String> workernamelist,
            final Map<Long, WorkerPeer> workerpeerlist);
    
    /**
     * Used for the peer who detect the failure of the subMaster to broadcast the message to the other peers
     */
    public abstract BooleanWrapper isDead();
}