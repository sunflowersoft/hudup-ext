/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Exportable;

/**
 * This interface declares more methods for remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface AlgRemoteTask extends Exportable /*, AgentSupport*/ {

	
	/**
	 * Getting name of this remote algorithm.
	 * @return name of this remote algorithm.
	 * @throws RemoteException if any error raises
	 */
	String queryName() throws RemoteException;
	
	
	/**
	 * Getting configuration of this remote algorithm.
	 * @return configuration of this remote algorithm.
	 * @throws RemoteException if any error raises
	 */
	DataConfig queryConfig() throws RemoteException;
	
	
	/**
	 * Getting description of this remote object.
	 * @return text form of this remote object.
	 * @throws RemoteException if any error raises.
	 */
	String getDescription() throws RemoteException;

	
	/**
	 * Main method to learn parameters. As usual, it is often called by {@code setup} method of algorithm or {@code learn} of KBase.
	 * @param info additional parameter. It is often serialized.
	 * @return the parameter to be learned, which is often serialized. Return null if learning is failed.
	 * @exception RemoteException if any error occurs.
	 */
	Object learnStart(Object...info) throws RemoteException;

	
	/**
	 * Pause learning process.
	 * This method will be synchronized.
	 * @return true if successful.
	 * @exception RemoteException if any error occurs.
	 */
	boolean learnPause() throws RemoteException;
	
	
	/**
	 * Resume learning process resumes after paused.
	 * This method will be synchronized.
	 * @return true if successful.
	 * @exception RemoteException if any error occurs.
	 */
	boolean learnResume() throws RemoteException;
	
	
	/**
	 * Stop learning process.
	 * This method will be synchronized.
	 * @return true if successful.
	 * @exception RemoteException if any error occurs.
	 */
	boolean learnStop() throws RemoteException;

	
	/**
	 * Force to stop learning process.
	 * This method will be synchronized.
	 * @return true if successful.
	 * @exception RemoteException if any error occurs.
	 */
	boolean learnForceStop() throws RemoteException;

	
	/**
	 * Testing whether learning process started.
	 * @return whether learning process started
	 * @exception RemoteException if any error occurs.
	 */
	boolean isLearnStarted() throws RemoteException;
	
	
	/**
	 * Testing whether learning process paused. 
	 * @return whether learning process paused.
	 * @exception RemoteException if any error occurs.
	 */
	boolean isLearnPaused() throws RemoteException;
	
	
	/**
	 * Testing whether learning process is running.
	 * @return whether learning process is running.
	 * @exception RemoteException if any error occurs.
	 */
	boolean isLearnRunning() throws RemoteException;

	
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
