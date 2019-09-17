/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * For security, each server should implements this interface so that authenticated remote application (client) can retrieves itself and its service.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Gateway extends Remote, Serializable {
	
	
	/**
	 * Retrieving remote server with authentication of account and password.
	 * @param account authenticated account.
	 * @param password authenticated password.
	 * @return remote {@link Server} with authentication of account and password.
	 * @throws RemoteException if any error raises.
	 */
	Server getRemoteServer(String account, String password) throws RemoteException;
	
	
	/**
	 * Retrieving remote service with authentication of account and password.
	 * @param account authenticated account.
	 * @param password authenticated password.
	 * @return remote {@link Service} with authentication of account and password.
	 * @throws RemoteException if any error raises.
	 */
	Service getRemoteService(String account, String password) throws RemoteException;
	
	
}
