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
	 * Getting remote application object.
	 * @param account account name.
	 * @param password account password.
	 * @param appName application name.
	 * @return remote application object.
	 * @throws RemoteException if any error raises.
	 */
	Remote getAppRemoteObject(String account, String password, String appName) throws RemoteException;
	
	
}
