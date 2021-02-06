/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;

/**
 * This interface indicates a remote model-based algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ModelBasedAlgRemote extends ModelBasedAlgRemoteTask, AlgRemote {


	@Override
	KBase getKBase() throws RemoteException;
	
	
	@Override
	KBase newKB() throws RemoteException;
	
	
	@Override
	KBase createKBase(Dataset dataset) throws RemoteException;


}
