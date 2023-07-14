/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents listener for an algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface WListener extends EventListener, Remote, Serializable, Cloneable {

	
	/**
	 * Receiving information event.
	 * @param evt information event.
	 * @throws RemoteException if any error raises.
	 */
	void receivedInfo(WInfoEvent evt) throws RemoteException;
	
	
	/**
	 * Receiving learning event.
	 * @param evt learning event.
	 * @throws RemoteException if any error raises.
	 */
	void receivedDo(WDoEvent evt) throws RemoteException;


}
