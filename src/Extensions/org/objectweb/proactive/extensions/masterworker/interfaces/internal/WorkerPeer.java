package org.objectweb.proactive.extensions.masterworker.interfaces.internal;

import java.util.Collection;
import java.util.Map;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


/**
 * WorkerPeer is used for workers to generate new submaster
 * It is mainly used for elect algorithm
 */
public interface WorkerPeer extends WorkerDeadListener {

    /**
     * Return whether the WorkerPeer called is alive
     * must be synchronous
     */
    public abstract boolean areYouAlive(final Long peerId, final String peerName);

    /**
     * The SubMaster informs a new peer join the group.
     * each time the whole peer list is updated
     */
    public abstract BooleanWrapper updateWorkerPeerList(long workerNameCounter,
            Map<Long, WorkerPeer> workerPeerList, Map<Long, String> workerNameSet);

    /**
     * The worker peer inform the all the workers that he is the new SubMaster
     * When a new submaster is generated, he send the peerlist to all the workers
     * The workers update the peerlist of the peer
     */
    public abstract BooleanWrapper iAmSubMaster(WorkerMaster submaster, final String subMasterName,
            final Map<Long, String> workernamelist, final Map<Long, WorkerPeer> workerpeerlist);

}
