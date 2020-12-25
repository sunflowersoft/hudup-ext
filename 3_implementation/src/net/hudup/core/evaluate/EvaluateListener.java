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

import net.hudup.core.logistic.Pingable;

/**
 * This interface represents a listener for evaluation.
 * Please distinguish evaluation listener {@link EvaluateListener} and evaluator listener {@link EvaluatorListener}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface EvaluateListener extends EventListener, Pingable, Remote {
	
	
	/**
	 * The main method is to respond a event from evaluation process.
	 * Any class that implement this interface must implement this method to define respective tasks.
	 * 
	 * @param evt event from an evaluator.
	 * @throws RemoteException if any error raises
	 */
	void receivedEvaluation(EvaluateEvent evt) throws RemoteException;


	/**
	 * Testing whether the specified class via its name is accepted.
	 * @param className specified class name.
	 * @return whether the specified class via its name is accepted.
	 * @throws RemoteException if any error raises.
	 */
	boolean classPathContains(String className) throws RemoteException;
	
	
}
