/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface indicates that its implemented objects can ping over network.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Pingable extends Remote {

	
	/**
	 * Ping call over network.
	 * @return true if ping successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean ping() throws RemoteException;
	
	
}
