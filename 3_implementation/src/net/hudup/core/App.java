/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.client.ConnectInfo;

/**
 * This interface represents an application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface App extends Remote, Serializable {

	
	/**
	 * Getting application name.
	 * @return application name
	 * @throws RemoteException if any error raises.
	 */
	String getName() throws RemoteException;
	
	
	/**
	 * Getting application description.
	 * @return application description.
	 * @throws RemoteException if any error raises.
	 */
	String getDesc() throws RemoteException;

	
	/**
	 * Discard this application.
	 * @return true if closing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean discard() throws RemoteException;
	
	
	/**
	 * Server task of this application.
	 * @return true if closing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean serverTask() throws RemoteException;
	
	
	/**
	 * Show this application.
	 * @param connectInfo connection information.
	 * @throws RemoteException if any error raises.
	 */
	void show(ConnectInfo connectInfo) throws RemoteException;
	
	
	/**
	 * Getting remote object.
	 * @return remote object.
	 * @throws RemoteException if any error raises.
	 */
	Remote getRemoteObject() throws RemoteException;
	
	
}
