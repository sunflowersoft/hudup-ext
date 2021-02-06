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
 * This interface represents an executing-learning remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExecuteAsLearnAlgRemote extends ExecuteAsLearnAlgRemoteTask, AlgExtRemote {


	@Override
	Object executeAsLearn(Object input) throws RemoteException;


}
