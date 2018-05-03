package net.hudup.core.evaluate;

import java.util.EventListener;


/**
 * This interface represents a listener that monitors the evaluation progress.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface EvaluatorProgressListener extends EventListener {
	
	
	/**
	 * The method is responsible for responding an event of evaluation progress.
	 * @param evt event of evaluation progress.
	 */
	void receivedProgress(EvaluatorProgressEvent evt);
	
	
}
