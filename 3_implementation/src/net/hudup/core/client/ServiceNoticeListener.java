/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents service notice listener.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ServiceNoticeListener extends EventListener, Remote, Serializable {

	
	/**
	 * This method notify a service notice event.
	 * @param evt service notice event.
	 * @throws RemoteException if any error raises.
	 */
	void notify(ServiceNoticeEvent evt) throws RemoteException;
	
	
}
