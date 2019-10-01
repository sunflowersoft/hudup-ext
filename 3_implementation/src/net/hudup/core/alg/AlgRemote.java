/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents a remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface AlgRemote extends AlgRemoteTask, SetupAlgListener, Remote, Serializable {


	/**
	 * Getting base remote interface names.
	 * @return base remote interface names.
	 * @throws RemoteException if any error raises.
	 */
	String[] getBaseRemoteInterfaceNames() throws RemoteException;
	

}
