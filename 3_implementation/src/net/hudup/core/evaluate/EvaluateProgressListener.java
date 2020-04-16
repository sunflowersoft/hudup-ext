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
 * This interface represents a listener that monitors the evaluation progress.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface EvaluateProgressListener extends EventListener, Remote {
	
	
	/**
	 * The method is responsible for responding an event of evaluation progress.
	 * @param evt event of evaluation progress.
	 * @throws RemoteException if any error raises.
	 */
	void receivedProgress(EvaluateProgressEvent evt) throws RemoteException;
	
	
}
