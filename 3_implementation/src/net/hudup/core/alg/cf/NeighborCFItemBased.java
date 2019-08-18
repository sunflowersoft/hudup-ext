package net.hudup.core.alg.cf;

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
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.<br/>
 * <br/>
 * There are many authors who contributed measure to this class.<br/>
 * Authors Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu contributed PSS measures and NHSM measure.<br>
 * Authors Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi contributed BC and BCF measures.<br>
 * Author Hyung Jun Ahn contributed PIP measure.<br>
 * Authors Keunho Choi and Yongmoo Suh contributed PC measure.<br>
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
	protected RatingVector estimate0(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		return estimate0(this, param, queryIds);
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
	 */
	public static RatingVector estimate0(NeighborCF cf, RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		if (param.ratingVector == null) return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = cf.getConfig().getAsBoolean(HYBRID);
		RatingVector thisUser = param.ratingVector;
		double minValue = cf.dataset.getConfig().getMinRating();
		double maxValue = cf.dataset.getConfig().getMaxRating();
		Fetcher<RatingVector> itemRatings = cf.dataset.fetchItemRatings();
		for (int itemId : queryIds) {
			RatingVector thisItem = cf.dataset.getItemRating(itemId);
			if (thisItem == null) continue; //This item is not empty and has no unrated if it is not null because it is retrieved from dataset.
			if (thisUser.isRated(itemId) && !thisItem.isRated(thisUser.id())) {
				thisItem = (RatingVector)thisItem.clone();
				thisItem.put(thisUser.id(), thisUser.get(itemId));
			}

			if (thisItem.isRated(thisUser.id())) {
				result.put(itemId, thisItem.get(thisUser.id()));
				continue;
			}
			
			Profile itemProfile1 = hybrid ? cf.dataset.getItemProfile(itemId) : null;
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
					
					Profile itemProfile2 = hybrid ? cf.dataset.getItemProfile(thatItem.id()) : null;
					
					// computing similarity
					double sim = cf.similar(thisItem, thatItem, itemProfile1, itemProfile2, thisUser.id());
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
				e.printStackTrace();
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
			e.printStackTrace();
		}
		
		return result.size() == 0 ? null : result;
	}

	
	@Override
	public double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	public double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pip(vRating1, vRating2, this.userMeans);
	}


	@Override
	public double pss(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return pss(vRating1, vRating2, this.ratingMedian, this.userMeans);
	}
	
	
	@Override
	public double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		// TODO Auto-generated method stub
		return pc(vRating1, vRating2, fixedColumnId, this.userMeans);
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		// TODO Auto-generated method stub
		return this.dataset.getUserRating(columnId);
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
