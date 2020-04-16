/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents a listener for evaluator.
 * Please distinguish evaluation listener {@link EvaluateListener} and evaluator listener {@link EvaluatorListener}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface EvaluatorListener extends EventListener, Remote {

	
	/**
	 * The main method is to respond a event from evaluator.
	 * Any class that implement this interface must implement this method to define respective tasks.
	 * 
	 * @param evt event from an evaluator.
	 * @throws RemoteException if any error raises
	 */
	void receivedEvaluator(EvaluatorEvent evt) throws RemoteException;


}
