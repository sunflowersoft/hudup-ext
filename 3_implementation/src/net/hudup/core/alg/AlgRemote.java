/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Exportable;

/**
 * This interface represents a remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface AlgRemote extends Remote, SetupAlgListener, Exportable, Serializable {

	
	/**
	 * Getting name of this inspectable object.
	 * @return name of this inspectable object.
	 * @throws RemoteException if any error raises
	 */
	String queryName() throws RemoteException;
	
	
	/**
	 * Getting configuration of this inspectable object.
	 * @return configuration of this inspectable object.
	 * @throws RemoteException if any error raises
	 */
	DataConfig queryConfig() throws RemoteException;
	
	
	/**
	 * Getting description of this inspectable object.
	 * @return text form of this inspectable object.
	 * @throws RemoteException if any error raises.
	 */
	String getDescription() throws RemoteException;

	
	/**
	 * Add the specified setting up listener to the end of listener list.
	 * This is remote method.
	 * @param listener specified setting up listener
	 * @throws RemoteException if any error raises.
	 */
	void addSetupListener(SetupAlgListener listener) throws RemoteException;

	
	/**
	 * Remove the specified setting up listener from the listener list.
	 * This is remote method.
	 * @param listener specified setting up listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeSetupListener(SetupAlgListener listener) throws RemoteException;


    /**
     * Firing (issuing) an event from this algorithm to all listeners. 
     * @param evt event from this algorithm.
	 * @throws RemoteException if any error raises.
     */
	void fireSetupEvent(SetupAlgEvent evt) throws RemoteException;

	
}
