/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents a remote knowledge base.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface KBaseRemote extends KBaseRemoteTask2, AlgRemoteTask2, SetupAlgListener, Remote, Serializable {

	
	/**
	 * Getting remote base class names.
	 * @return remote base class names.
	 * @throws RemoteException if any error raises.
	 */
	String[] getBaseRemoteInterfaceNames() throws RemoteException;


}
