/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.Exportable;

/**
 * This interface represents remote object built in application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface AppRemote extends Remote, Cloneable, Serializable, Exportable {

	
	@Override
	Remote export(int serverPort) throws RemoteException;

	
	@Override
	void unexport() throws RemoteException;

	
	@Override
	void forceUnexport() throws RemoteException;

	
	@Override
	Remote getExportedStub() throws RemoteException;

	
}
