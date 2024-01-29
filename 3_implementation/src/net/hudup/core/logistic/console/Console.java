/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.console;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents a console.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Console extends Cloneable, Serializable, Remote {

	
	/**
	 * Starting console.
	 * @param params additional parameters.
	 * @return true if starting is successfully.
	 * @throws Exception if any error raises.
	 */
	boolean startConsole(Object...params) throws RemoteException;
	

	/**
	 * Stopping console.
	 * @param params additional parameters.
	 * @return true if starting is successfully.
	 * @throws Exception if any error raises.
	 */
	boolean stopConsole(Object...params) throws RemoteException;


	/**
	 * Testing whether console started.
	 * @return whether console started.
	 * @throws RemoteException if any error raises.
	 */
	boolean isConsoleStarted() throws RemoteException;
	
	
	/**
	 * Getting content.
	 * @return entire content.
	 * @throws RemoteException if any error raises.
	 */
	String getContent() throws RemoteException;
	
	
    /**
	 * Add the specified listener to the listener list.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
	void addListener(ConsoleListener listener) throws RemoteException;
	
	
	/**
	 * Remove the specified listener from the listener list.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeListener(ConsoleListener listener) throws RemoteException;
	
	
}
