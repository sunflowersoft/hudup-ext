package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;

/**
 * This interface represents a remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface AlgRemote extends Remote, SetupAlgListener, Serializable {

	
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
     * Firing (issuing) an event from this EM to all listeners. 
     * @param evt event from this EM.
	 * @throws RemoteException if any error raises.
     */
	void fireSetupEvent(SetupAlgEvent evt) throws RemoteException;

	
	/**
     * Remote exporting this remote algorithm.
     * @param serverPort server port.
     * @return stub as remote algorithm. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    AlgRemote export(int serverPort) throws RemoteException;
    
    
    /**
     * Remote unexporting remote algorithm.
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;


}
