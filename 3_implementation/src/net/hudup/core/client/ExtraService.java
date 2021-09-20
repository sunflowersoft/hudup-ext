package net.hudup.core.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
	
	
}
