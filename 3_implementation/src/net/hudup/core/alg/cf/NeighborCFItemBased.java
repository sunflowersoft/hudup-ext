/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

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
 * This class sets up the nearest neighbors collaborative filtering algorithm for items. It extends directly {@link NeighborCF} class.
 * It is often called Neighbor Item-Based CF because the similar measure is calculated between two item rating vectors (possibly, plus two item profiles).
 * Note, item rating vector contains all ratings of many users on the same item.
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.<br>
 * <br>
 * There are many authors who contributed measure to this class.<br>
 * Authors Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, and Fan Min contributed Triangle measure and TJM measure.<br>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NeighborCFItemBased extends NeighborCF implements DuplicatableAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFItemBased() {

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
	 * <li>Its id < 0, which indicates it is not stored in training dataset then, caching does not work even though this is cached algorithm.</li>
	 * <li>Its id &ge; 0 and, it must be empty or the same to the existing one in training dataset. If it is empty, it will be fulfilled as the same to the existing one in training dataset.</li>
	 * <li>Its id is &ge; 0 but, it is not stored in training dataset then, it must be a full rating vector of a user.</li>
	 * </ol>
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	public static RatingVector estimate(NeighborCF cf, RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = cf.getConfig().getAsBoolean(HYBRID);
		RatingVector thisUser = param.ratingVector;
		double minValue = cf.getMinRating();
		double maxValue = cf.getMaxRating();
		boolean isBoundedMinMax = cf.isBoundedMinMaxRating();; 
		Fetcher<RatingVector> itemRatings = cf.getDataset().fetchItemRatings();
		int knn = cf.getConfig().getAsInt(KNN);
		knn = knn < 0 ? 0 : knn;
		for (int itemId : queryIds) {
			RatingVector thisItem = cf.getDataset().getItemRating(itemId);
			if (thisItem == null) continue; //This item is not empty and has no unrated if it is not null because it is retrieved from dataset.
			if (thisUser.isRated(itemId) && !thisItem.isRated(thisUser.id())) {
				thisItem = (RatingVector)thisItem.clone();
				thisItem.put(thisUser.id(), thisUser.get(itemId));
			}

			if (thisItem.isRated(thisUser.id())) {
				result.put(itemId, thisItem.get(thisUser.id()));
				continue;
			}
			
			Profile thisProfile = hybrid ? cf.getDataset().getItemProfile(itemId) : null;
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			List<ObjectPair<RatingVector>> pairs = Util.newList();
			try {
				while (itemRatings.next()) {
					RatingVector thatItem = itemRatings.pick();
					if (thatItem == null || thatItem.id() == itemId)
						continue;
					if (thisUser.isRated(thatItem.id()) && !thatItem.isRated(thisUser.id())) {
						thatItem = (RatingVector)thatItem.clone();
						thatItem.put(thisUser.id(), thisUser.get(thatItem.id()));
					}
					if (!thatItem.isRated(thisUser.id()))
						continue;
					
					Profile thatProfile = hybrid ? cf.getDataset().getItemProfile(thatItem.id()) : null;
					
					// computing similarity
					double sim = cf.sim(thisItem, thatItem, thisProfile, thatProfile, thisUser.id());
					if (!Util.isUsed(sim)) continue;
					
					if (knn == 0) {
						double deviate = thatItem.get(thisUser.id()).value - thatItem.mean();
						accum += sim * deviate;
						simTotal += Math.abs(sim);
						
						calculated = true;
					}
					else {
						int found = ObjectPair.findIndexOfLessThan(sim, pairs);
						ObjectPair<RatingVector> pair = new ObjectPair<RatingVector>(thatItem, sim);
						if (found == -1) {
							if (pairs.size() < knn) pairs.add(pair);
						}
						else {
							pairs.add(found, pair);
							if (pairs.size() > knn) pairs.remove(pairs.size() - 1);
						}
					}
				}
				itemRatings.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			if (knn > 0) {
				accum = 0;
				simTotal = 0;
				calculated = false;
				for (ObjectPair<RatingVector> pair : pairs) {
					RatingVector thatItem = pair.key();

					double sim = pair.value();
					double deviate = thatItem.get(thisUser.id()).value - thatItem.mean();
					accum += sim * deviate;
					simTotal += Math.abs(sim);
					
					calculated = true;
				}
			}
			if (!calculated) continue;
			
			double thisMean = thisItem.mean();
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = isBoundedMinMax ? Math.min(value, maxValue) : value;
			value = isBoundedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			itemRatings.close();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return result.size() == 0 ? null : result;
	}
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_itembased";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Item-based nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
