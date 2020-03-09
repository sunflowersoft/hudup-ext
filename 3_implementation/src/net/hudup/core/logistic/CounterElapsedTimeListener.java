/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents a listener that monitors the counter.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface CounterElapsedTimeListener extends EventListener, Remote {

	
	/**
	 * The method is responsible for responding an event of counter.
	 * @param evt event of counter.
	 * @throws RemoteException if any error raises.
	 */
	void receivedElapsedTime(CounterElapsedTimeEvent evt) throws RemoteException;


}
