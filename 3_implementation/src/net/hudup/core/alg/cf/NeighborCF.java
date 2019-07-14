package net.hudup.core.alg.cf;

import java.awt.Component;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.SupportCacheAlg;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.Vector;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class sets up the neighbor collaborative filtering (Neighbor CF) algorithm. It is memory-based CF because it extends directly {@link MemoryBasedCF} class.
 * Neighbor CF algorithm finds out the nearest neighbor of active user (or active item).
 * Later on, Neighbor CF algorithm recommends items that the nearest neighbor likes (rates with high value) to the active user, with assumption that the nearest neighbor and the active user (active item) share the same interests.
 * <br>
 * The most important feature of any Neighbor CF algorithm is how to calculate the similarity between two rating vectors or between two profiles so as to find out the nearest neighbor.
 * The nearest neighbor is the one who has highest similar measure with the active user (active item).
 * This class provides the method {@link #similar(RatingVector, RatingVector, Profile, Profile)} to calculate the similarity measure between two pairs.
 * The first pair includes the first rating vector and the first profile.
 * The second pair includes the second rating vector and the second profile.
 * If you only want to calculate the similarity between two rating vectors, two in put profiles are set to be null.
 * If you only want to calculate the similarity between two profiles, two in put rating vectors are set to be null.
 * In current implementation, only three similarity measures are supported such as Pearson, cosine, and hybrid.
 * Hybrid measure means that profile is merged into rating vector as a unified vector for calculating Pearson measure or cosine measure.<br/>
 * <br/>
 * There are many authors who contributed measure to this class.<br/>
 * Authors Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu contributed PSS measures and NHSM measure.<br>
 * Authors Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi contributed BC and BCF measures.<br>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class NeighborCF extends MemoryBasedCF implements SupportCacheAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * In the configuration, the entry of similarity measure has the name specified by this constant.
	 */
	public static final String MEASURE = "measure";
	
	
	/**
	 * Name of hybrid measure.
	 */
	public static final String HYBRID = "hybrid";

	
	/**
	 * Name of cosine measure.
	 */
	public static final String COSINE = "cosine";

	
	/**
	 * Name of Pearson measure.
	 */
	public static final String PEARSON = "pearson";

	
	/**
	 * Name of adjusted cosine measure.
	 */
	public static final String ACOS = "acos";

	
	/**
	 * Name of constrained Pearson correlation coefficient measure.
	 */
	public static final String CPCC = "cpcc";
	
	
	/**
	 * Name of weighted Pearson correlation coefficient measure.
	 */
	public static final String WPCC = "wpcc";
	
	
	/**
	 * Name of sigmoid Pearson correlation coefficient measure.
	 */
	public static final String SPCC = "spcc";

	
	/**
	 * Name of Jaccard measure.
	 */
	public static final String JACCARD = "jaccard";

	
	/**
	 * Name of Jaccard2 measure.
	 */
	public static final String JACCARD2 = "jaccard2";

	
	/**
	 * Name of MSD measure.
	 */
	public static final String MSD = "msd";

	
	/**
	 * Name of MSD + Jaccard measure.
	 */
	public static final String MSDJ = "msdj";

	
	/**
	 * Name of PSS measure.
	 */
	public static final String PSS = "pss";

	
	/**
	 * Name of NHSM measure.
	 */
	public static final String NHSM = "nhsm";

	
	/**
	 * Name of BCF measure.
	 */
	public static final String BCF = "bcf";

	
	/**
	 * Name of BCFJ measure (BCF + Jaccard).
	 */
	public static final String BCFJ = "bcfj";

	
	/**
	 * Value bins.
	 */
	public static final String BCF_VALUE_BINS_FIELD = "bcf_value_bins";

	
	/**
	 * Default value bins.
	 */
	public static final String BCF_VALUE_BINS_DEFAULT = "1, 2, 3, 4, 5";

	
	/**
	 * BCF median mode.
	 */
	public static final String BCF_MEDIAN_MODE_FIELD = "bcf_median";

	
	/**
	 * Default BCF median mode.
	 */
	public static final boolean BCF_MEDIAN_MODE_DEFAULT = true;

	
	/**
	 * Threshold for WPCC (weight weighted Pearson correlation coefficient).
	 */
	public static final double WPCC_THRESHOLD = 50;

	
	/**
	 * Minimum rating value
	 */
	protected double minRating = DataConfig.MIN_RATING_DEFAULT;

	
	/**
	 * Maximum rating value
	 */
	protected double maxRating = DataConfig.MAX_RATING_DEFAULT;
	
	
	/**
	 * Rating median.
	 */
	protected double ratingMedian = (DataConfig.MIN_RATING_DEFAULT + DataConfig.MAX_RATING_DEFAULT) / 2.0;

	
	/**
	 * Internal item identifiers.
	 */
	protected Set<Integer> itemIds = Util.newSet();

	
	/**
	 * General item mean.
	 */
	protected double generalItemMean = Constants.UNUSED;

	
	/**
	 * Internal item means.
	 */
	protected Map<Integer, Double> itemMeans = Util.newMap();

	
	/**
	 * General item variance.
	 */
	protected double generalItemVar = Constants.UNUSED;

	
	/**
	 * Internal item variances.
	 */
	protected Map<Integer, Double> itemVars = Util.newMap();
	
	
	/**
	 * Internal item identifiers.
	 */
	protected Set<Integer> userIds = Util.newSet();

	
	/**
	 * General user mean.
	 */
	protected double generalUserMean = Constants.UNUSED;

	
	/**
	 * Internal user means.
	 */
	protected Map<Integer, Double> userMeans = Util.newMap();

	
	/**
	 * General user variance.
	 */
	protected double generalUserVar = Constants.UNUSED;

	
	/**
	 * Internal user variances.
	 */
	protected Map<Integer, Double> userVars = Util.newMap();

	
	/**
	 * Row similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> rowSimCache = Util.newMap();


	/**
	 * Value bins.
	 */
	protected List<Double> valueBins = Util.newList();
	
	
	/**
	 * BCF column similarity cache.
	 */
	protected Map<Integer, Object> bcfColumnModuleCache = Util.newMap();

	
	/**
	 * Column BC cache.
	 */
	protected Map<Integer, Map<Integer, Object>> bcColumnCache = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public NeighborCF() {
		// TODO Auto-generated constructor stub
		
	}


	@Override
	public void setup(Dataset dataset, Object... params) throws Exception {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		
		this.minRating = getMinRating();
		this.maxRating = getMaxRating();
		this.ratingMedian = (this.maxRating + this.minRating) / 2.0;
		
		this.rowSimCache.clear();

		this.valueBins = extractConfigValueBins();
		this.bcfColumnModuleCache.clear();
		this.bcColumnCache.clear();

		updateItemMeanVars(dataset);
		updateUserMeanVars(dataset);
	}


	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		super.unsetup();
		
		this.minRating = DataConfig.MIN_RATING_DEFAULT;
		this.maxRating = DataConfig.MAX_RATING_DEFAULT;
		this.ratingMedian = (this.minRating + this.maxRating) / 2.0;
		
		this.itemIds.clear();
		this.generalItemMean = Constants.UNUSED;
		this.itemMeans.clear();
		this.generalItemVar = Constants.UNUSED;
		this.itemVars.clear();
		this.userIds.clear();
		this.generalUserMean = Constants.UNUSED;
		this.userMeans.clear();
		this.generalUserVar = Constants.UNUSED;
		this.userVars.clear();
		
		this.rowSimCache.clear();
		
		this.valueBins.clear();
		this.bcfColumnModuleCache.clear();
		this.bcColumnCache.clear();
	}


	/**
	 * Updating individual item means and variances from specified dataset.
	 * @param dataset specified dataset.
	 * @throws Exception if any error raises.
	 */
	private void updateItemMeanVars(Dataset dataset) throws Exception {
		int totalItemCount = 0;
		this.itemIds.clear();
		this.generalItemMean = 0.0;
		this.itemMeans.clear();
		this.generalItemVar = 0.0;
		this.itemVars.clear();
		
		//Resetting individual item counts and means
		Map<Integer, Double> itemCounts = Util.newMap();
		Fetcher<Integer> itemFetcher = dataset.fetchItemIds();
		while (itemFetcher.next()) {
			int itemId = itemFetcher.pick();
			this.itemIds.add(itemId);
			this.itemMeans.put(itemId, 0.0);
			itemCounts.put(itemId, 0.0);
		}
		itemFetcher.close();
		
		//Calculating item means and counts
		Fetcher<RatingVector> ratings = dataset.fetchUserRatings();
		while (ratings.next()) {
			RatingVector rating = ratings.pick();
			if (rating == null) continue;
			
			Set<Integer> itemIds = rating.fieldIds(true);
			for (int itemId : itemIds) {
				double count = itemCounts.get(itemId);
				itemCounts.put(itemId, count + 1);
				double oldMean = this.itemMeans.get(itemId);
				double newValue = rating.get(itemId).value;
				this.itemMeans.put(itemId, (oldMean*count + newValue) / (count + 1));
				
				totalItemCount++;
				this.generalItemMean += newValue;
			}
		}
		this.generalItemMean = this.generalItemMean / (double)totalItemCount;
		
		//Calculating sum of item variances.
		ratings.reset();
		while (ratings.next()) {
			RatingVector rating = ratings.pick();
			if (rating == null) continue;
			
			Set<Integer> itemIds = rating.fieldIds(true);
			for (int itemId : itemIds) {
				double value = rating.get(itemId).value;
				double d = value - this.itemMeans.get(itemId);
				if (!this.itemVars.containsKey(itemId))
					this.itemVars.put(itemId, d*d);
				else
					this.itemVars.put(itemId, this.itemVars.get(itemId) + d*d);
				
				double D = value - this.generalItemMean;
				this.generalItemVar += D*D;
			}
		}
		ratings.close();
		this.generalItemVar = this.generalItemVar / (double)totalItemCount;
		
		//Calculating item variances.
		Set<Integer> itemIds = this.itemVars.keySet();
		for (int itemId : itemIds) {
			this.itemVars.put(itemId, this.itemVars.get(itemId) / itemCounts.get(itemId)); //Use MLE variance
		}
	}
	
	
	/**
	 * Updating individual user means and variances from specified dataset.
	 * @param dataset specified dataset.
	 * @throws Exception if any error raises.
	 */
	private void updateUserMeanVars(Dataset dataset) throws Exception {
		int totalUserCount = 0;
		this.userIds.clear();
		this.generalUserMean = 0.0;
		this.userMeans.clear();
		this.generalUserVar = 0.0;
		this.userVars.clear();
		
		//Resetting individual user counts and means
		Map<Integer, Double> userCounts = Util.newMap();
		Fetcher<Integer> userFetcher = dataset.fetchUserIds();
		while (userFetcher.next()) {
			int userId = userFetcher.pick();
			this.userIds.add(userId);
			this.userMeans.put(userId, 0.0);
			userCounts.put(userId, 0.0);
		}
		userFetcher.close();
		
		//Calculating user means and counts
		Fetcher<RatingVector> ratings = dataset.fetchItemRatings();
		while (ratings.next()) {
			RatingVector rating = ratings.pick();
			if (rating == null) continue;
			
			Set<Integer> userIds = rating.fieldIds(true);
			for (int userId : userIds) {
				double count = userCounts.get(userId);
				userCounts.put(userId, count + 1);
				double oldMean = this.userMeans.get(userId);
				double newValue = rating.get(userId).value;
				this.userMeans.put(userId, (oldMean*count + newValue) / (count + 1));
				
				totalUserCount++;
				this.generalUserMean += newValue;
			}
		}
		this.generalUserMean = this.generalUserMean / (double)totalUserCount;
		
		//Calculating sum of user variances.
		ratings.reset();
		while (ratings.next()) {
			RatingVector rating = ratings.pick();
			if (rating == null) continue;
			
			Set<Integer> userIds = rating.fieldIds(true);
			for (int userId : userIds) {
				double value = rating.get(userId).value;
				double d = value - this.userMeans.get(userId);
				if (!this.userVars.containsKey(userId))
					this.userVars.put(userId, d*d);
				else
					this.userVars.put(userId, this.userVars.get(userId) + d*d);
				
				double D = value - this.generalUserMean;
				this.generalUserVar += D*D;
			}
		}
		ratings.close();
		this.generalUserVar = this.generalUserVar / (double)totalUserCount;
		
		//Calculating user variances.
		Set<Integer> userIds = this.userVars.keySet();
		for (int userId : userIds) {
			this.userVars.put(userId, this.userVars.get(userId) / userCounts.get(userId)); //Use MLE variance
		}
	}

	
	/**
	 * Getting the list of supported similar measures in names.
	 * @return supported similar measures.
	 */
	public List<String> getSupportedSimilarMeasures() {
		// TODO Auto-generated method stub
		Set<String> mSet = Util.newSet();
		mSet.add(COSINE);
		mSet.add(PEARSON);
		mSet.add(ACOS);
		mSet.add(CPCC);
		mSet.add(WPCC);
		mSet.add(SPCC);
		mSet.add(JACCARD);
		mSet.add(JACCARD2);
		mSet.add(MSD);
		mSet.add(MSDJ);
		mSet.add(PSS);
		mSet.add(NHSM);
		mSet.add(BCF);
		mSet.add(BCFJ);
		
		List<String> measures = Util.newList();
		measures.clear();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}

	
	/**
	 * Getting the default similarity measure.
	 * @return default similar measure.
	 */
	public String getDefaultSimilarMeasure() {
		return COSINE;
	}

	
	/**
	 * Calculating the similarity measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * If you only want to calculate the similarity between two rating vectors, two in put profiles are set to be null.
	 * If you only want to calculate the similarity between two profiles, two in put rating vectors are set to be null.
	 * In current implementation, only three similarity measures are supported such as Pearson, cosine, and hybrid.
	 * Hybrid measure means that profile is merged into rating vector as a unified vector for calculating Pearson measure or cosine measure.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return similarity between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public double similar(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String measure = config.getAsString(MEASURE);
		if (measure == null) return Constants.UNUSED;

		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return similarAsUsual(measure, vRating1, vRating2, profile1, profile2);
			}
		};
		
		return (double)cacheTask(vRating1.id(), vRating2.id(), this.rowSimCache, task);
	}
	
	
	/**
	 * Calculating the similarity measure between two pairs as usual.
	 * @param measure specified measure.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return similarity between both two {@link RatingVector} (s) and two {@link Profile} (s) as usual.
	 */
	protected double similarAsUsual(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		boolean hybrid = config.getAsBoolean(HYBRID);
		if (!hybrid) {
			profile1 = null;
			profile2 = null;
		}
		
		if (measure.equals(COSINE))
			return cosine(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PEARSON))
			return corr(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(ACOS))
			return acos(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(CPCC))
			return cpcc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(WPCC))
			return wpcc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(SPCC))
			return spcc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(JACCARD))
			return jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(JACCARD2))
			return jaccard2(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(MSD))
			return msd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(MSDJ))
			return msdj(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PSS))
			return pss(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(NHSM))
			return nhsm(vRating1, vRating2, profile1, profile2);
		else
			return Constants.UNUSED;
			
	}
	
	
	/**
	 * Calculating the cosine measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return the cosine between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public double cosine(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		if (profile1 == null || profile2 == null) 
			return vRating1.cosine(vRating2);
		
		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
		Vector vector1 = vectors[0];
		Vector vector2 = vectors[1];
		
		return vector1.cosine(vector2);
	}

	
	/**
	 * Calculating the correlation coefficient between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return correlation coefficient between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public double corr(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		if (profile1 == null || profile2 == null) 
			return vRating1.corr(vRating2);

		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
		Vector vector1 = vectors[0];
		Vector vector2 = vectors[1];
		
		return vector1.corr(vector2);
	}

	
	/**
	 * Normalizing two pairs into two normalized vectors.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * Note, all elements of normalized vectors are &gt;=0 and &lt;=1 and sum of them is 1.
	 * However, for the result of this method, each normalized vector corresponding to each pair has two parts such as part of rating vector and part of profile.
	 * Part of rating vector is normalized partially and part of profile is normalized partially.
	 * For example, suppose the first pair is (1, 2, 3, 4, 5, 6) in which part of rating vector is (1, 2, 3) and part of profile is (4, 5, 6).
	 * Such pair is normalized as vector (1/6, 2/6, 3/6, 4/15, 5/15, 6/15) because the normalized rating vector (1/6, 2/6, 3/6) is and the normalized profile is (4/15, 5/15, 6/15).
	 * If profile does not contains numbers, there is a conversion.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return array of two normalized vectors.
	 */
	private Vector[] toNormVector(
			RatingVector vRating1, RatingVector vRating2, 
			Profile profile1, Profile profile2) {
		
		Vector vector1 = new Vector(0, 0);
		Vector vector2 = new Vector(0, 0);
		
		Vector[] vectors = toNormVector(vRating1, vRating2);
		if (vectors.length != 0) {
			vector1.concat(vectors[0]);
			vector2.concat(vectors[1]);
		}
		
		vectors = toNormDiscreteVectors(profile1, profile2);
		if (vectors.length != 0) {
			vector1.concat(vectors[0]);
			vector2.concat(vectors[1]);
		}
		
		return new Vector[] { vector1, vector2 };
	}
	
	
	/**
	 * Normalizing two rating vectors into two normalized vectors.
	 * For example, two rating vectors (1, 3, 5) and (2, 4, 6) are normalized into (1/9, 3/9, 5/9) and (2/12, 4/12, 6/12).
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return array of two normalized {@link Vector} (s) from two rating vectors.
	 */
	private Vector[] toNormVector(RatingVector vRating1, RatingVector vRating2) {
		
		List<Integer> common = Util.newList();
		common.addAll(vRating1.fieldIds(true));
		common.retainAll(vRating2.fieldIds(true));
		if (common.size() == 0)
			return new Vector[0];
		
		double n = dataset.getConfig().getMaxRating() - dataset.getConfig().getMinRating();
		
		double[] data1 = new double[common.size()];
		double[] data2 = new double[common.size()];
		for (int i = 0; i < common.size(); i++) {
			int id = common.get(i);
			
			data1[i] = vRating1.get(id).value / n; 
			data2[i] = vRating2.get(id).value / n; 
		}
		
		return new Vector[] { new Vector(data1), new Vector(data2)};
	}
	
	
	/**
	 * Normalizing two profiles into two normalized vector.
	 * Note, values in such two profiles are binary or nominal. Nominal indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * 
	 * @param profile1 first profiles.
	 * @param profile2 second profiles.
	 * @return array of two normalized {@link Vector} (s) from two profiles.
	 */
	private Vector[] toNormDiscreteVectors(Profile profile1, Profile profile2) {
		if (profile1 == null || profile2 == null)
			return new Vector[0];
		
		List<Double> data1 = Util.newList();
		List<Double> data2 = Util.newList();

		AttributeList attRef = profile1.getAttRef();
		for (int i = 0; i < attRef.size(); i++) {
			Attribute att = attRef.get(i);
			if (!att.isCategory() || att.isKey())
				continue;
			if (profile1.isMissing(i) || profile2.isMissing(i))
				continue;
			
			double value1 = profile1.getValueAsReal(i);
			double value2 = profile2.getValueAsReal(i);
			if (att.getType() == Type.nominal) {
				value1 = profile1.getNominalNormalizedValue(i);
				value2 = profile2.getNominalNormalizedValue(i);
			}
			data1.add(value1);
			data2.add(value2);
		}
		
		if (data1.size() == 0 || data2.size() == 0)
			return new Vector[0];
		
		return new Vector[] { new Vector(data1), new Vector(data2)};
	}

	
	/**
	 * Calculating the ACOS (adjusted cosine) measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return ACOS measure between both two rating vectors.
	 */
	public abstract double acos(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2);
	

	/**
	 * Calculating the ACOS (adjusted cosine) measure between two rating vectors.
	 * @param vRating1 the first rating vectors.
	 * @param vRating2 the second rating vectors.
	 * @param fieldMeans mean value of field ratings.
	 * @return ACOS (adjusted cosine) measure between two rating vectors.
	 */
	public static double acos(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;

		double VX = 0, VY = 0;
		double VXY = 0;
		for (int fieldId : common) {
			double mean = fieldMeans.get(fieldId);
			double deviate1 = vRating1.get(fieldId).value - mean;
			double deviate2 = vRating2.get(fieldId).value - mean;
			
			VX  += deviate1 * deviate1;
			VY  += deviate2 * deviate2;
			VXY += deviate1 * deviate2;
		}
		
		if (VX == 0 && VY == 0)
			return 1;
		else if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
	}

	
	/**
	 * Calculating the CPCC (constrained Pearson correlation coefficient) measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return CPCC measure between both two rating vectors.
	 */
	public double cpcc(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int fieldId : common) {
			double deviate1 = vRating1.get(fieldId).value - this.ratingMedian;
			double deviate2 = vRating2.get(fieldId).value - this.ratingMedian;
			
			VX  += deviate1 * deviate1;
			VY  += deviate2 * deviate2;
			VXY += deviate1 * deviate2;
		}
		
		if (VX == 0 && VY == 0)
			return 1;
		else if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
	}
	
	
	/**
	 * Calculating the WPCC (weighted Pearson correlation coefficient) measure between two pairs. SMTP is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return WPCC measure between both two rating vectors.
	 */
	public double wpcc(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		double N = common.size();
		if (N <= WPCC_THRESHOLD)
			return corr(vRating1, vRating2, profile1, profile2) * (N/WPCC_THRESHOLD);
		else
			return corr(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the WPCC (weighted Pearson correlation coefficient) measure between two pairs. SMTP is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return WPCC measure between both two rating vectors.
	 */
	public double spcc(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		double N = common.size();
		
		return corr(vRating1, vRating2, profile1, profile2) / (1 + Math.exp(-N/2.0));
	}
	
	
	/**
	 * Calculating the Jaccard measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Jaccard measure between both two rating vectors and profiles.
	 */
	public double jaccard(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> ratedIds1 = vRating1.fieldIds(true);
		Set<Integer> ratedIds2 = vRating2.fieldIds(true);
		if (ratedIds1.size() == 0 && ratedIds2.size() == 0)
			return 1;
		if (ratedIds1.size() == 0 || ratedIds2.size() == 0)
			return 0;
		
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		Set<Integer> union = unionFieldIds(vRating1, vRating2);
		return (double)common.size() / (double)(union.size());

	}
	
	
	/**
	 * Calculating the Jaccard2 measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Jaccard2 measure between both two rating vectors and profiles.
	 */
	public double jaccard2(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> ratedIds1 = vRating1.fieldIds(true);
		Set<Integer> ratedIds2 = vRating2.fieldIds(true);
		if (ratedIds1.size() == 0 && ratedIds2.size() == 0)
			return 1;
		if (ratedIds1.size() == 0 || ratedIds2.size() == 0)
			return Constants.UNUSED;
		
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		return (double)common.size() / (double)(ratedIds1.size()*ratedIds2.size());
	}

	
	/**
	 * Calculating the PSS measure between two pairs. PSS measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return PSS measure between both two rating vectors and profiles.
	 */
	public abstract double pss(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2);


	/**
	 * Calculating the PSS measure between two rating vectors. PSS measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param ratingMedian general mean.
	 * @param fieldMeans map of field means.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return PSS measure between two rating vectors.
	 */
	public static double pss(RatingVector vRating1, RatingVector vRating2, double ratingMedian, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double pss = 0.0;
		for (int id : common) {
			double r1 = vRating1.get(id).value;
			double r2 = vRating2.get(id).value;
			
			double pro = 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs(r1-r2)));
			//Note: I think that it is better to use mean instead of median for significant.
			//At the worst case, median is always approximate to mean given symmetric distribution like normal distribution.
			//Moreover, in fact, general user mean is equal to general item mean.
			//However, I still use rating median because of respecting authors' ideas.
			double sig = 1.0 / (1.0 + Math.exp(
					-Math.abs(r1-ratingMedian)*Math.abs(r2-ratingMedian)));
			double singular = 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs((r1+r2)/2.0 - fieldMeans.get(id))));
			
			pss += pro * sig * singular;
		}
		
		return pss;
	}
	
	
	/**
	 * Calculating the NHSM measure between two pairs. NHSM measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return NHSM measure between both two rating vectors and profiles.
	 */
	public double nhsm(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double mean1 = vRating1.mean();
		double var1 = vRating1.mleVar();
		double mean2 = vRating2.mean();
		double var2 = vRating2.mleVar();
		
		double urp = 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs(mean1-mean2)*Math.abs(var1-var2)));
		double jaccard2 = jaccard2(vRating1, vRating2, profile1, profile2);
		return pss(vRating1, vRating2, profile1, profile2) * jaccard2 * urp;
	}


	/**
	 * Calculating the MSD measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return MSD measure between both two rating vectors and profiles.
	 */
	public double msd(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double sum = 0;
		for (int id : common) {
			double d = (vRating1.get(id).value - vRating2.get(id).value) / this.maxRating;
			sum += d*d;
		}
		
		return 1.0 - sum / common.size();
	}
	
	
	/**
	 * Calculating the MSDJ (MSD + Jaccard) measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return MSDJ measure between both two rating vectors and profiles.
	 */
	public double msdj(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return msd(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculate the Bhattacharyya measure from specified rating vectors. BC measure is modified by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return Bhattacharyya measure from specified rating vectors.
	 */
	protected double bc(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return bcAsUsual(vRating1, vRating2, profile1, profile2);
			}
		};
		
		return (double)cacheTask(vRating1.id(), vRating2.id(), this.bcColumnCache, task);
	}

	
	/**
	 * Calculate the Bhattacharyya measure from specified rating vectors as usual. BC measure is modified by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return Bhattacharyya measure from specified rating vectors.
	 */
	protected double bcAsUsual(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		List<Double> bins = this.valueBins;
		if (bins.isEmpty())
			bins = extractValueBins(vRating1, vRating2);
		
		Set<Integer> ids1 = vRating1.fieldIds(true);
		Set<Integer> ids2 = vRating2.fieldIds(true);
		int n1 = ids1.size();
		int n2 = ids2.size();
		if (n1 == 0 && n2 == 0) return 1;
		if (n1 == 0 || n2 == 0) return Constants.UNUSED;
		double bc = 0;
		for (double bin : bins) {
			int count1 = 0, count2 = 0;
			for (int id1 : ids1) {
				if (vRating1.get(id1).value == bin)
					count1++;
			}
			for (int id2 : ids2) {
				if (vRating2.get(id2).value == bin)
					count2++;
			}
			
			bc += Math.sqrt( ((double)count1/(double)n1) * ((double)count2/(double)n2) ); 
		}
		
		return bc;
	}

	
	/**
	 * Calculating the advanced BCF measure between two pairs. BCF measure is developed by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return BCF measure between both two rating vectors and profiles.
	 */
	public double bcf(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> columnIds1 = vRating1.fieldIds(true);
		Set<Integer> columnIds2 = vRating2.fieldIds(true);
		if (columnIds1.size() == 0 && columnIds2.size() == 0)
			return 1;
		if (columnIds1.size() == 0 || columnIds2.size() == 0)
			return Constants.UNUSED;
		
		double bcSum = 0;
		boolean medianMode = getConfig().getAsBoolean(BCF_MEDIAN_MODE_FIELD);
		for (int columnId1 : columnIds1) {
			RatingVector columnVector1 = bcfGetColumnRating(columnId1);
			if (columnVector1 == null) continue;
			double columnModule1 = bcfCalcColumnModule(columnVector1);
			if (!Util.isUsed(columnModule1) || columnModule1 == 0) continue;
			
			double value1 = medianMode? vRating1.get(columnId1).value-this.ratingMedian : vRating1.get(columnId1).value-vRating1.mean();
			for (int columnId2 : columnIds2) {
				RatingVector columnVector2 = columnId2 == columnId1 ? columnVector1 : bcfGetColumnRating(columnId2);
				if (columnVector2 == null) continue;
				double columnModule2 = bcfCalcColumnModule(columnVector2);
				if (!Util.isUsed(columnModule2) || columnModule2 == 0) continue;
				
				double bc = bc(columnVector1, columnVector2, profile1, profile2);
				if (!Util.isUsed(bc)) continue;

				double value2 = medianMode? vRating2.get(columnId2).value-this.ratingMedian : vRating2.get(columnId2).value-vRating2.mean();
				double loc = value1 * value2 / (columnModule1*columnModule2);
				if (!Util.isUsed(loc)) continue;
				
				bcSum += bc * loc;
			}
		}
		
		return bcSum;
	}

	
	/**
	 * Calculating the advanced BCFJ measure (BCF + Jaccard) between two pairs. BCF measure is developed by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return BCFJ measure between both two rating vectors and profiles.
	 */
	public double bcfj(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return bcf(vRating1, vRating2, profile1, profile2) + jaccard(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Getting rating vector given column ID (item ID or user ID) for BCF measure.
	 * @param columnId specified column ID (item ID or user ID).
	 * @return rating vector given column ID (item ID or user ID).
	 */
	protected abstract RatingVector bcfGetColumnRating(int columnId);
	
	
	/**
	 * Calculating module (length) of column rating vector for BCF measure.
	 * @param columnVector specified column rating vector.
	 * @return module (length) of column rating vector.
	 */
	protected double bcfCalcColumnModule(RatingVector columnVector) {
		double ratingMedian = this.ratingMedian;
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				if (columnVector == null) return Constants.UNUSED;
				
				Set<Integer> fieldIds = columnVector.fieldIds(true);
				double columnModule = 0;
				boolean medianMode = getConfig().getAsBoolean(BCF_MEDIAN_MODE_FIELD);
				for (int fieldId : fieldIds) {
					double deviate = medianMode ? columnVector.get(fieldId).value-ratingMedian : columnVector.get(fieldId).value;
					columnModule += deviate * deviate;
				}
				
				return Math.sqrt(columnModule);
			}
		};
		
		return (double)cacheTask(columnVector.id(), this.bcfColumnModuleCache, task);
	}

	
	@Override
	public Object cacheTask(int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object...params) {
		// TODO Auto-generated method stub
		return SupportCacheAlg.cacheTask(this, id1, id2, cache, task, params);
	}

	
	@Override
	public Object cacheTask(int id, Map<Integer, Object> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		return SupportCacheAlg.cacheTask(this, id, cache, task, params);
	}

	
	@Override
	public boolean isCached() {
		return getConfig().getAsBoolean(SupportCacheAlg.SUPPORT_CACHE_FIELD);
	}
	
	
	@Override
	public void setCached(boolean cached) {
		getConfig().put(SupportCacheAlg.SUPPORT_CACHE_FIELD, cached);
	}

	
	/**
	 * Computing common field IDs of two rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return common field IDs of two rating vectors.
	 */
	protected static Set<Integer> commonFieldIds(RatingVector vRating1, RatingVector vRating2) {
		if (vRating1 == null || vRating2 == null)
			return Util.newSet();
		
		Set<Integer> common = Util.newSet();
		common.addAll(vRating1.fieldIds(true));
		common.retainAll(vRating2.fieldIds(true));
		return common;
	}

	
	/**
	 * Computing union field IDs of two rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return common field IDs of two rating vectors.
	 */
	protected static Set<Integer> unionFieldIds(RatingVector vRating1, RatingVector vRating2) {
		if (vRating1 == null || vRating2 == null)
			return Util.newSet();
		
		Set<Integer> union = Util.newSet();
		union.addAll(vRating1.fieldIds(true));
		union.addAll(vRating2.fieldIds(true));
		return union;
	}

	
	/**
	 * Extracting value bins from two specified rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return Extracted value bins from two specified rating vectors.
	 */
	protected static List<Double> extractValueBins(RatingVector vRating1, RatingVector vRating2) {
		Set<Double> values = Util.newSet();
		
		Set<Integer> ids1 = vRating1.fieldIds(true);
		for (int id1 : ids1) {
			double value1 = vRating1.get(id1).value;
			values.add(value1);
		}
		
		Set<Integer> ids2 = vRating2.fieldIds(true);
		for (int id2 : ids2) {
			double value2 = vRating2.get(id2).value;
			values.add(value2);
		}
		
		List<Double> bins = DSUtil.toDoubleList(values);
		Collections.sort(bins);
		return bins;
	}
	
	
	/**
	 * Extracting value bins from configuration.
	 * @return extracted value bins from configuration.
	 */
	protected List<Double> extractConfigValueBins() {
		if (!getConfig().containsKey(BCF_VALUE_BINS_FIELD))
			return Util.newList();
		
		return TextParserUtil.parseListByClass(
				getConfig().getAsString(BCF_VALUE_BINS_FIELD),
				Double.class,
				",");
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(DataConfig.MIN_RATING_FIELD, DataConfig.MIN_RATING_DEFAULT);
		tempConfig.put(DataConfig.MAX_RATING_FIELD, DataConfig.MAX_RATING_DEFAULT);
		tempConfig.put(MEASURE, getDefaultSimilarMeasure());
		tempConfig.put(BCF_VALUE_BINS_FIELD, BCF_VALUE_BINS_DEFAULT);
		tempConfig.put(BCF_MEDIAN_MODE_FIELD, BCF_MEDIAN_MODE_DEFAULT);
		tempConfig.put(SUPPORT_CACHE_FIELD, SUPPORT_CACHE_DEFAULT);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				// TODO Auto-generated method stub
				if (key.equals(MEASURE)) {
					String measure = getAsString(MEASURE);
					measure = measure == null ? getDefaultSimilarMeasure() : measure;
					return (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one similar measure", 
							"Choosing similar measure", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							getSupportedSimilarMeasures().toArray(), 
							measure);
				}
				else 
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};

		config.putAll(tempConfig);
		return config;
	}


}
