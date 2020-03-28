/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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
				// Having maxRecommend + 1 if all are maximum rating.
				if (maxRecommend > 0 && n >= maxRecommend) {
					int lastIndex = n - 1;
					Pair last = pairs.get(lastIndex);
					if (last.value() == maxRating)
						break;
					else if (n > maxRecommend)
						pairs.remove(lastIndex);
				}
				
				
			} // end while
	
			
			int n = pairs.size();
			if (maxRecommend > 0 && n > maxRecommend) {
				if (pairs.size() == maxRecommend + 1)
					pairs.remove(n - 1); //Remove the redundant recommended item because the pair list has almost maxRecommend + 1 pairs.
				else
					pairs = pairs.subList(0, maxRecommend); //The pair list has at most maxRecommend + 1 pairs and so this code line is for safe.
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
				// TODO Auto-generated catch block
				LogUtil.trace(e);
			}
			
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
//	private synchronized RatingVector recommend1(RecommendParam param, int maxRecommend) throws RemoteException {
//		param = recommendPreprocess(param);
//		if (param == null)
//			return null;
//		
//		filterList.prepare(param);
//		Fetcher<Integer> fieldIds = dataset.fetchItemIds();
//		
//		List<Pair> pairs = Util.newList();
//		double maxRating = getMaxRating(); //Bug fixing date: 2019.07.13 by Loc Nguyen
//		try {
//			//int size = fieldIds.getMetadata().getSize(); //The dataset can be dynamic and so this statement needs to be removed.
//			
//			//int i = 0;
//			while (fieldIds.next()) {
//				//i++;
//				
//				Integer fieldId = fieldIds.pick();
//				if (fieldId == null || fieldId < 0 || param.ratingVector.isRated(fieldId))
//					continue;
//				//
//				if(!filterList.filter(dataset, RecommendFilterParam.create(fieldId)))
//					continue;
//				
//				Set<Integer> queryIds = Util.newSet();
//				queryIds.add(fieldId);
//				RatingVector predict = estimate(param, queryIds);
//				if (predict == null || !predict.isRated(fieldId))
//					continue;
//				
//				// Finding maximum rating
//				double value = predict.get(fieldId).value;
//				int found = Pair.findIndexOfLessThan(value, pairs);
//				Pair pair = new Pair(fieldId, value);
//				if (found == -1)
//					pairs.add(pair);
//				else 
//					pairs.add(found, pair);
//				
//				int n = pairs.size();
//				// Having maxRecommend + 1 if all are maximum rating.
//				if (maxRecommend > 0 && n >= maxRecommend) {
//					int lastIndex = pairs.size() - 1;
//					Pair last = pairs.get(lastIndex);
//					if (last.value() == maxRating /*|| i >= size*/) //The dataset can be dynamic and so the statement "i >= size" needs to be removed.
//						break;
//					else if (n > maxRecommend)
//						pairs.remove(lastIndex);
//				}
//				
//				
//			} // end while
//	
//			
//			if (maxRecommend > 0 && pairs.size() > maxRecommend) {
//				if (pairs.size() == maxRecommend + 1)
//					pairs.remove(pairs.size() - 1); //Remove the redundant recommended item because the pair list has almost maxRecommend + 1 pairs.
//				else
//					pairs = pairs.subList(0, maxRecommend); //The pair list has at most maxRecommend + 1 pairs and so this code line is for safe.
//			}
//		}
//		catch (Throwable e) {
//			LogUtil.trace(e);
//		}
//		finally {
//			try {
//				fieldIds.close();
//			} 
//			catch (Throwable e) {
//				// TODO Auto-generated catch block
//				LogUtil.trace(e);
//			}
//			
//		}
//		
//		
//		if (pairs.size() == 0)
//			return null;
//		
//		RatingVector rec = param.ratingVector.newInstance(true);
//		Pair.fillRatingVector(rec, pairs);
//		return rec;
//		
//	}
//
//	
//	/**
//	 * This is backup recommendation method. It is not used in current implementation.
//	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
//	 * @param maxRecommend the maximum recommended items (users) in the returned rating vector.
//	 * @return list of recommended items (users) which is provided to the user (item), represented by {@link RatingVector} class. The number of items (users) of such list is specified by the the maximum number. Return null if cannot estimate.
//	 * @throws RemoteException if any error raises.
//	 */
//	@SuppressWarnings("unused")
//	private synchronized RatingVector recommend0(RecommendParam param, int maxRecommend) throws RemoteException {
//		param = recommendPreprocess(param);
//		if (param == null) return null;
//		
//		filterList.prepare(param);
//		
//		Set<Integer> queryIds = Util.newSet();
//		Fetcher<RatingVector> items = dataset.fetchItemRatings();
//		try {
//			while (items.next()) {
//				RatingVector v = items.pick();
//				if (v == null || v.size() == 0)
//					continue;
//				
//				int itemId = v.id();
//				if(filterList.filter(dataset, RecommendFilterParam.create(itemId)))
//					queryIds.add(itemId);
//			}
//		}
//		catch (Throwable e) {
//			LogUtil.trace(e);
//		}
//		finally {
//			try {
//				if (items != null) items.close();
//			} 
//			catch (Throwable e) {
//				// TODO Auto-generated catch block
//				LogUtil.trace(e);
//			}
//			
//		}
//		
//		RatingVector predict = estimate(param, queryIds);
//		if (predict == null) return null;
//		
//		List<Pair> pairs = Pair.toPairList(predict);
//		Pair.sort(pairs, true, maxRecommend);
//		RatingVector rec = param.ratingVector.newInstance(true);
//		Pair.fillRatingVector(rec, pairs);
//		return rec.size() == 0 ? null : rec;
//	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {RecommenderRemote.class.getName(), MemoryBasedAlgRemote.class.getName()};
	}


}
