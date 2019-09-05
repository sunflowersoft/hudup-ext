package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

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
public class RecommenderRemoteWrapper extends AlgRemoteWrapper implements Recommender {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with remote recommender.
	 * @param remoteRecommender remote recommender.
	 */
	public RecommenderRemoteWrapper(RecommenderRemote remoteRecommender) {
		// TODO Auto-generated constructor stub
		super(remoteRecommender);
	}

	
	/**
	 * Constructor with remote recommender and exclusive mode.
	 * @param remoteRecommender remote recommender.
	 * @param exclusive exclusive mode.
	 */
	public RecommenderRemoteWrapper(RecommenderRemote remoteRecommender, boolean exclusive) {
		// TODO Auto-generated constructor stub
		super(remoteRecommender, exclusive);
	}

	
	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {e.printStackTrace();}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public void setup(Dataset dataset, Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		((RecommenderRemote)remoteAlg).setup(dataset, params);
	}

	
	@Override
	public void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		((RecommenderRemote)remoteAlg).unsetup();
	}

	
	@Override
	public RecommendFilterList getFilterList() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RecommenderRemote)remoteAlg).getFilterList();
	}

	
	@Override
	public Dataset getDataset() throws RemoteException {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof Recommender)
			return ((Recommender)remoteAlg).getDataset();
		else {
			logger.error("getDataset() not supported");
			return null;
		}
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RecommenderRemote)remoteAlg).estimate(param, queryIds);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RecommenderRemote)remoteAlg).recommend(param, maxRecommend);
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof Recommender) {
			Recommender newRecommender = (Recommender) ((Recommender)remoteAlg).newInstance();
			return new RecommenderRemoteWrapper(newRecommender, exclusive);
		}
		else {
			logger.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}

	
	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteAlg != null) {
			((RecommenderRemote)remoteAlg).unsetup();
		}

		super.unexport();
	}

	
}
