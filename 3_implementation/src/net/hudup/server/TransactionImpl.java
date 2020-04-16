/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class implements the transaction interface, please see {@link Transaction}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class TransactionImpl implements Transaction {

	
	/**
	 * Internal lock.
	 */
	protected ReentrantReadWriteLock lock = null;
	
	
	/**
	 * Default constructor.
	 */
	public TransactionImpl() {
		// TODO Auto-generated constructor stub
		lock = new ReentrantReadWriteLock();
	}

	
	/**
	 * Constructor with fair parameter.
	 * @param fair true if using the fair ordering policy. Please see {@link ReentrantReadWriteLock}.
	 */
	public TransactionImpl(boolean fair) {
		// TODO Auto-generated constructor stub
		
		lock = new ReentrantReadWriteLock(fair);
	}

	
	@Override
	public void lockRead() {
		lock.readLock().lock();
	}

	
	@Override
	public void unlockRead() {
		lock.readLock().unlock();
	}
	
	
	@Override
	public void lockWrite() {
		lock.writeLock().lock();
	}

	
	@Override
	public void unlockWrite() {
		lock.writeLock().unlock();
	}


	@Override
	public boolean isWriteLockedByCurrentThread() {
		// TODO Auto-generated method stub
		
		return lock.isWriteLockedByCurrentThread();
	}


	
	
	
}
