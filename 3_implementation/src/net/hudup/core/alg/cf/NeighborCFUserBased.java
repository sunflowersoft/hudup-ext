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
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.<br/>
 * <br/>
 * There are many authors who contributed measure to this class.<br/>
 * Authors Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu contributed PSS measures and NHSM measure.<br>
 * Authors Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi contributed BC and BCF measures.<br>
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
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users).
	 */
	public static RatingVector estimate(NeighborCF cf, RecommendParam param, Set<Integer> queryIds) {
		if (param.ratingVector == null) return null;
		RatingVector thisUser = param.ratingVector;
		RatingVector innerUser = cf.dataset.getUserRating(thisUser.id());
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
		Profile userProfile1 = hybrid ? param.profile : null;
		double minValue = cf.dataset.getConfig().getMinRating();
		double maxValue = cf.dataset.getConfig().getMaxRating();
		double thisMean = thisUser.mean();
		Fetcher<RatingVector> userRatings = cf.dataset.fetchUserRatings();
		for (int itemId : queryIds) {
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			try {
				while (userRatings.next()) {
					RatingVector thatUser = userRatings.pick();
					if (thatUser == null || thatUser.id()== thisUser.id() || !thatUser.isRated(itemId))
						continue;
					
					Profile userProfile2 = hybrid ? cf.dataset.getUserProfile(thatUser.id()) : null;
					
					// computing similarity
					double sim = cf.similar(thisUser, thatUser, userProfile1, userProfile2);
					if (!Util.isUsed(sim)) continue;
					
					double thatValue = thatUser.get(itemId).value;
					double thatMean = thatUser.mean();
					double deviate = thatValue - thatMean;
					accum += sim * deviate;
					simTotal += Math.abs(sim);
					
					calculated = true;
				}
				userRatings.reset();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			userRatings.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result.size() == 0 ? null : result;
	}

	
	@Override
	public double acos(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return acos(vRating1, vRating2, this.itemMeans);
	}

	
	@Override
	public double pss(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pss(vRating1, vRating2, this.ratingMedian, this.itemMeans);
	}


	@Override
	protected RatingVector bcfGetColumnRating(int columnId) {
		// TODO Auto-generated method stub
		return this.dataset.getItemRating(columnId);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_userbased";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}
	
	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborCFUserBased cf = new NeighborCFUserBased();
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