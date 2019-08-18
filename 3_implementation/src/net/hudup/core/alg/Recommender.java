package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;


/**
 * This abstract class represents all recommendation algorithms.
 * Its two typical inherited classes are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are abstract classes for memory-based recommendation algorithm and model-based recommendation algorithm.
 * All completed recommendation algorithms must inherit directly this {@link Recommender} or derived indirectly from MemoryBasedRecomender class and ModelBasedRecommender class.
 * These completed recommendation algorithms must implement two main abstract methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} and two other abstract methods such as {@link #setup(Dataset, Object...)} and {@link #getDataset()}.
 * As a convention, this class and all classes extending it are called {@code recommender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class Recommender extends AbstractAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The filter list contains of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Concretely, two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} require filtering tasks specified by filters of this list.
	 * Suppose every item has types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 */
	protected RecommendFilterList filterList = new RecommendFilterList();
	
	
	/**
	 * Default constructor.
	 */
	public Recommender() {
		super();
	}

	
	/**
	 * Setting up this recommender based on specified dataset and additional parameters
	 * 
	 * @param dataset dataset is the main parameter to setup this recommender.
	 * @param params Additional parameters to set up this recommender. This parameter is really an array of sub-parameters.
	 * @exception RemoteException if any error occurs.
	 */
	public abstract void setup(Dataset dataset, Serializable... params) throws RemoteException;
	
	
	/**
	 * Unset this recommender. After this method is called, this recommender cannot be used unless the method {@link #setup(Dataset, Object...)} is called again.
	 * @throws RemoteException if any error raises.
	 */
	public void unsetup() throws RemoteException {
		filterList.clear();
		Dataset dataset = getDataset();
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
	}

	
	/**
	 * Getting a list of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} will call this method before estimation task and recommendation task.
	 * Suppose every item have types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 * @return list of filters.
	 */
	public RecommendFilterList getFilterList() {
		return filterList;
	}
	
	
	/**
	 * Any recommender has always a reference to a dataset represented by {@link Dataset} class because recommender is executed on the dataset.
	 * Some recommender (s) store internal dataset but others only have references to dataset.
	 * Anyway, such reference always exists.
	 * @return reference to dataset.
	 */
	public abstract Dataset getDataset();
	
	
	/**
	 * This method is very important, which is used to estimate rating values of given items (users). Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and a set of item (user) identifiers.
	 * The output result is a set of predictive or estimated rating values of items (users) specified by the second input parameter.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	public abstract RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException;

	
	/**
	 * This method is the most important one in recommendation task. Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and the maximum recommended items (users) in the returned rating vector.
	 * The output result is a list of recommended items (users) which is provided to the user (item).
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param maxRecommend the maximum recommended items (users) in the returned rating vector.
	 * @return list of recommended items (users) which is provided to the user (item), represented by {@link RatingVector} class. The number of items (users) of such list is specified by the the maximum number. Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	public abstract RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException;

	
	/**
	 * Pre-processing the specified recommendation parameter.
	 * For example, if this recommendation parameter only has user identifier (user ID) but it has no ratings then, this method fills in ratings by reading such ratings from framework database.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter. Return null if cannot pre-processing.
	 * @return new recommendation parameter that is processed from the specified recommendation parameter.
	 */
	protected RecommendParam recommendPreprocess(RecommendParam param) {
		if (param == null || param.ratingVector == null)
			return null;
		
		// Pay attention following lines
		Dataset dataset = getDataset();
		int userId = param.ratingVector.id();
		RatingVector vRating = dataset.getUserRating(userId);
		if (param.ratingVector.size() == 0) {
			if (vRating == null || vRating.size() == 0)
				return null;
			else
				param.ratingVector = vRating;
		}
		else if (vRating != null) {//Fixing date: 2019.07.13 by Loc Nguyen
			Set<Integer> itemIds = vRating.fieldIds(true);
			if (itemIds.size() > 0)
				param.ratingVector = (RatingVector)param.ratingVector.clone();
			for (int itemId : itemIds) {
				if (!param.ratingVector.contains(itemId))
					param.ratingVector.put(itemId, vRating.get(itemId));
			}
		}
		
		if (param.profile == null) {
			Profile profile = dataset.getUserProfile(param.ratingVector.id());
			param.profile = profile;
		}
		
		return param;
	}
	
	
	/**
	 * Getting minimum rating.
	 * @return minimum rating.
	 */
	public double getMinRating() {
		double minRating = getDataset().getConfig().getMinRating();
		if (!Util.isUsed(minRating))
			minRating = getConfig().getAsReal(DataConfig.MIN_RATING_FIELD);
		return minRating; 
	}

	
	/**
	 * Getting maximum rating.
	 * @return maximum rating.
	 */
	public double getMaxRating() {
		double maxRating = getDataset().getConfig().getMaxRating();
		if (!Util.isUsed(maxRating))
			maxRating = getConfig().getAsReal(DataConfig.MAX_RATING_FIELD);
		return maxRating; 
	}
	

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
