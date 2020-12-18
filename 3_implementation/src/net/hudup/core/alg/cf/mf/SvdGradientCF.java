/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.mf;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseRecommendIntegrated;
import net.hudup.core.alg.RecommendFilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCFAbstract;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;


/**
 * This class implements Singular Vector Decomposition (SVD) algorithm for collaborative filtering.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SvdGradientCF extends ModelBasedCFAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SvdGradientCF() {
		super();
		if (kb != null)
			config.putAll( ((KBaseRecommendIntegrated) kb).getDefaultParameters() );
	}

	
	/**
	 * Setting up algorithm with user rating matrix.
	 * @param userMatrix specified user rating matrix, represented by {@link RatingMatrix}.
	 * @throws Exception if any error raises.
	 */
	public synchronized void setup0(RatingMatrix userMatrix) throws Exception {
		unsetup();
		((SvdGradientKB)kb).learn0(userMatrix);
	}

	
	@Override
	public KBase newKB() throws RemoteException {
		return SvdGradientKB.create(this);
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		SvdGradientKB kb = (SvdGradientKB) getKBase();
		if (kb.isEmpty())
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		
		double minValue = getMinRating();
		double maxValue = getMaxRating();
		boolean isBoundedMinMax = isBoundedMinMaxRating();
		int userId = result.id();
		for (int queryId : queryIds) {
			double ratingValue = kb.estimate(userId, queryId);
			if (!Util.isUsed(ratingValue)) continue;
			
			if (isBoundedMinMax) {
				ratingValue = isBoundedMinMax ? Math.min(ratingValue, maxValue) : ratingValue;
				ratingValue = isBoundedMinMax ? Math.max(ratingValue, minValue) : ratingValue;
			}
			result.put(queryId, ratingValue);
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
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
		
		double maxRating = getMaxRating();
		int userId = param.ratingVector.id();
		
		List<Pair> pairs = Util.newList();
		for (int itemId : queryIds) {
			
			double value = kb.estimate(userId, itemId);
			if (!Util.isUsed(value)) continue;
			if (!Accuracy.isRelevant(value, this))
				continue;
			
			// Finding maximum rating
			int found = Pair.findIndexOfLessThan(value, pairs);
			Pair pair = new Pair(itemId, value);
			if (found == -1)
				pairs.add(pair);
			else 
				pairs.add(found, pair);
			
			int n = pairs.size();
			if (maxRecommend > 0 && n >= maxRecommend) {
				Pair last = pairs.get(n - 1);
				if (getConfig().getAsBoolean(FAST_RECOMMEND) || last.value() >= maxRating) {
					if (n > maxRecommend) pairs.remove(n - 1);
					break;
				}
				else if (n > maxRecommend)
					pairs.remove(n - 1);
			}
			
		}
		
		if (maxRecommend > 0 && pairs.size() > maxRecommend) {
			pairs = pairs.subList(0, maxRecommend); //This code line is for safe in case that there are maxRecommend+1 items.
		}
		if (pairs.size() == 0) return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
	}

	
	@Override
	public String getName() {
		return "svd_gradient";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "SVD algorithm";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(MINMAX_RATING_RECONFIG, MINMAX_RATING_RECONFIG_DEFAULT);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean validate(String key, Serializable value) {
				
				if (value instanceof Number) {
					double valueNumber = ((Number)value).doubleValue();
					if (valueNumber < 0) return false;
				}
				
				return true;
			}
			
		};
		
//		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
//		config.setStoreUri(store);
		config.putAll(tempConfig);
		
		if (kb != null)
			config.putAll( ((KBaseRecommendIntegrated) kb).getDefaultParameters() );
		return config;
	}
	
}


