/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

/**
 * {@code Transaction} interface represents transaction layer in the architecture of {@code Recommender} module.
 * {@code Transaction} is responsible for managing concurrence data accesses. Currently, {@link DefaultServer} is responsible for implementing {@code Transaction} interface.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Transaction {

	/**
	 * Read locking.
	 */
	void lockRead();

	
	/**
	 * Read unlocking.
	 */
	void unlockRead();
	
	
	/**
	 * Write locking.
	 */
	void lockWrite();


	/**
	 * Write unlocking.
	 */
	void unlockWrite();
	
	
	/**
	 * Testing whether write locked by current thread.
	 * @return whether write locked by current thread.
	 */
    boolean isWriteLockedByCurrentThread();
	
    
    
}
