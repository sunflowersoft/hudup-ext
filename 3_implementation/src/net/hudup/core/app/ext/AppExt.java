/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import java.rmi.RemoteException;

import net.hudup.core.app.App;

/**
 * This interface represents extensive application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface AppExt extends App {

	
    /**
	 * Add the specified listener to the listener list.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
	void addListener(AppListener listener) throws RemoteException;
	
	
	/**
	 * Remove the specified listener from the listener list.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeListener(AppListener listener) throws RemoteException;


}
