package net.hudup.core.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.app.App;
import net.hudup.core.data.PropList;

/**
 * This interface represents extra service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExtraService extends Remote, AutoCloseable {

	
	/**
	 * Open extra service.
	 * @return true if opening is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean open() throws RemoteException;
	
	
	/**
	 * Getting internal applications;
	 * @return internal applications.
	 * @throws RemoteException if any error raises.
	 */
	List<App> getApps() throws RemoteException;
	
	
	/**
	 * Update list of applications.
	 * @throws RemoteException if any error raises.
	 */
	void updateApps() throws RemoteException;
	
	
	/**
	 * Getting system properties.
	 * @return system properties.
	 * @throws RemoteException if any error raises.
	 */
	PropList getSystemProperties() throws RemoteException;

	
	/**
	 * Clearing logs.
	 * @throws RemoteException if any error raises.
	 */
	void clearLogs() throws RemoteException;
	
	
	/**
	 * Performing server tasks.
	 * @return true if performance is possible.
	 * @throws RemoteException if any error raises.
	 */
	boolean doServerTasks() throws RemoteException;
	
	
}
