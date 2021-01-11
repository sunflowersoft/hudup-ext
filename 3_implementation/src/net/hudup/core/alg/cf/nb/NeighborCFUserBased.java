/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.ObjectPair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;

/**
 * This class sets up the nearest neighbors collaborative filtering algorithm for users. It extends directly {@link NeighborCF} class.
 * It is often called Neighbor User-Based CF because the similar measure is calculated between two user rating vectors (possibly, plus two user profiles).
 * Note, user rating vector contains all ratings of the same user on many items.
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.<br>
 * <br>
 * There are many authors who contributed measure to this class.<br>
 * Authors Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, and Fan Min contributed Triangle measure and TJM measure.<br>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NeighborCFUserBased extends NeighborCF implements DuplicatableAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFUserBased() {
		super();
	}


	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return estimate(this, param, queryIds);
	}


	/**
	 * This method is very important, which is used to estimate rating values of given items (users). Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and a set of item (user) identifiers.
	 * The output result is a set of predictive or estimated rating values of items (users) specified by the second input parameter.
	 * @param cf current neighbor algorithm.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * There are three cases of <code>param.ratingVector</code>:
	 * <ol>
	 * <li>Its id &lt; 0, which indicates it is not stored in training dataset then, caching does not work even though this is cached algorithm.</li>
	 * <li>Its id &ge; 0 and, it must be empty or the same to the existing one in training dataset. If it is empty, it will be fulfilled as the same to the existing one in training dataset.</li>
	 * <li>Its id is &ge; 0 but, it is not stored in training dataset then, it must be a full rating vector of a user.</li>
	 * </ol>
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	public static RatingVector estimate(NeighborCF cf, RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		int knn = cf.getConfig().getAsInt(KNN);
		knn = knn < 0 ? 0 : knn;
		if (cf.getConfig().getAsBoolean(FAST_RECOMMEND) && knn > 0)
			return estimateFast(cf, param, queryIds);
		
		if (param.ratingVector == null) return null;
		
		RatingVector thisUser = param.ratingVector;
		RatingVector innerUser = cf.getDataset().getUserRating(thisUser.id());
		if (innerUser != null) {
			Set<Integer> itemIds = innerUser.fieldIds(true);
			itemIds.removeAll(thisUser.fieldIds(true));
			if (itemIds.size() > 0) thisUser = (RatingVector)thisUser.clone();
			for (int itemId : itemIds) {
				if (!thisUser.isRated(itemId))
					thisUser.put(itemId, innerUser.get(itemId));
			}
		}
		if (thisUser.size() == 0) return null;
		
		RatingVector result = thisUser.newInstance(true);
		boolean hybrid = cf.getConfig().getAsBoolean(HYBRID);
		Profile thisProfile = hybrid ? param.profile : null;
		double minValue = cf.getMinRating();
		double maxValue = cf.getMaxRating();
		boolean isBoundedMinMax = cf.isBoundedMinMaxRating();
		double simThreshold = getSimThreshold(cf.getConfig());
		double thisMean = cf.calcRowMean(thisUser);
		Map<Integer, Double> localUserSimCache = Util.newMap();
		Fetcher<RatingVector> userRatings = cf.getDataset().fetchUserRatings();
		for (int itemId : queryIds) {
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			List<ObjectPair<RatingVector>> pairs = Util.newList();
			try {
				while (userRatings.next()) {
					RatingVector thatUser = userRatings.pick();
					if (thatUser == null || thatUser.id()== thisUser.id() || !thatUser.isRated(itemId))
						continue;
					
					Profile thatProfile = hybrid ? cf.getDataset().getUserProfile(thatUser.id()) : null;
					
					// computing similarity
					double sim = Constants.UNUSED;
					if (cf.isCached() && cf.isCachedSim() && thisUser.id() < 0) { //Local caching
						if (localUserSimCache.containsKey(thatUser.id()))
							sim = localUserSimCache.get(thatUser.id());
						else {
							sim = cf.sim(thisUser, thatUser, thisProfile, thatProfile, itemId);
							localUserSimCache.put(thatUser.id(), sim);
						}
					}
					else
						sim = cf.sim(thisUser, thatUser, thisProfile, thatProfile, itemId);
					if (!Util.isUsed(sim) || (Util.isUsed(simThreshold) && sim < simThreshold))
						continue;
					
					if (knn == 0) {
						double deviate = thatUser.get(itemId).value - cf.calcRowMean(thatUser);
						accum += sim * deviate;
						simTotal += Math.abs(sim);
						
						calculated = true;
					}
					else {
						int found = ObjectPair.findIndexOfLessThan(sim, pairs);
						ObjectPair<RatingVector> pair = new ObjectPair<RatingVector>(thatUser, sim);
						if (found == -1) {
							if (pairs.size() < knn) pairs.add(pair);
						}
						else {
							pairs.add(found, pair);
							if (pairs.size() > knn) pairs.remove(pairs.size() - 1);
						}
					}
				}
				userRatings.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			if (knn > 0) {
				accum = 0;
				simTotal = 0;
				calculated = false;
				for (ObjectPair<RatingVector> pair : pairs) {
					RatingVector thatUser = pair.key();

					double deviate = thatUser.get(itemId).value - cf.calcRowMean(thatUser);
					double sim = pair.value();
					accum +=  sim * deviate;
					simTotal += Math.abs(sim);
					
					calculated = true;
				}
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = isBoundedMinMax ? Math.min(value, maxValue) : value;
			value = isBoundedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			userRatings.close();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		localUserSimCache.clear();
		
		return result.size() == 0 ? null : result;
	}

	
	/**
	 * Estimate rating values of given items (users) in fast recommendation mode. This method is the second version.
	 * @param cf current neighbor algorithm.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * There are three cases of <code>param.ratingVector</code>:
	 * <ol>
	 * <li>Its id < 0, which indicates it is not stored in training dataset then, caching does not work even though this is cached algorithm.</li>
	 * <li>Its id &ge; 0 and, it must be empty or the same to the existing one in training dataset. If it is empty, it will be fulfilled as the same to the existing one in training dataset.</li>
	 * <li>Its id is &ge; 0 but, it is not stored in training dataset then, it must be a full rating vector of a user.</li>
	 * </ol>
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	private static RatingVector estimateFast(NeighborCF cf, RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		if (param.ratingVector == null) return null;
		
		RatingVector thisUser = param.ratingVector;
		RatingVector innerUser = cf.getDataset().getUserRating(thisUser.id());
		if (innerUser != null) {
			Set<Integer> itemIds = innerUser.fieldIds(true);
			itemIds.removeAll(thisUser.fieldIds(true));
			if (itemIds.size() > 0) thisUser = (RatingVector)thisUser.clone();
			for (int itemId : itemIds) {
				if (!thisUser.isRated(itemId))
					thisUser.put(itemId, innerUser.get(itemId));
			}
		}
		if (thisUser.size() == 0) return null;
		
		boolean hybrid = cf.getConfig().getAsBoolean(HYBRID);
		Profile thisProfile = hybrid ? param.profile : null;
		int knn = cf.getConfig().getAsInt(KNN);
		Fetcher<RatingVector> userRatings = cf.getDataset().fetchUserRatings();
		List<ObjectPair<RatingVector>> pairs = getKnnList(cf, knn, userRatings, thisUser, thisProfile);
		try {
			userRatings.close();
		} 
		catch (Throwable e) {LogUtil.trace(e);}
		if (pairs.size() == 0) return null;
		
		RatingVector result = thisUser.newInstance(true);
		double minValue = cf.getMinRating();
		double maxValue = cf.getMaxRating();
		boolean isBoundedMinMax = cf.isBoundedMinMaxRating();; 
		double thisMean = cf.calcRowMean(thisUser);
		for (int itemId : queryIds) {
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			for (ObjectPair<RatingVector> pair : pairs) {
				RatingVector thatUser = pair.key();
				if (!thatUser.isRated(itemId)) continue;
				
				double sim = pair.value();
				double deviate = thatUser.get(itemId).value - cf.calcRowMean(thatUser);
				accum +=  sim * deviate;
				simTotal += Math.abs(sim);
				
				calculated = true;
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = isBoundedMinMax ? Math.min(value, maxValue) : value;
			value = isBoundedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		return result.size() == 0 ? null : result;
	}

	
	/**
	 * Getting list of k nearest neighbors.
	 * @param cf current neighbor algorithm.
	 * @param knn the number of nearest neighbors.
	 * @param userRatings user rating dataset.
	 * @param thisUser this user rating vector.
	 * @param thisProfile this user profile.
	 * @return list of k nearest neighbors.
	 */
	private static List<ObjectPair<RatingVector>> getKnnList(NeighborCF cf, int knn, Fetcher<RatingVector> userRatings, RatingVector thisUser, Profile thisProfile) {
		knn = knn < 0 ? 0 : knn;
		List<ObjectPair<RatingVector>> pairs = Util.newList(knn);
		boolean hybrid = cf.getConfig().getAsBoolean(HYBRID);
		thisProfile = hybrid ? thisProfile : null;
		Map<Integer, Double> localUserSimCache = Util.newMap(knn);
		try {
			while (userRatings.next()) {
				RatingVector thatUser = userRatings.pick();
				if (thatUser == null || thatUser.id() == thisUser.id())
					continue;
				
				Profile thatProfile = hybrid ? cf.getDataset().getUserProfile(thatUser.id()) : null;
				
				// computing similarity
				double sim = Constants.UNUSED;
				if (cf.isCached() && cf.isCachedSim() && thisUser.id() < 0) { //Local caching
					if (localUserSimCache.containsKey(thatUser.id()))
						sim = localUserSimCache.get(thatUser.id());
					else {
						sim = cf.sim(thisUser, thatUser, thisProfile, thatProfile);
						localUserSimCache.put(thatUser.id(), sim);
					}
				}
				else
					sim = cf.sim(thisUser, thatUser, thisProfile, thatProfile);
				if (!Util.isUsed(sim)) continue;
				
				int found = ObjectPair.findIndexOfLessThan(sim, pairs);
				ObjectPair<RatingVector> pair = new ObjectPair<RatingVector>(thatUser, sim);
				if (found == -1) {
					if (knn == 0 || pairs.size() < knn) pairs.add(pair);
				}
				else {
					pairs.add(found, pair);
					if (knn > 0 && pairs.size() > knn) pairs.remove(pairs.size() - 1);
				}
			}
			
			userRatings.reset();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		localUserSimCache.clear();
		
		return pairs;
	}
	
	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.itemMeans);
	}


	@Override
	protected Set<Integer> getRowIds() {
		return userIds;
	}


	@Override
	protected RatingVector getRowRating(int rowId) {
		return dataset.getUserRating(rowId);
	}


	@Override
	protected double calcRowMean(RatingVector vRating) {
		return calcMean(this, userMeans, vRating);
	}


	@Override
	protected Set<Integer> getColumnIds() {
		return itemIds;
	}

	
	@Override
	protected RatingVector getColumnRating(int columnId) {
		return dataset.getItemRating(columnId);
	}


	@Override
	protected double calcColumnMean(RatingVector vRating) {
		return calcMean(this, itemMeans, vRating);
	}


	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_userbased";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		return "User-based nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}
	
	
}
