/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * The class is a wrapper of remote external query.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@BaseClass //This class is not base class but the base class annotation prevents the wrapper to be registered in plug-in storage.
public class ExternalQueryRemoteWrapper extends AlgRemoteWrapper implements ExternalQuery, ExternalQueryRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with remote external query.
	 * @param remoteExternalQuery remote external query.
	 */
	public ExternalQueryRemoteWrapper(ExternalQueryRemote remoteExternalQuery) {
		super(remoteExternalQuery);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with remote external query and exclusive mode.
	 * @param remoteExternalQuery remote external query.
	 * @param exclusive exclusive mode.
	 */
	public ExternalQueryRemoteWrapper(ExternalQueryRemote remoteExternalQuery, boolean exclusive) {
		super(remoteExternalQuery, exclusive);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean setup(DataConfig internalConfig, ExternalConfig externalConfig) throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExternalQueryRemote)remoteAlg).setup(internalConfig, externalConfig);
	}

	
	@Override
	public ExternalItemInfo getItemInfo(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExternalQueryRemote)remoteAlg).getItemInfo(itemId);
	}

	
	@Override
	public ExternalUserInfo getUserInfo(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExternalQueryRemote)remoteAlg).getUserInfo(userId);
	}

	
	@Override
	public void importData(ProgressListener registeredListener) throws RemoteException {
		// TODO Auto-generated method stub
		((ExternalQueryRemote)remoteAlg).importData(registeredListener);
	}

	
	@Override
	public synchronized void resetConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof ExternalQueryAbstract)
			((ExternalQueryAbstract)remoteAlg).resetConfig();
		else
			LogUtil.info("External query remote wrapper does not support method resetConfig()");
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof ExternalQueryAbstract)
			return ((ExternalQueryAbstract)remoteAlg).createDefaultConfig();
		else {
			LogUtil.info("External query wrapper does not support method createDefaultConfig()");
			return null;
		}
	}

	
	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteAlg != null) {
			try {
				((ExternalQueryRemote)remoteAlg).close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}

		super.unexport();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof ExternalQueryAbstract) {
			ExternalQueryAbstract newExternalQuery = (ExternalQueryAbstract) ((ExternalQueryAbstract)remoteAlg).newInstance();
			return new ExternalQueryRemoteWrapper(newExternalQuery, exclusive);
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			unexport();
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {ExternalQueryRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		} catch (Throwable e) {LogUtil.trace(e);}
	}


}
