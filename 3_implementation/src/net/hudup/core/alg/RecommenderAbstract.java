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
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This abstract class implements basically recommendation algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public abstract class RecommenderAbstract extends AlgAbstract implements Recommender, RecommenderRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Mode of bounding minimum rating and maximum rating.
	 */
	public static final String MINMAX_RATING_BOUND = "minmax_rating_bound";

	
	/**
	 * Default value for mode of bound minimum rating and maximum rating.
	 */
	public static final boolean MINMAX_RATING_BOUND_DEFAULT = true;

	
	/**
	 * Re-configuring minimum-maximum ratings mode.
	 */
	public static final String MINMAX_RATING_RECONFIG = "minmax_rating_reconfigure";

	
	/**
	 * Default value for re-configuring minimum-maximum ratings mode.
	 */
	public static final boolean MINMAX_RATING_RECONFIG_DEFAULT = false;

	
	/**
	 * Fast recommendation mode.
	 */
	public static final String FAST_RECOMMEND = "recommend_fast";

	
	/**
	 * Default value for the fast recommendation mode.
	 */
	public static final boolean FAST_RECOMMEND_DEFAULT = true;

	
	/**
	 * Reversed recommendation mode.
	 */
	public static final String REVERSED_RECOMMEND = "recommend_reversed";

	
	/**
	 * Default value for the reversed recommendation mode.
	 */
	public static final boolean REVERSED_RECOMMEND_DEFAULT = false;

	
	/**
	 * The filter list contains of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Concretely, two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} require filtering tasks specified by filters of this list.
	 * Suppose every item has types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 */
	protected RecommendFilterList filterList = new RecommendFilterList();
	
	
	/**
	 * Default constructor.
	 */
	public RecommenderAbstract() {
		super();
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		if (filterList != null) filterList.clear();
		Dataset dataset = getDataset();
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
	}


	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		param = recommendPreprocess(param);
		if (param == null)	return null;
		
		filterList.prepare(param);
		Dataset dataset = getDataset();
		Fetcher<RatingVector> items = dataset.fetchItemRatings();
		
		List<Pair> pairs = Util.newList();
		double maxRating = getMaxRating();
		double minRating = getMinRating();
		boolean reserved = getConfig().getAsBoolean(REVERSED_RECOMMEND);
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
				if (Accuracy.isRelevant(value, this) == reserved)
					continue;
				int found = reserved ? Pair.findIndexOfGreaterThan(value, pairs) : Pair.findIndexOfLessThan(value, pairs);
				Pair pair = new Pair(itemId, value);
				if (found == -1)
					pairs.add(pair);
				else 
					pairs.add(found, pair);
				
				int n = pairs.size();
				if (maxRecommend > 0 && n >= maxRecommend) {
					Pair last = pairs.get(n - 1);
					if (getConfig().getAsBoolean(FAST_RECOMMEND)
							|| (reserved ? last.value() <= minRating : last.value() >= maxRating)) {
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
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		if (pairs.size() == 0) return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
	}

	
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
		Dataset dataset = null;
		try {
			dataset = getDataset(); //This is training dataset.
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			dataset = null;
		}
		
		RatingVector vRating = null;
		if (dataset == null)
			LogUtil.warn("Training dataset is null. This is acceptable because some model-based algorithms do not store dataset");
		else {
			int userId = param.ratingVector.id(); //If the user id is negative (< 0), param.ratingVector is not stored in database. 
			vRating = dataset.getUserRating(userId);
		}
		
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
	
	
	@Override
	public RecommendFilterList getFilterList() throws RemoteException {
		return filterList;
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
		double threshold = getConfig().getAsReal(DataConfig.RELEVANT_RATING_FIELD);
		if (Util.isUsed(threshold)) return threshold;
		
		double minRating = getMinRating();
		double maxRating = getMaxRating();
		if (Util.isUsed(minRating) && Util.isUsed(maxRating))
			return Accuracy.getRelevantRatingThreshold(minRating, maxRating);
		else
			return DataConfig.RELEVANT_RATING_DEFAULT;
	}

	
	/**
	 * Checking whether minimum rating and maximum rating are bounded.
	 * @return whether minimum rating and maximum rating are bounded.
	 */
	public boolean isBoundedMinMaxRating() {
		return getConfig().getAsBoolean(MINMAX_RATING_BOUND)
				&& Util.isUsed(getMinRating()) && Util.isUsed(getMaxRating()); 
	}
	

	/**
	 * Re-configuring minimum rating and maximum rating.
	 * @param config specified configuration.
	 * @param dataset specified dataset.
	 */
	public static void reconfigMinMaxRating(DataConfig config, Dataset dataset) {
		if (config == null || dataset == null) return;

		double minRating = Double.MAX_VALUE;
		double maxRating = Double.MIN_VALUE;
		Fetcher<RatingVector> users = null;
		boolean success = true;
		try {
			users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				
				Set<Integer> itemIds = user.fieldIds(true);
				for (int itemId : itemIds) {
					double value = user.get(itemId).value;
					if (value < minRating) minRating = value;
					if (value > maxRating) maxRating = value;
				}
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
			success = false;
		}
		finally {
			if (users != null) {
				try {
					users.close();
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
		if (!success) return;
		
		try {
			double cfgMinRating = config.getMinRating();
			double cfgMaxRating = config.getMaxRating();
			double cfgRelevantRating = Accuracy.getRelevantRatingThreshold(config);
			
			if (cfgMinRating != minRating) {
				config.put(DataConfig.MIN_RATING_FIELD, minRating);
				dataset.getConfig().put(DataConfig.MIN_RATING_FIELD, minRating);
			}
			if (cfgMaxRating != maxRating) {
				config.put(DataConfig.MAX_RATING_FIELD, maxRating);
				dataset.getConfig().put(DataConfig.MAX_RATING_FIELD, maxRating);
			}
			
			double relevantRating = Accuracy.getRelevantRatingThreshold(cfgMinRating, cfgMaxRating);
			if (cfgRelevantRating != relevantRating) {
				config.put(DataConfig.RELEVANT_RATING_FIELD, relevantRating);
				dataset.getConfig().put(DataConfig.RELEVANT_RATING_FIELD, relevantRating);
			}
		} catch (Exception e) {LogUtil.trace(e);}
	}
	
	
	@Override
	public synchronized Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {RecommenderRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MINMAX_RATING_BOUND, MINMAX_RATING_BOUND_DEFAULT);
		config.put(FAST_RECOMMEND, FAST_RECOMMEND_DEFAULT);
		config.put(REVERSED_RECOMMEND, REVERSED_RECOMMEND_DEFAULT);
		config.put(MINMAX_RATING_RECONFIG, MINMAX_RATING_RECONFIG_DEFAULT);
		return config;
	}


	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
	}
	
	
}
