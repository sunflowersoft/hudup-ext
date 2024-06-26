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

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
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
public class RecommenderRemoteWrapper extends AlgRemoteWrapper implements Recommender, RecommenderRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with remote recommender.
	 * @param remoteRecommender remote recommender.
	 */
	public RecommenderRemoteWrapper(RecommenderRemote remoteRecommender) {
		super(remoteRecommender);
	}

	
	/**
	 * Constructor with remote recommender and exclusive mode.
	 * @param remoteRecommender remote recommender.
	 * @param exclusive exclusive mode.
	 */
	public RecommenderRemoteWrapper(RecommenderRemote remoteRecommender, boolean exclusive) {
		super(remoteRecommender, exclusive);
	}

	
	@Override
	public void setup(Dataset dataset, Object... params) throws RemoteException {
		((RecommenderRemote)remoteAlg).setup(dataset, params);
	}

	
	@Override
	public void unsetup() throws RemoteException {
		((RecommenderRemote)remoteAlg).unsetup();
	}

	
	@Override
	public RecommendFilterList getFilterList() throws RemoteException {
		return ((RecommenderRemote)remoteAlg).getFilterList();
	}

	
	@Override
	public Dataset getDataset() throws RemoteException {
		if (remoteAlg instanceof Recommender)
			return ((Recommender)remoteAlg).getDataset();
		else {
			LogUtil.error("getDataset() not supported");
			return null;
		}
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return ((RecommenderRemote)remoteAlg).estimate(param, queryIds);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		return ((RecommenderRemote)remoteAlg).recommend(param, maxRecommend);
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exclusive && remoteAlg != null) {
			((RecommenderRemote)remoteAlg).unsetup();
		}

		super.unexport();
	}

	
	/**
	 * Getting minimum rating.
	 * @return minimum rating.
	 */
	public double getMinRating() {
		double minRating = getConfig().getMinRating();
		if (!Util.isUsed(minRating)) {
			try {
				minRating = getDataset().getConfig().getMinRating();
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		return minRating; 
	}

	
	/**
	 * Getting maximum rating.
	 * @return maximum rating.
	 */
	public double getMaxRating() {
		double maxRating = getConfig().getMaxRating();
		if (!Util.isUsed(maxRating)) {
			try {
				maxRating = getDataset().getConfig().getMaxRating();
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		return maxRating; 
	}
	
	
	/**
	 * Getting relevant rating threshold.
	 * @return relevant rating threshold.
	 */
	public double getRelevantRatingThreshold() {
		return (getMaxRating() + getMinRating()) / 2.0; 
	}

	
	/**
	 * Checking whether minimum rating and maximum rating are bounded.
	 * @return whether minimum rating and maximum rating are bounded.
	 */
	public boolean isBoundedMinMaxRating() {
		return getConfig().getAsBoolean(RecommenderAbstract.MINMAX_RATING_BOUND)
				&& Util.isUsed(getMinRating()) && Util.isUsed(getMaxRating()); 
	}

	
	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getDialogForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {RecommenderRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		if (remoteAlg instanceof Recommender)
			return ((Recommender)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote recommendation algorithm does not support createDefaultConfig()");
			return null;
		}
	}


}
