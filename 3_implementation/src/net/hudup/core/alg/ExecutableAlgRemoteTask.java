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
 * This interface declares methods for remote executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface ExecutableAlgRemoteTask extends AlgExtRemoteTask {


	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter.
	 * @return result of execution. Return null if execution is failed.
	 * @throws RemoteException if any error raises.
	 */
	Object execute(Object input) throws RemoteException;
	
	
}
