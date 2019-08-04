package net.hudup.core.evaluate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;


/**
 * This interface represents a listener for evaluator.
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
	void receivedEvaluation(EvaluatorEvent evt) throws RemoteException;
	
	
}
