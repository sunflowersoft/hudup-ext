/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * This interface represent dataset pools service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface DatasetPoolsService extends Remote {

	
	/**
	 * Getting names of pools.
	 * @return names of pools.
	 * @throws RemoteException if any error raises.
	 */
	Set<String> names() throws RemoteException;
	
	
	/**
	 * Checking if containing the specified dataset pool given name.
	 * @param name given name.
	 * @return if containing the specified dataset pool given name.
	 * @throws RemoteException if any error raises.
	 */
	boolean contains(String name) throws RemoteException;
	
	
	/**
	 * Getting dataset pool.
	 * @param name dataset pool name.
	 * @return dataset pool.
	 * @throws RemoteException if any error raises.
	 */
	DatasetPoolExchanged get(String name) throws RemoteException;
	
	
	/**
	 * Putting dataset pool given name.
	 * @param name dataset pool.
	 * @param pool given name.
	 * @return true if putting is successful.
	 * @throws RemoteException  if any error raises.
	 */
	boolean put(String name, DatasetPoolExchanged pool) throws RemoteException;
	
	
	/**
	 * Removing dataset pool.
	 * @param name dataset pool name.
	 * @return true if removing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remove(String name) throws RemoteException;
	
	
	/**
	 * Clearing pool.
	 * @throws RemoteException if any error raises.
	 */
	void clear() throws RemoteException;
	
	
}
