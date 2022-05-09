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

/**
 * This interface represents extra gateway.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExtraGateway extends Remote, Serializable {

	
	/**
	 * Getting remote task object.
	 * @param account account name.
	 * @param password account password.
	 * @param taskName task name.
	 * @return remote task object.
	 * @throws RemoteException if any error raises.
	 */
	Remote getTaskRemoteObject(String account, String password, String taskName) throws RemoteException;
	
	
}
