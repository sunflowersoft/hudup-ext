/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.alg.AlgAbstract;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class implements basically external query.
 * 
 * @author Loc Nguyen
 * @version 12.0
 * 
 */
public abstract class ExternalQueryAbstract extends AlgAbstract implements ExternalQuery, ExternalQueryRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExternalQueryAbstract() {

	}

	
	@Override
	public synchronized void setConfig(DataConfig config) {
		LogUtil.info("External query does not support method setConfig(DataConfig)");
	}

	
	@Override
	public synchronized void resetConfig() {
		LogUtil.info("External query does not support method resetConfig()");
	}

	
	@Override
	public void close() throws Exception {
		try {
			unexport();
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {ExternalQueryRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
}
