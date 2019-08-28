package net.hudup.alg;

import java.rmi.RemoteException;
import java.util.Set;

import org.apache.log4j.Logger;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendFilterList;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * The class is a wrapper of remote recommendation algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as Green Fall will has a pair: Green Fall stub (remote recommender) and Green Fall wrapper (normal recommender).
 * The server creates Green Fall stub (remote recommender) and the client creates and uses the Green Fall wrapper as normal recommender.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class RecommenderRemoteWrapper implements Recommender {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(RecommenderRemoteWrapper.class);


	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote recommendation algorithm.
	 */
	protected Recommender remoteRecommender = null;
	
	
	/**
	 * Constructor with remote recommender.
	 * @param remoteRecommender remote recommender.
	 */
	public RecommenderRemoteWrapper(Recommender remoteRecommender) {
		// TODO Auto-generated constructor stub
		this(remoteRecommender, true);
	}

	
	/**
	 * Constructor with remote recommender and exclusive mode.
	 * @param remoteRecommender remote recommender.
	 * @param exclusive exclusive mode.
	 */
	public RecommenderRemoteWrapper(Recommender remoteRecommender, boolean exclusive) {
		// TODO Auto-generated constructor stub
		this.remoteRecommender = remoteRecommender;
		this.exclusive = exclusive;
	}

	
	@Override
	public String queryName() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteRecommender.queryName();
	}

	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteRecommender.queryConfig();
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteRecommender.getDescription();
	}

	
	@Override
	public synchronized Inspector getInspector() {
		// TODO Auto-generated method stub
		return EvaluateGUI.createInspector(this);
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		try {
			return remoteRecommender.queryConfig();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		logger.error("resetConfig() not supported");
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		logger.error("createDefaultConfig() not supported");
		return null;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		try {
			return remoteRecommender.queryName();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		logger.warn("newInstance() returns itselfs and so does not return new object");
		return this;
	}

	
	@Override
	public void setup(Dataset dataset, Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		remoteRecommender.setup(dataset, params);
	}

	
	@Override
	public void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		remoteRecommender.unsetup();
	}

	
	@Override
	public RecommendFilterList getFilterList() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteRecommender.getFilterList();
	}

	
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		logger.error("getDataset() not supported");
		return null;
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteRecommender.estimate(param, queryIds);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteRecommender.recommend(param, maxRecommend);
	}


	@Override
	public void export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		logger.warn("export(int) not supported");
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteRecommender != null) {
			remoteRecommender.unsetup();
			remoteRecommender.unexport();
			remoteRecommender = null;
		}
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
