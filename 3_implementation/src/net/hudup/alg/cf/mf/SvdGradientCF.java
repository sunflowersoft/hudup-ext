package net.hudup.alg.cf.mf;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseRecommendIntegrated;
import net.hudup.core.alg.RecommendFilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.xURI;


/**
 * This class implements Singular Vector Decomposition (SVD) algorithm for collaborative filtering.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SvdGradientCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SvdGradientCF() {
		super();
		// TODO Auto-generated constructor stub
		
		if (kb != null)
			config.putAll( ((KBaseRecommendIntegrated) kb).getDefaultParameters() );
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return SvdGradientKB.create(this);
	}

	
	/**
	 * Setting up algorithm with user rating matrix.
	 * @param userMatrix specified user rating matrix, represented by {@link RatingMatrix}.
	 * @throws Exception if any error raises.
	 */
	public synchronized void setup0(RatingMatrix userMatrix) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		
		((SvdGradientKB) kb).learn0(userMatrix);
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		SvdGradientKB kb = (SvdGradientKB) getKBase();
		if (kb.isEmpty())
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		
		int userId = result.id();
		for (int queryId : queryIds) {
			double ratingValue = kb.estimate(userId, queryId);
			if (Util.isUsed(ratingValue))
				result.put(queryId, ratingValue);
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		
		SvdGradientKB kb = (SvdGradientKB) getKBase();
		if (kb.isEmpty())
			return null;

		param = recommendPreprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		List<Integer> itemIds = kb.getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : itemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), RecommendFilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		double maxRating = config.getMaxRating();
		int userId = param.ratingVector.id();
		
		List<Pair> pairs = Util.newList();
		for (int itemId : queryIds) {
			
			double value = kb.estimate(userId, itemId);
			if (!Util.isUsed(value) || !Accuracy.isRelevant(value, this))
				continue;
			
			// Finding maximum rating
			int found = Pair.findIndexOfLessThan(value, pairs);
			Pair pair = new Pair(itemId, value);
			if (found == -1)
				pairs.add(pair);
			else 
				pairs.add(found, pair);
			
			int n = pairs.size();
			// Having maxRecommend + 1 if all are maximum rating.
			if (maxRecommend > 0 && n >= maxRecommend) {
				int lastIndex = n - 1;
				Pair last = pairs.get(lastIndex);
				if (last.value() == maxRating)
					break;
				else if (n > maxRecommend)
					pairs.remove(lastIndex);
			}
			
		}
		
		int n = pairs.size();
		if (maxRecommend > 0 && n > maxRecommend) {
			if (pairs.size() == maxRecommend + 1)
				pairs.remove(n - 1); //Remove the redundant recommended item because the pair list has almost maxRecommend + 1 pairs.
			else
				pairs = pairs.subList(0, maxRecommend); //The pair list has at most maxRecommend + 1 pairs and so this code line is for safe.
		}
		if (pairs.size() == 0)
			return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
	}

	
//	/**
//	 * This is backup recommendation method. It is not used in current implementation.
//	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
//	 * @param maxRecommend the maximum recommended items (users) in the returned rating vector.
//	 * @return list of recommended items (users) which is provided to the user (item), represented by {@link RatingVector} class. The number of items (users) of such list is specified by the the maximum number. Return null if cannot estimate.
//	 * @throws RemoteException if any error raises.
//	 */
//	@SuppressWarnings("unused")
//	private synchronized RatingVector recommend0(RecommendParam param, int maxRecommend) throws RemoteException {
//		// TODO Auto-generated method stub
//		
//		SvdGradientKB kb = (SvdGradientKB) getKBase();
//		if (kb.isEmpty())
//			return null;
//
//		param = recommendPreprocess(param);
//		if (param == null)
//			return null;
//		
//		filterList.prepare(param);
//		List<Integer> itemIds = kb.getItemIds();
//		Set<Integer> queryIds = Util.newSet();
//		for (int itemId : itemIds) {
//			
//			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), RecommendFilterParam.create(itemId)) )
//				queryIds.add(itemId);
//		}
//		
//		double maxRating = config.getMaxRating();
//		int userId = param.ratingVector.id();
//		
//		List<Pair> pairs = Util.newList();
//		//int size = queryIds.size();
//		//int i = 0;
//		for (int itemId : queryIds) {
//			//i++;
//			
//			double value = kb.estimate(userId, itemId);
//			if (!Util.isUsed(value))
//				continue;
//			
//			// Finding maximum rating
//			int found = Pair.findIndexOfLessThan(value, pairs);
//			Pair pair = new Pair(itemId, value);
//			if (found == -1)
//				pairs.add(pair);
//			else 
//				pairs.add(found, pair);
//			
//			int n = pairs.size();
//			// Having maxRecommend + 1 if all are maximum rating.
//			if (maxRecommend > 0 && n >= maxRecommend) {
//				int lastIndex = pairs.size() - 1;
//				Pair last = pairs.get(lastIndex);
//				if (last.value() == maxRating /*|| i >= size*/)
//					break;
//				else if (n > maxRecommend)
//					pairs.remove(lastIndex);
//			}
//			
//		}
//		
//		if (maxRecommend > 0 && pairs.size() > maxRecommend) {
//			if (pairs.size() == maxRecommend + 1)
//				pairs.remove(pairs.size() - 1); //Remove the redundant recommended item because the pair list has almost maxRecommend + 1 pairs.
//			else
//				pairs = pairs.subList(0, maxRecommend); //The pair list has at most maxRecommend + 1 pairs and so this code line is for safe.
//		}
//		if (pairs.size() == 0)
//			return null;
//		
//		RatingVector rec = param.ratingVector.newInstance(true);
//		Pair.fillRatingVector(rec, pairs);
//		return rec;
//	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "svd_gradient";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SvdGradientCF();
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean validate(String key, Serializable value) {
				
				if (value instanceof Number) {
					double valueNumber = ((Number)value).doubleValue();
					
					if (valueNumber < 0)
						return false;
				}
				
				return true;
			}
			
		};
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		if (kb != null)
			config.putAll( ((KBaseRecommendIntegrated) kb).getDefaultParameters() );
		return config;
	}
	
}


