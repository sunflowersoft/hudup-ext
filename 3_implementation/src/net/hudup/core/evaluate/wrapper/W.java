/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * <code>W</code> is the most abstract interface for all algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface W extends Remote, Serializable, Cloneable, AutoCloseable {

	
	/**
	 * Learning the algorithm based on specified setting and mathematical expression.
	 * @param setting specified setting. It can be null.
	 * @param params additional parameters.
	 * @return target function.
	 * @throws RemoteException if any error raises.
	 */
	Object learn(WSetting setting, Object...params) throws RemoteException;

		
	/**
	 * Getting W setting.
	 * @return W setting.
	 * @throws RemoteException if any error raises.
	 */
	WSetting getSetting() throws RemoteException;
	
	
	/**
	 * Setting W setting.
	 * @param setting W setting.
	 * @throws RemoteException if any error raises.
	 */
	void setSetting(WSetting setting) throws RemoteException;
	
	
	/**
	 * Adding listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
	void addListener(WListener listener) throws RemoteException;

	
	/**
	 * Removing listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeListener(WListener listener) throws RemoteException;


	/**
	 * Getting configuration of this network.
	 * @return configuration of this network.
	 * @throws RemoteException if any error raises.
	 */
    WConfig getConfig() throws RemoteException;

	
	/**
	 * Setting configuration of this network.
	 * @param config specified configuration.
	 * @throws RemoteException if any error raises.
	 */
	void setConfig(WConfig config) throws RemoteException;
	
	
	/**
	 * Pause doing.
	 * @return true if pausing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doPause() throws RemoteException;


	/**
	 * Resume doing.
	 * @return true if resuming is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doResume() throws RemoteException;


	/**
	 * Stop doing.
	 * @return true if stopping is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doStop() throws RemoteException;

	/**
	 * Checking whether in doing mode.
	 * @return whether in doing mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoStarted() throws RemoteException;


	/**
	 * Checking whether in paused mode.
	 * @return whether in paused mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoPaused() throws RemoteException;


	/**
	 * Checking whether in running mode.
	 * @return whether in running mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoRunning() throws RemoteException;

	
	/**
     * Exporting this network.
     * @param serverPort server port. Using port 0 if not concerning registry or naming.
     * @return stub as remote object. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    Remote export(int serverPort) throws RemoteException;
    
    
    /**
     * Unexporting this network.
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;

    
	@Override
    void close() throws Exception;


}
