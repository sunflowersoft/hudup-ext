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
import net.hudup.core.data.RatingVector;

/**
 * This interface represents a remote recomendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface RecommenderRemote extends RecommenderRemoteTask, AlgRemote {


	@Override
	void setup(Dataset dataset, Object...params) throws RemoteException;
	
	
	@Override
	void unsetup() throws RemoteException;

	
	@Override
	RecommendFilterList getFilterList() throws RemoteException;
	
	
	@Override
	RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException;

	
	@Override
	RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException;

	
}
