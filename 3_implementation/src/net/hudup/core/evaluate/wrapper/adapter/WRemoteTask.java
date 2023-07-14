/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper.adapter;

import java.rmi.RemoteException;

import net.hudup.core.alg.ExecuteAsLearnAlgRemoteTask;

/**
 * This interface declares methods for remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public interface WRemoteTask extends ExecuteAsLearnAlgRemoteTask {


	/**
	 * New setting up method.
	 * @throws RemoteException if any error raises.
	 */
	void setup() throws RemoteException;

	
}
