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
public interface ExecuteAsLearnAlgRemoteTask extends ExecutableAlgRemoteTask {

	
	/**
	 * Learning as execution.
	 * @param input input parameter which is often profile.
	 * @return executed result.
	 * @throws RemoteException if any error raises.
	 */
	Object learnAsExecuteStart(Object input) throws RemoteException;

	
}
