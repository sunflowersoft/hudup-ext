/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;

/**
 * This interface declares methods for remote knowledge base.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface KBaseRemoteTask2 extends KBaseRemoteTask, Remote {
	
		
	@Override
	void load() throws RemoteException;
	

	@Override
	void learn(Dataset dataset, Alg alg) throws RemoteException;
	
	
	@Override
	void save() throws RemoteException;
	
	
	@Override
	void save(DataConfig storeConfig) throws RemoteException;
	
	
	@Override
	void close() throws Exception;

	
	@Override
	void clear() throws RemoteException;
	
	
	@Override
	boolean isEmpty() throws RemoteException;


}
