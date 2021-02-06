/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

/**
 * This interface declares methods for remote executing-learning algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface ExecuteAsLearnAlgRemoteTask extends AlgExtRemoteTask {


	/**
	 * Executing this algorithm by input parameter. The execution process is the same to the learning process.
	 * @param input specified input parameter.
	 * @return result of execution. Return null if execution is failed.
	 * @throws RemoteException if any error raises.
	 */
	Object executeAsLearn(Object input) throws RemoteException;
	
	
}
