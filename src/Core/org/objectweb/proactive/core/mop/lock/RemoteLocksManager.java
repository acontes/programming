package org.objectweb.proactive.core.mop.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;


/**
 * Interface for wrappers which handle locks. 
 * This Manager allows sending proxies on local locks
 * @see LockProxy
 */
public interface RemoteLocksManager {

    ////////////////////METHODS FROM LOCK ////////////////////

    void lock(int id);

    void lockInterruptibly(int id) throws InterruptedException;

    boolean tryLock(int id);

    boolean tryLock(int id, long time, TimeUnit unit) throws InterruptedException;

    void unlock(int id);

    Condition newCondition(int id);
}
