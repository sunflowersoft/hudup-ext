/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Map;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is wrapper of remote AUgorithm {@link AugRemote}.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@BaseClass //This class is not base class but the base class annotation prevents this class to be registered in plug-in storage.
public class AugRemoteWrapper extends ExecutableAlgRemoteWrapper implements Aug, AugRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with remote AUgorithm.
	 * @param remoteAug remote AUgorithm.
	 */
	public AugRemoteWrapper(AugRemote remoteAug) {
		super(remoteAug);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with remote AUgorithm and exclusive mode.
	 * @param remoteAug remote AUgorithm.
	 * @param exclusive exclusive mode.
	 */
	public AugRemoteWrapper(AugRemote remoteAug, boolean exclusive) {
		super(remoteAug, exclusive);
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase getKBase() throws RemoteException {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).getKBase();
		else {
			LogUtil.info("AUgorithm does not support method getKBase()");
			return null;
		}
	}


	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).newKB();
		else {
			LogUtil.info("AUgorithm does not support method newKB()");
			return null;
		}
	}


	@Override
	public KBase createKBase(Dataset dataset) throws RemoteException {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).createKBase(dataset);
		else {
			LogUtil.info("AUgorithm does not support method createKBase(Dataset)");
			return null;
		}
	}


	@Override
	public Dataset getDataset() throws RemoteException {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).getDataset();
		else {
			LogUtil.info("AUgorithm does not support method getDataset()");
			return null;
		}
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract) {
			AugAbstract newAlg = (AugAbstract) ((AugAbstract)remoteAlg).newInstance();
			return new AugRemoteWrapper(newAlg, exclusive);
		}
		else {
			LogUtil.warn("Wrapper of remote executable algorithm: newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}

	
	@Override
	public String note() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).note();
		else {
			LogUtil.info("AUgorithm does not support method note()");
			return null;
		}
	}


	@Override
	public Object cacheTask(int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).cacheTask(id1, id2, cache, task, params);
		else {
			LogUtil.info("AUgorithm does not support method cacheTask(int, int, Map, net.hudup.core.alg.SupportCacheAlg.Task, Object...)");
			return null;
		}
	}

	
	@Override
	public Object cacheTask(int id, Map<Integer, Object> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).cacheTask(id, cache, task, params);
		else {
			LogUtil.info("AUgorithm does not support method #cacheTask(int, Map, net.hudup.core.alg.SupportCacheAlg.Task, Object...)");
			return null;
		}
	}


	@Override
	public boolean isCached() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).isCached();
		else {
			LogUtil.info("AUgorithm does not support method isCached()");
			return false;
		}
	}

	
	@Override
	public void setCached(boolean cached) {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			((AugAbstract)remoteAlg).setCached(cached);
		else
			LogUtil.info("AUgorithm does not support method setCached(boolean)");
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AugAbstract)
			return ((AugAbstract)remoteAlg).createDefaultConfig();
		else {
			LogUtil.info("AUgorithm does not support method createDefaultConfig()");
			return null;
		}
	}

	
}
