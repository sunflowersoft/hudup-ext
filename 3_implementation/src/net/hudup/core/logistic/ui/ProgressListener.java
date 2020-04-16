/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface establishes a protocol to observe a given process. As a convention, this interface and any its implementations are called {@code progress listener} or {@code progress observer}.
 * For example, there is an application {@code A} whose process is to copy file.
 * In the progress of copying, user needs to see the progress bar.
 * So programmer must implement this interface as class {@code B} by defining the method {@link #receiveProgress(ProgressEvent)} for showing the progress bar of bytes transferring.
 * Moreover the application {@code A} needs to register the progress listener {@code B}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ProgressListener extends EventListener, Remote {

	
	/**
	 * The main method that programmer must implement to define some tasks.
	 * @param evt the event specified by {@link ProgressEvent}, issued by the application and then, passed to a progress listener.
	 * @throws RemoteException if any error raises.
	 */
	void receiveProgress(ProgressEvent evt) throws RemoteException;

}
