/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.LogUtil;

/**
 * This class implements basically the memory-based recommendation algorithm represented by the interface {@code MemoryBasedRecomender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class MemoryBasedRecommenderAbstract extends RecommenderAbstract implements MemoryBasedRecommender, MemoryBasedAlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Fast recommendation mode.
	 */
	public static final String FAST_RECOMMEND = "fast_recommend";

	
	/**
	 * Default value for the fast recommendation mode.
	 */
	public static final boolean FAST_RECOMMEND_DEFAULT = false;

	
	/**
	 * Internal dataset.
	 */
	protected Dataset dataset = null;

	
	/**
	 * Default constructor.
	 */
	public MemoryBasedRecommenderAbstract() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		unsetup();

		this.dataset = dataset;
		
		this.config.setMetadata(dataset.getConfig().getMetadata());
		this.config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		this.config.addReadOnly(DataConfig.MAX_RATING_FIELD);
	}
	
	
	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		
		this.config.setMetadata(null);
		this.config.removeReadOnly(DataConfig.MIN_RATING_FIELD);
		this.config.removeReadOnly(DataConfig.MAX_RATING_FIELD);
		
		this.dataset = null;
	}

	
	@Override
	public Dataset getDataset() throws RemoteException {
		return dataset;
	}
	

	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		param = recommendPreprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		Fetcher<RatingVector> items = dataset.fetchItemRatings();
		
		List<Pair> pairs = Util.newList();
		double maxRating = config.getMaxRating(); //Bug fixing date: 2019.07.13 by Loc Nguyen
		try {
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null || item.size() == 0)
					continue;
				int itemId = item.id();
				if (itemId < 0 || param.ratingVector.isRated(itemId))
					continue;
				//
				if(!filterList.filter(dataset, RecommendFilterParam.create(itemId)))
					continue;
				
				Set<Integer> queryIds = Util.newSet();
				queryIds.add(itemId);
				RatingVector predict = estimate(param, queryIds);
				if (predict == null || !predict.isRated(itemId))
					continue;
				
				// Finding maximum rating
				double value = predict.get(itemId).value;
				if (!Accuracy.isRelevant(value, this))
					continue;
				int found = Pair.findIndexOfLessThan(value, pairs);
				Pair pair = new Pair(itemId, value);
				if (found == -1)
					pairs.add(pair);
				else 
					pairs.add(found, pair);
				
				int n = pairs.size();
				if (maxRecommend > 0 && n >= maxRecommend) {
					Pair last = pairs.get(n - 1);
					if (last.value() == maxRating || config.getAsBoolean(FAST_RECOMMEND)) {
						if (n > maxRecommend) pairs.remove(n - 1);
						break;
					}
					else if (n > maxRecommend)
						pairs.remove(n - 1);
				}
				
			} // end while
	
			if (maxRecommend > 0 && pairs.size() > maxRecommend) {
				pairs = pairs.subList(0, maxRecommend); //This code line is for safe in case that there are maxRecommend+1 items.
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				items.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		if (pairs.size() == 0) return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
		
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {RecommenderRemote.class.getName(), MemoryBasedAlgRemote.class.getName()};
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(FAST_RECOMMEND, FAST_RECOMMEND_DEFAULT);
		return config;
	}


}
