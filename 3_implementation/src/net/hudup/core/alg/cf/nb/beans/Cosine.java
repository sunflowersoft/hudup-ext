/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.nb.beans;

import net.hudup.core.alg.cf.nb.Measure;
import net.hudup.core.alg.cf.nb.NeighborCFUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * Cosine measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Cosine extends NeighborCFUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public Cosine() {

	}


	@Override
	public String getMeasure() {
		return Measure.COSINE;
	}


	@Override
	protected void updateConfig(String measure) {
		config.remove(MEASURE);
		config.remove(CALC_STATISTICS);
		config.remove(MSD_FRACTION_FIELD);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return cosine(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_cosine";
	}


}
