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
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
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
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		this.itemBasedCF.setup(dataset, params);
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		this.itemBasedCF.unsetup();
	}


	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
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
//		boolean hybrid = config.getAsBoolean(HYBRID);
//		Profile userProfile1 = hybrid ? param.profile : null;
		double minValue = config.getMinRating();
		double maxValue = config.getMaxRating();
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
					
//					Profile userProfile2 = hybrid ? dataset.getUserProfile(thatUser.id()) : null;
					
					// computing similarity
					double sim = sim(thisUser, thatUser, null, null, itemId);
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
//					Profile profile1 = hybrid ? dataset.getItemProfile(itemId) : null;
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
						
//						Profile profile2 = hybrid ? dataset.getItemProfile(thatItem.id()) : null;
						
						// computing similarity
						double sim = itemBasedCF.sim(thisItem, thatItem, null/*profile1*/, null/*profile2*/, thisUser.id());
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
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Two-dimension collaborative filtering algorithm";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborCF2d cf = new NeighborCF2d();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}



/**
 * This class is also the neighbor collaborative filtering (Neighbor CF) algorithm for users because it extends directly {@link NeighborCFUserBased}.
 * However it tries to calculate a so-called 2D (2-Dimension) similar measure which is the weighted sum of two similar measures.
 * The first similar measure is of two users and second similar measure is of two items.
 * So it is called 2D (2-Dimension) Neighbor CF algorithm. It mainly re-defines the method {@link #estimate(RecommendParam, Set)} for supporting 2D similar measure.
 * It is not perfect and so it is a next-update algorithm.
 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class NeighborCF2dDeprecated extends NeighborCFUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCF2dDeprecated() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		
		Fetcher<RatingVector> vUserRatings = null;
		Fetcher<RatingVector> vItemRatings = null;
		vUserRatings = dataset.fetchUserRatings();
		vItemRatings = dataset.fetchItemRatings();
		
		RatingVector result = param.ratingVector.newInstance(true);
//		boolean hybrid = config.getAsBoolean(HYBRID);
		for (int queryId : queryIds) {
			double accum = 0;
			double simTotal = 0;
			
			try {
				while (vUserRatings.next()) {
					
					RatingVector user = vUserRatings.pick();
					if (user == null)
						continue;
					
//					Profile thisProfile = hybrid ? param.profile : null;
//					Profile userProfile = hybrid ? dataset.getUserProfile(user.id()) : null;
					
					double userSim = sim(param.ratingVector, user, null, null, queryId);
					
					try {
						while (vItemRatings.next()) {
							RatingVector item = vItemRatings.pick();
							if (item == null)
								continue;
							//
							if(!user.isRated(item.id())) 
								continue;
							
//							RatingVector fieldItem = null;
//							Profile fieldProfile = null; 
//							Profile itemProfile = null;
//							
//							if (hybrid) {
//								fieldItem = dataset.getItemRating(queryId); 
//								fieldProfile = dataset.getItemProfile(queryId);  
//								itemProfile = dataset.getItemProfile(item.id());  
//							}
							
							double itemSim = 0;
//							if (fieldItem != null)
//								itemSim = similar(fieldItem, item, fieldProfile, itemProfile, user.id());
									
							double gravity = Math.sqrt(userSim * userSim + itemSim * itemSim);
							if (!Util.isUsed(gravity))
								continue;
							
							double rating = user.get(item.id()).value;
							accum += gravity * rating;
							simTotal += Math.abs(gravity);
						}
						vItemRatings.reset();
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
					
					
				}
				vUserRatings.reset();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			
			
			double value = (simTotal == 0 ? param.ratingVector.mean() : accum / simTotal);
			result.put(queryId, value);
		}
		
		
		try {
			vUserRatings.close();
			vItemRatings.close();
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	
	
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "neighborcf_2d_deprecated";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new NeighborCF2dDeprecated();
	}
	
	
}
