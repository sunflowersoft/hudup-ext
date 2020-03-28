/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;


/**
 * This class sets up the neighbor collaborative filtering (Neighbor CF) algorithm for items. It extends directly {@link NeighborCF} class.
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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		return estimate(this, param, queryIds);
	}


	/**
	 * This method is very important, which is used to estimate rating values of given items (users). Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and a set of item (user) identifiers.
	 * The output result is a set of predictive or estimated rating values of items (users) specified by the second input parameter.
	 * @param cf current neighbor algorithm.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 * @throws RemoteException if any error raises.
	 */
	public static RatingVector estimate(NeighborCF cf, RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		/*
		 * There are three cases of param.ratingVector:
		 * 1. Its id is < 0, which indicates it is not stored in training dataset then, caching does not work even though this is cached algorithm.
		 * 2. Its id is >= 0 and, it must be empty or the same to the existing one in training dataset. If it is empty, it will be fulfilled as the same to the existing one in training dataset.
		 * 3. Its id is >= 0 but, it is not stored in training dataset then, it must be a full rating vector of a user.
		 */
		if (param.ratingVector == null) return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = cf.getConfig().getAsBoolean(HYBRID);
		RatingVector thisUser = param.ratingVector;
		double minValue = cf.getConfig().getMinRating();
		double maxValue = cf.getConfig().getMaxRating();
		Fetcher<RatingVector> itemRatings = cf.getDataset().fetchItemRatings();
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
			
			Profile itemProfile1 = hybrid ? cf.getDataset().getItemProfile(itemId) : null;
			double thisMean = thisItem.mean();
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
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
					
					Profile itemProfile2 = hybrid ? cf.getDataset().getItemProfile(thatItem.id()) : null;
					
					// computing similarity
					double sim = cf.sim(thisItem, thatItem, itemProfile1, itemProfile2, thisUser.id());
					if (!Util.isUsed(sim)) continue;
					
					double thatValue = thatItem.get(thisUser.id()).value;
					double thatMean = thatItem.mean();
					double deviate = thatValue - thatMean;
					accum += sim * deviate;
					simTotal += Math.abs(sim);
					
					calculated = true;
				}
				itemRatings.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;

			result.put(itemId, value);
		}
		
		try {
			itemRatings.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_itembased";
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Item-based nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborCFItemBased cf = new NeighborCFItemBased();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
