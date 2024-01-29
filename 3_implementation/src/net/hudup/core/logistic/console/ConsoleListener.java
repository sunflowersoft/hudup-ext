/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.console;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents a listener for console.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ConsoleListener extends EventListener, Remote {

	
	/**
	 * Receiving message event.
	 * @param evt message event.
	 * @throws RemoteException if any error raises.
	 */
	void receiveMessage(ConsoleEvent evt) throws RemoteException;
	
	
}
