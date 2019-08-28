package net.hudup.core.alg;

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
public interface AlgRemote extends Remote {

	
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
	 * @throws RemoteException if any error raises
	 */
	String getDescription() throws RemoteException;

	
    /**
     * Remote exporting this remote algorithm.
     * @param serverPort server port.
     * @throws RemoteException if any error raises.
     */
    void export(int serverPort) throws RemoteException;
    
    
    /**
     * Remote unexporting remote algorithm.
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;


}
