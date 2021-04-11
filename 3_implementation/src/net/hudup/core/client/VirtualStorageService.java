/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This is interface for storage service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface VirtualStorageService extends Remote {

	
	/**
	 * Getting root storage unit.
	 * @return root storage unit.
	 * @throws RemoteException if any error raises.
	 */
	VirtualStorageUnit getRoot() throws RemoteException;
	
	
	/**
	 * Listing sub-units of given parent unit.
	 * @param parent given parent unit.
	 * @return sub-units of given parent unit.
	 * @throws RemoteException if any error raises.
	 */
	List<VirtualStorageUnit> list(VirtualStorageUnit parent) throws RemoteException;
	
	
	/**
	 * Checking whether specified unit is store.
	 * @param unit specified unit.
	 * @return whether specified unit is store.
	 * @throws RemoteException if any error raises.
	 */
	boolean isStore(VirtualStorageUnit unit) throws RemoteException;
	
	
	/**
	 * Create store.
	 * @param store store.
	 * @return created store.
	 * @throws RemoteException if any error raises.
	 */
	VirtualStorageUnit createStore(VirtualStorageUnit store) throws RemoteException;
	
	
	/**
	 * Create file.
	 * @param file file.
	 * @return created file.
	 * @throws RemoteException if any error raises.
	 */
	VirtualStorageUnit createFile(VirtualStorageUnit file) throws RemoteException;
	
	
	/**
	 * Reading file.
	 * @param file file.
	 * @return read byte array.
	 * @throws RemoteException if any error raises.
	 */
	byte[] readFile(VirtualStorageUnit file) throws RemoteException;
	
	
	/**
	 * Writing file.
	 * @param file file.
	 * @param data data to write.
	 * @return true if writing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean writeFile(VirtualStorageUnit file, byte[] data) throws RemoteException;
	
	
	/**
	 * Rename unit.
	 * @param unit specified unit.
	 * @param newName new name.
	 * @return renamed unit.
	 * @throws RemoteException if any error raises.
	 */
	VirtualStorageUnit rename(VirtualStorageUnit unit, String newName) throws RemoteException;
	
	
	/**
	 * Copying source unit to destination unit.
	 * @param src source unit.
	 * @param dst destination unit.
	 * @return true if copying is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean copy(VirtualStorageUnit src, VirtualStorageUnit dst) throws RemoteException;
	
	
	/**
	 * Moving source unit to destination unit.
	 * @param src source unit.
	 * @param dst destination unit.
	 * @return true if copying is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean move(VirtualStorageUnit src, VirtualStorageUnit dst) throws RemoteException;

	
	/**
	 * Deleting specified unit.
	 * @param unit specified unit.
	 * @return whether deletion is success.
	 * @throws RemoteException if any error raises.
	 */
	boolean delete(VirtualStorageUnit unit) throws RemoteException;
	
	
}
