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

import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.Inspectable;


/**
 * This interface represents all recommendation algorithms.
 * Its two typical inherited interfaces are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are interfaces for memory-based recommendation algorithm and model-based recommendation algorithm.
 * All completed recommendation algorithms must implement directly this {@link Recommender} or implement indirectly from MemoryBasedRecomender interface and ModelBasedRecommender interface.
 * These completed recommendation algorithms must implement two main abstract methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} and two other abstract methods such as {@link #setup(Dataset, Object...)} and {@link #getDataset()}.
 * As a convention, this interface and all interfaces extending it are called {@code recommender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract interface Recommender extends RecommenderRemoteTask, Alg, Inspectable {

	
	/**
	 * Any recommender has always a reference to a dataset represented by {@link Dataset} class because recommender is executed on the dataset.
	 * Some recommender (s) store internal dataset but others only have references to dataset.
	 * Anyway, such reference always exists.
	 * @return reference to dataset.
	 * @throws RemoteException if any error raises.
	 */
	Dataset getDataset() throws RemoteException;
	
	
}
