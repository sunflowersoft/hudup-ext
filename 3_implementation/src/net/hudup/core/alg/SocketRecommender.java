/**
 * 
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
		// TODO Auto-generated constructor stub
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "socket_server_query";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Recommendation algorithm by calling socket service";
	}


	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		try {
			if (connection == null)
				return null;
			else
				return connection.getSnapshot();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
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
			e.printStackTrace();
		}
		
		super.unsetup();
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		
		if (connection == null)
			return null;
		else {
			try {
				return connection.estimate(param, queryIds);
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		
		if (connection == null)
			return null;
		else {
			try {
				return connection.recommend(param, maxRecommend);
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SocketRecommender();
	}


}
