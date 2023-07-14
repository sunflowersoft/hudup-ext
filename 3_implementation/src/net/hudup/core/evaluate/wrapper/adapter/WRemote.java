/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper.adapter;

import java.rmi.RemoteException;

import net.hudup.core.alg.ExecuteAsLearnAlgRemote;

/**
 * This interface represents remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface WRemote extends WRemoteTask, ExecuteAsLearnAlgRemote {


	@Override
	void setup() throws RemoteException;

	
}
