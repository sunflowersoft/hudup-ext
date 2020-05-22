/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.client.ClientUtil;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;

/**
 * This interface indicates an RMI service considered as an recommendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RmiRecommender extends ServiceRecommenderAbstract implements RmiAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * RMI service as recommendation algorithm. 
	 */
	protected Service service = null;
	
	
	/**
	 * Default constructor.
	 */
	public RmiRecommender() {
		super();
	}


	@Override
	public DataConfig createDefaultConfig() {
		return new DataConfig();
	}

	
	@Override
	public String getName() {
		return "rmi_server_query";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Recommendation algorithm by calling RMI service";
	}


	@Override
	public Dataset getDataset() throws RemoteException {
		if (service == null)
			return null;
		else
			return service.getSnapshot();
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		unsetup();
		
		xURI uri = dataset.getConfig().getStoreUri(); 
		service = ClientUtil.getRemoteService(
				uri.getHost(),
				uri.getPort(),
				dataset.getConfig().getStoreAccount(), 
				dataset.getConfig().getStorePassword().getText());
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		service = null;

		super.unsetup();
	}


	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		
		try {
			return service.estimate(param, queryIds);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			return null;
		}
	}

	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {

		try {
			return service.recommend(param, maxRecommend);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			return null;
		}
		
	}


	
}



