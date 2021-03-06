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
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.ServerPointer;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;

/**
 * This interface indicates an socket service considered as a recommendation algorithm.
 * This class is deprecated because it is similar to {@link RmiRecommender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SocketRecommender extends ServiceRecommenderAbstract implements SocketAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Socket connection as a recommendation algorithm.
	 */
	protected SocketConnection connection = null;
	
	
	/**
	 * Default constructor.
	 */
	public SocketRecommender() {
		super();
	}


	@Override
	public DataConfig createDefaultConfig() {
		return new DataConfig();
	}

	
	@Override
	public String getName() {
		return "socket_server_query";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Recommendation algorithm by calling socket service";
	}


	@Override
	public Dataset getDataset() throws RemoteException {
		if (connection == null)
			return null;
		else
			return connection.getSnapshot();
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		unsetup();
		
		if (!(dataset instanceof ServerPointer))
			throw new RuntimeException("Invalid parameter");
		
		xURI serverUri = dataset.getConfig().getStoreUri();
		String username = dataset.getConfig().getStoreAccount();
		HiddenText password = dataset.getConfig().getStorePassword();
		
		connection = ClientUtil.getSocketConnection(serverUri, username, password.getText()); 
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		super.unsetup();
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		
		if (connection == null)
			return null;
		else {
			try {
				return connection.estimate(param, queryIds);
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
				return null;
			}
		}
	}

	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		
		if (connection == null)
			return null;
		else {
			try {
				return connection.recommend(param, maxRecommend);
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
				return null;
			}
		}
	}

	
}
