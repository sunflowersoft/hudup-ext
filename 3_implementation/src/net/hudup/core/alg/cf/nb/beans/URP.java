/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.nb.beans;

import java.util.Arrays;
import java.util.List;

import net.hudup.core.alg.cf.nb.Measure;
import net.hudup.core.alg.cf.nb.NeighborCFUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * URP measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class URP extends NeighborCFUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public URP() {

	}


	@Override
	public List<String> getAllMeasures() {
		return getMainMeasures();
	}


	@Override
	public List<String> getMainMeasures() {
		return Arrays.asList(getDefaultMeasure());
	}


	@Override
	protected String getDefaultMeasure() {
		return Measure.URP;
	}


	@Override
	public String getMeasure() {
		return getDefaultMeasure();
	}


	@Override
	protected void updateConfig(String measure) {
		super.updateConfig(measure);
		
		config.remove(MEASURE);
		config.remove(COSINE_NORMALIZED_FIELD);
		config.remove(COSINE_WEIGHTED_FIELD);
		config.remove(COSINE_RA_FIELD);
		config.remove(PEARSON_RA_FIELD);
		config.remove(PEARSON_WEIGHTED_FIELD);
		config.remove(MSD_FRACTION_FIELD);
		config.remove(ENTROPY_SUPPORT_FIELD);
		config.remove(RATINGJ_THRESHOLD_FIELD);
		config.remove(INDEXEDJ_INTERVALS_FIELD);
		config.remove(JACCARD_TYPE);
		config.remove(COSINE_TYPE);
		config.remove(PEARSON_TYPE);
		config.remove(MSD_TYPE);
		config.remove(TRIANGLE_TYPE);
		config.remove(IPWR_ALPHA_FIELD);
		config.remove(IPWR_BETA_FIELD);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return urp(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_urp";
	}


}
