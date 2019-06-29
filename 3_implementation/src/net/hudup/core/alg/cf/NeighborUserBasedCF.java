/**
 * 
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

/**
 * This class sets up the neighbor collaborative filtering (Neighbor CF) algorithm for users. It extends directly {@link NeighborCF} class.
 * It is often called Neighbor User-Based CF because the similar measure is calculated between two user rating vectors (possibly, plus two user profiles).
 * Note, user rating vector contains all ratings of the same user on many items.
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NeighborUserBasedCF extends NeighborCF implements DuplicatableAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborUserBasedCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		if (param.ratingVector.count(true) < 2)
			return null;
		
		Fetcher<RatingVector> vRatings = null;
		vRatings = dataset.fetchUserRatings();
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = config.getAsBoolean(HYBRID);
		double minValue = dataset.getConfig().getMinRating();
		double maxValue = dataset.getConfig().getMaxRating();
		for (int queryId : queryIds) {
			double accum = 0;
			double simTotal = 0;
			try {
				while (vRatings.next()) {
					RatingVector that = vRatings.pick();
					if (that == null || !that.isRated(queryId))
						continue;
					
					Profile profile1 = hybrid ? param.profile : null;
					Profile profile2 = hybrid ? dataset.getUserProfile(that.id()) : null;
					
					// computing similarity array
					double sim = similar(param.ratingVector, that, profile1, profile2);
					if (!Util.isUsed(sim))
						continue;
					
					double thatValue = that.get(queryId).value;
					double thatMean = that.mean();
					double deviate = thatValue - thatMean;
					accum += sim * deviate;
					simTotal += Math.abs(sim);
				}
				vRatings.reset();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			double mean = param.ratingVector.mean();
			double value = simTotal == 0 ? mean : mean + accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;
			
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
			return "neighbor_userbased";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}
	
	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborUserBasedCF cf = new NeighborUserBasedCF();
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
