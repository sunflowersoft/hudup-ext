package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;

/**
 * This interface represents a remote recomendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface RecommenderRemote extends AlgRemote {


	/**
	 * Setting up this recommender based on specified dataset and additional parameters
	 * 
	 * @param dataset dataset is the main parameter to setup this recommender.
	 * @param params Additional parameters to set up this recommender. This parameter is really an array of sub-parameters.
	 * Its elements should be serializable for RMI.
	 * @exception RemoteException if any error occurs.
	 */
	void setup(Dataset dataset, Object...params) throws RemoteException;
	
	
	/**
	 * Unset this recommender. After this method is called, this recommender cannot be used unless the method {@link #setup(Dataset, Object...)} is called again.
	 * @throws RemoteException if any error raises.
	 */
	void unsetup() throws RemoteException;

	
	/**
	 * Getting a list of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} will call this method before estimation task and recommendation task.
	 * Suppose every item have types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 * @return list of filters.
	 * @throws RemoteException if any error raises.
	 */
	RecommendFilterList getFilterList() throws RemoteException;
	
	
	/**
	 * This method is very important, which is used to estimate rating values of given items (users). Any class that implements this interface must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and a set of item (user) identifiers.
	 * The output result is a set of predictive or estimated rating values of items (users) specified by the second input parameter.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException;

	
	/**
	 * This method is the most important one in recommendation task. Any class that implements this interface must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and the maximum recommended items (users) in the returned rating vector.
	 * The output result is a list of recommended items (users) which is provided to the user (item).
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param maxRecommend the maximum recommended items (users) in the returned rating vector.
	 * @return list of recommended items (users) which is provided to the user (item), represented by {@link RatingVector} class. The number of items (users) of such list is specified by the the maximum number. Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException;

	
}
