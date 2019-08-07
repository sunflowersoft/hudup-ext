package net.hudup.core.alg.cf;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class sets up an advanced version of 2-dimensional neighbor collaborative filtering (Neighbor CF) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NeighborCF2d extends NeighborCFUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Item-based collaborative filtering algorithm.
	 */
	protected NeighborCFItemBased itemBasedCF = new NeighborCFItemBased();
	
	
	/**
	 * Default constructor.
	 */
	public NeighborCF2d() {
		// TODO Auto-generated constructor stub
		this.itemBasedCF.setConfig(this.getConfig());
	}

	
	@Override
	public void setup(Dataset dataset, Serializable... params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		this.itemBasedCF.setup(dataset, params);
	}


	@Override
	public void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		this.itemBasedCF.unsetup();
	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		if (param.ratingVector == null) return null;
		RatingVector thisUser = param.ratingVector;
		RatingVector innerUser = dataset.getUserRating(thisUser.id());
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
		boolean hybrid = config.getAsBoolean(HYBRID);
		Profile userProfile1 = hybrid ? param.profile : null;
		double minValue = getMinRating();
		double maxValue = getMaxRating();
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		for (int itemId : queryIds) {
			double accum = 0;
			double simTotal = 0;
			
			//Estimation according to user-based matrix.
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			try {
				while (userRatings.next()) {
					RatingVector thatUser = userRatings.pick();
					if (thatUser == null || thatUser.id()== thisUser.id() || !thatUser.isRated(itemId))
						continue;
					
					Profile userProfile2 = hybrid ? dataset.getUserProfile(thatUser.id()) : null;
					
					// computing similarity
					double sim = similar(thisUser, thatUser, userProfile1, userProfile2);
					if (!Util.isUsed(sim)) continue;
					
					double thatValue = thatUser.get(itemId).value;
					accum += sim * thatValue;
					simTotal += Math.abs(sim);
				}
				userRatings.reset();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			//Estimation according to item-based matrix.
			RatingVector thisItem = dataset.getItemRating(itemId);
			if (thisItem != null) {//This item is not empty and has no unrated if it is not null because it is retrieved from dataset.
				if (thisUser.isRated(itemId) && !thisItem.isRated(thisUser.id())) {
					thisItem = (RatingVector)thisItem.clone();
					thisItem.put(thisUser.id(), thisUser.get(itemId));
				}
				try {
					Profile profile1 = hybrid ? dataset.getItemProfile(itemId) : null;
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
						
						Profile profile2 = hybrid ? dataset.getItemProfile(thatItem.id()) : null;
						
						// computing similarity
						double sim = itemBasedCF.similar(thisItem, thatItem, profile1, profile2);
						if (!Util.isUsed(sim)) continue;
						
						double thatValue = thatItem.get(thisUser.id()).value;
						accum += sim * thatValue;
						simTotal += Math.abs(sim);
					}
					itemRatings.reset();
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			if (simTotal == 0) continue;
			
			double value = accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			userRatings.close();
			itemRatings.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result.size() == 0 ? null : result;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_2d";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborCF2d cf = new NeighborCF2d();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}
