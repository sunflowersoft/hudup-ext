/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents remote dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface DatasetRemote extends DatasetRemoteTask2, Remote {


	/**
	 * Getting base remote interface names.
	 * @return base remote interface names.
	 * @throws RemoteException if any error raises.
	 */
	String[] getBaseRemoteInterfaceNames() throws RemoteException;


}
