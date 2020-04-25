/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

import net.hudup.core.logistic.Pingable;

/**
 * This interface represents a listener for successful setting up process.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface SetupAlgListener extends EventListener, Pingable, Remote {

	
	/**
	 * The main method is to respond a setting up event issued by algorithm. As usual, evaluator receives this event and then passes it to GUI.
	 * In other words, evaluator often implements this method but any class can implement this method.
	 * In general, any class that implement this interface must implement this method to define respective tasks.
	 * @param evt setting up event issued by algorithm.
	 * @throws RemoteException if any error raises.
	 */
	void receivedSetup(SetupAlgEvent evt) throws RemoteException;
	
	
}
