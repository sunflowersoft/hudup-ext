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


/**
 * This class sets up the neighbor collaborative filtering (Neighbor CF) algorithm for items. It extends directly {@link NeighborCF} class.
 * It is often called Neighbor Item-Based CF because the similar measure is calculated between two item rating vectors (possibly, plus two item profiles).
 * Note, item rating vector contains all ratings of many users on the same item.
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NeighborItemBasedCF extends NeighborCF implements DuplicatableAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborItemBasedCF() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		Fetcher<RatingVector> vRatings = null;
		vRatings = dataset.fetchItemRatings();
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = config.getAsBoolean(HYBRID);
		
		int userId = param.ratingVector.id();
		double minValue = dataset.getConfig().getMinRating();
		double maxValue = dataset.getConfig().getMaxRating();
		for (int queryId : queryIds) {
			double accum = 0;
			double simTotal = 0;
			
			RatingVector thisItem = dataset.getItemRating(queryId);
			if (thisItem == null || thisItem.count(true) < 2)
				continue;
			
			Profile profile1 = hybrid ? dataset.getItemProfile(queryId) : null;
			try {
				while (vRatings.next()) {
					RatingVector thatItem = vRatings.pick();
					if (thatItem == null || !thatItem.isRated(userId))
						continue;
					
					Profile profile2 = hybrid ? dataset.getItemProfile(thatItem.id()) : null;
					// computing similarity array
					double sim = similar(thisItem, thatItem, profile1, profile2);
					if (!Util.isUsed(sim))
						continue;
					
					double thatValue = thatItem.get(userId).value;
					double thatMean = thatItem.mean();
					double deviate = thatValue - thatMean;
					accum += sim * deviate;
					simTotal += Math.abs(sim);
				}
				vRatings.reset();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			
			double mean = thisItem.mean();
			double value = simTotal == 0 ? mean : mean + accum / simTotal;
			value = Math.min(value, maxValue);
			value = Math.max(value, minValue);

			result.put(queryId, value);
		}
		
		try {
			vRatings.close();
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
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighbor_itembased";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborItemBasedCF cf = new NeighborItemBasedCF();
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
