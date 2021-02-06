/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemote;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * This interface represents a remote external query.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface ExternalQueryRemote extends ExternalQueryRemoteTask, AlgRemote {


	@Override
	boolean setup(
			DataConfig internalConfig, 
			ExternalConfig externalConfig) throws RemoteException;
	
	
	@Override
	ExternalItemInfo getItemInfo(int itemId) throws RemoteException;
	
	
	@Override
	ExternalUserInfo getUserInfo(int userId) throws RemoteException;
	
	
	@Override
	void importData(ProgressListener registeredListener) throws RemoteException;


}
