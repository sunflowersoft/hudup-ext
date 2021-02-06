/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;

/**
 * This interface represents a remote knowledge base.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface KBaseRemote extends KBaseRemoteTask, AlgRemoteTask, SetupAlgListener, Remote, Serializable {
	
	
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


	/**
	 * Getting remote base class names.
	 * @return remote base class names.
	 * @throws RemoteException if any error raises.
	 */
	String[] getBaseRemoteInterfaceNames() throws RemoteException;


}
