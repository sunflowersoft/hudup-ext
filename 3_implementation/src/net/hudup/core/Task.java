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
 * This interface represents a task.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Task extends java.lang.Cloneable, Remote, Serializable {

	
	/**
	 * Getting task name.
	 * @return task name
	 * @throws RemoteException if any error raises.
	 */
	String getName() throws RemoteException;
	
	
	/**
	 * Getting task description.
	 * @return task description.
	 * @throws RemoteException if any error raises.
	 */
	String getDesc() throws RemoteException;

	
	/**
	 * Discard this task.
	 * @return true if closing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean discard() throws RemoteException;
	
	
	/**
	 * Server do this task.
	 * @return true if closing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean serverDo() throws RemoteException;
	
	
	/**
	 * Show this task.
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
