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

import net.hudup.core.ExtraStorage;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.logistic.NetUtil;

/**
 * This class is a simple implementation of exportable object.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExportableObject implements Exportable, Remote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Exported stub must be serializable.
     */
    protected Remote exportedStub = null;

    
    /**
	 * Default constructor.
	 */
	public ExportableObject() {

	}

	
	@Override
	public Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null)
			exportedStub = (AlgRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	@Override
	public void unexport() throws RemoteException {
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
	
			ExtraStorage.removeUnmanagedExportedObject(this);
		}
	}

	
	@Override
	public void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}


}
