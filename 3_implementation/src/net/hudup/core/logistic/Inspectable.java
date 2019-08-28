package net.hudup.core.logistic;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;

/**
 * This interface represents an inspectable object which can be inspected via its {@link #getInspector()} method.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Inspectable extends Remote, Serializable {

	
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
	 * Getting the inspector to inspect the object. This is not remote method.
	 */
	Inspector getInspector();

	
}
