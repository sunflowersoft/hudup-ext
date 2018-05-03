package net.hudup.core.alg;

import java.util.EventListener;


/**
 * This interface represents a listener for successful setting up process.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface SetupAlgListener extends EventListener {

	
	/**
	 * The main method is to respond a setting up event from source (often evaluator).
	 * Any class that implement this interface must implement this method to define respective tasks.
	 * 
	 * @param evt setting up event from source (often evaluator).
	 */
	void receivedSetup(SetupAlgEvent evt);
	
	
}
