/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents a listener for application.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface AppListener extends EventListener, Remote {

	
	/**
	 * Receiving application event.
	 * @param evt application event.
	 * @throws RemoteException if any error raises.
	 */
	void receiveEvent(AppEvent evt) throws RemoteException;
	
	
}
