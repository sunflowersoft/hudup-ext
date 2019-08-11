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
	 * @throws RemoteException if any error raises.
	 */
	void remoteStart(Serializable...parameters) throws RemoteException;

	
	/**
	 * Runner pauses.
	 * @throws RemoteException if any error raises.
	 */
	void remotePause() throws RemoteException;

	
	/**
	 * Runner resumes
	 * @throws RemoteException if any error raises.
	 */
	void remoteResume() throws RemoteException;

	
	/**
	 * Runner stops.
	 * @throws RemoteException if any error raises.
	 */
	void remoteStop() throws RemoteException;
	
	
}
