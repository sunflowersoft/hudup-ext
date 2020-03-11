/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface specifies that any object that implements it can start, pause, resume, and stop remotely.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface RemoteRunner extends Remote {
	
	
	/**
	 * Runner starts.
	 * @param parameters additional parameters.
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteStart(Serializable...parameters) throws RemoteException;

	
	/**
	 * Runner pauses.
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remotePause() throws RemoteException;

	
	/**
	 * Runner resumes
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteResume() throws RemoteException;

	
	/**
	 * Runner stops.
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteStop() throws RemoteException;
	
	
}
