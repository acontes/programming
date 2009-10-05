package org.objectweb.proactive.core.mop.lock;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class AbstractRemoteLocksManager implements RemoteLocksManager{
	
	
	// exported locks
	// [lock's hashcode => lock] this allows O(1) bidirectional access to the table
	// no collision should occurs as Lock.hashcode() is not overridden 
	private Hashtable<Integer, Lock> locks;
	
	public void lock(int id) {
		this.locks.get(id).lock();
	}

	public void lockInterruptibly(int id) throws InterruptedException {
		this.locks.get(id).lockInterruptibly();
	}

	public Condition newCondition(int id) {
		return this.locks.get(id).newCondition();
	}

	public boolean tryLock(int id) {
		return this.locks.get(id).tryLock();
	}

	public boolean tryLock(int id, long time, TimeUnit unit)
			throws InterruptedException {
		return this.locks.get(id).tryLock(time, unit);
	}

	public void unlock(int id) {
		this.locks.get(id).unlock();
	}
	
}
