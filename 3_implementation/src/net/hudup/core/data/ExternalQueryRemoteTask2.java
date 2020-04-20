/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemoteTask2;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * This interface declares more methods for remote external query.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface ExternalQueryRemoteTask2 extends AlgRemoteTask2, AutoCloseable {


	/**
	 * Setting up (initializing) this external query by both internal configuration and external configuration.
	 * @param internalConfig internal configuration to describe Hudup database.
	 * @param externalConfig external configuration to describe outside database.
	 * @return whether setting up successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean setup(
			DataConfig internalConfig, 
			ExternalConfig externalConfig) throws RemoteException;
	
	
	/**
	 * Getting external item information from internal item identifier.
	 * @param itemId internal item id.
	 * @return {@link ExternalItemInfo} stored in outside database.
	 * @throws RemoteException if any error raises.
	 */
	ExternalItemInfo getItemInfo(int itemId) throws RemoteException;
	
	
	/**
	 * Getting external user information from internal user identifier.
	 * @param userId internal user id.
	 * @return {@link ExternalUserInfo} stored in outside database.
	 * @throws RemoteException if any error raises.
	 */
	ExternalUserInfo getUserInfo(int userId) throws RemoteException;
	
	
	/**
	 * Fill in Hudup database by importing data from outside (external database) database.
	 * The Hudup database (internal database) is described by internal configuration mentioned in {@link #setup(DataConfig, ExternalConfig)}.
	 * The outside database (external database) is described by external configuration mentioned in {@link #setup(DataConfig, ExternalConfig)}.
	 * @param registeredListener the registered {@link ProgressListener} to observe the importing process. This listener can do some particular tasks such as showing the progress bar so that users know the progress of importing process.
	 * @throws RemoteException if any error raises.
	 */
	void importData(ProgressListener registeredListener) throws RemoteException;


}
