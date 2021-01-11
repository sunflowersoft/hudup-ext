/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.nb;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.SupportCacheAlg;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.alg.cf.MemoryBasedCFAbstract;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.ui.ImportantProperty;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.Vector;

/**
 * This class sets up the nearest neighbors collaborative filtering algorithm. It is memory-based CF because it extends directly {@link MemoryBasedCF} class.
 * Neighbor CF algorithm finds out the nearest neighbor of active user (or active item).
 * Later on, Neighbor CF algorithm recommends items that the nearest neighbor likes (rates with high value) to the active user, with assumption that the nearest neighbor and the active user (active item) share the same interests.
 * <br>
 * The most important feature of any Neighbor CF algorithm is how to calculate the similarity between two rating vectors or between two profiles so as to find out the nearest neighbor.
 * The nearest neighbor is the one who has highest similar measure with the active user (active item).
 * This class provides the method {@link #sim(RatingVector, RatingVector, Profile, Profile, Object...)} to calculate the similarity measure between two pairs.
 * The first pair includes the first rating vector and the first profile.
 * The second pair includes the second rating vector and the second profile.
 * If you only want to calculate the similarity between two rating vectors, two in put profiles are set to be null.
 * If you only want to calculate the similarity between two profiles, two in put rating vectors are set to be null.
 * In current implementation, only three similarity measures are supported such as Pearson, cosine, and hybrid.
 * Hybrid measure means that profile is merged into rating vector as a unified vector for calculating Pearson measure or cosine measure.<br>
 * <br>
 * There are many authors who contributed measure to this class.<br>
 * Authors Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, and Fan Min contributed Triangle measure and TJM measure.<br>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class NeighborCF extends MemoryBasedCFAbstract implements SupportCacheAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The maximum number of nearest neighbors.
	 */
	protected static final String KNN = "knn";

	
	/**
	 * Default value of the maximum number of nearest neighbors.
	 */
	protected static final int KNN_DEFAULT = 0;

	
	/**
	 * Statistics calculation mode.
	 */
	protected static final String CALC_STATISTICS_FIELD = "calc_statistics";

	
	/**
	 * Default value for statistics calculation mode.
	 */
	protected static final boolean CALC_STATISTICS_DEFAULT = false;

	
	/**
	 * In the configuration, the entry of similarity measure has the name specified by this constant.
	 */
	protected static final String MEASURE = "measure";
	
	
	/**
	 * Name of hybrid measure.
	 */
	protected static final String HYBRID = "hybrid";

	
	/**
	 * Similarity threshold.
	 */
	protected static final String SIMILARITY_THRESHOLD_FIELD = "similarity_threshold";

	
	/**
	 * Default value for statistics calculation mode.
	 */
	protected static final String SIMILARITY_THRESHOLD_DEFAULT = "NaN";

	
	/**
	 * Cosine normalized mode.
	 */
	protected static final String COSINE_NORMALIZED_FIELD = "cosine_normalized";

	
	/**
	 * Default cosine normalized mode.
	 */
	protected static final boolean COSINE_NORMALIZED_DEFAULT = false;

	
	/**
	 * MSD fraction mode.
	 */
	protected static final String MSD_FRACTION_FIELD = "msd_fraction";

	
	/**
	 * Default MSD fraction mode.
	 */
	protected static final boolean MSD_FRACTION_DEFAULT = false;

	
	/**
	 * Threshold for WPCC (weighted Pearson correlation coefficient).
	 */
	protected static final double WPC_THRESHOLD = 50;

	
	/**
	 * General rating median.
	 */
	protected double ratingMedian = DataConfig.RELEVANT_RATING_DEFAULT;

	
	/**
	 * General rating mean.
	 */
	protected double ratingMean = Constants.UNUSED;

	
	/**
	 * General user variance.
	 */
	protected double ratingVar = Constants.UNUSED;

	
	/**
	 * Internal item identifiers.
	 */
	protected Set<Integer> userIds = Util.newSet();

	
	/**
	 * Internal user means.
	 */
	protected Map<Integer, Double> userMeans = Util.newMap();

	
	/**
	 * Internal user variances.
	 */
	protected Map<Integer, Double> userVars = Util.newMap();

	
	/**
	 * Internal item identifiers.
	 */
	protected Set<Integer> itemIds = Util.newSet();

	
	/**
	 * Internal item means.
	 */
	protected Map<Integer, Double> itemMeans = Util.newMap();

	
	/**
	 * Internal item variances.
	 */
	protected Map<Integer, Double> itemVars = Util.newMap();
	
	
//	/**
//	 * User rating cache (user id, item id, rating value).
//	 */
//	protected Map<Integer, Map<Integer, Object>> userRatingCache = Util.newMap();
	
	
	/**
	 * Row similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> rowSimCache = Util.newMap();


	/**
	 * Column similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> columnSimCache = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public NeighborCF() {
		updateConfig(getMeasure());
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		super.setup(dataset, params);
		
		if (getConfig().getAsBoolean(CALC_STATISTICS_FIELD)) {
			updateUserMeanVars(dataset);
			updateItemMeanVars(dataset);
		}

		this.ratingMedian = getRelevantRatingThreshold();
		if (!Util.isUsed(this.ratingMedian) && Util.isUsed(this.ratingMean))
			this.ratingMedian = this.ratingMean;
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		this.ratingMedian = DataConfig.RELEVANT_RATING_DEFAULT;
		
		this.ratingMean = Constants.UNUSED;
		this.ratingVar = Constants.UNUSED;
		this.userMeans.clear();
		this.userVars.clear();
		
		this.itemIds.clear();
		this.itemMeans.clear();
		this.itemVars.clear();
		this.userIds.clear();
		
//		this.userRatingCache.clear();
		this.rowSimCache.clear();
		this.columnSimCache.clear();
	}


	/**
	 * Updating individual user means and variances from specified dataset.
	 * @param dataset specified dataset.
	 * @throws RemoteException if any error raises.
	 */
	private void updateUserMeanVars(Dataset dataset) throws RemoteException {
		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		this.ratingVar = 0.0;
		this.userIds.clear();
		this.userMeans.clear();
		this.userVars.clear();
		
		//Calculating user means
		Fetcher<RatingVector> users = dataset.fetchUserRatings();
		while (users.next()) {
			RatingVector user = users.pick();
			if (user == null) continue;
			
			int userId = user.id();
			this.userIds.add(userId);
			
			Set<Integer> itemIds = user.fieldIds(true);
			double meanSum = 0;
			for (int itemId : itemIds) {
				double value = user.get(itemId).value;
				meanSum += value;
				
				totalRatingCount++;
				this.ratingMean += value;
			}
			this.userMeans.put(userId, meanSum / (double)(itemIds.size()));
		}
		this.ratingMean = this.ratingMean / (double)totalRatingCount;

		//Calculating user variances
		users.reset();
		while (users.next()) {
			RatingVector user = users.pick();
			if (user == null) continue;
		
			Set<Integer> itemIds = user.fieldIds(true);
			int userId = user.id();
			double varSum = 0;
			double userMean = this.userMeans.get(userId);
			for (int itemId : itemIds) {
				double value = user.get(itemId).value;
				double d = value - userMean;
				varSum += d*d;
				
				double D = value - this.ratingMean;
				this.ratingVar += D*D;
			}
			this.userVars.put(userId, varSum / (double)(itemIds.size()));
		}
		this.ratingVar = this.ratingVar / (double)totalRatingCount;
		
		users.close();
	}

	
	/**
	 * Updating individual item means and variances from specified dataset.
	 * @param dataset specified dataset.
	 * @throws RemoteException if any error raises.
	 */
	private void updateItemMeanVars(Dataset dataset) throws RemoteException {
		this.itemIds.clear();
		this.itemMeans.clear();
		this.itemVars.clear();
		
		//Calculating item means
		Fetcher<RatingVector> items = dataset.fetchItemRatings();
		while (items.next()) {
			RatingVector item = items.pick();
			if (item == null) continue;
			
			int itemId = item.id();
			this.itemIds.add(itemId);
			
			Set<Integer> userIds = item.fieldIds(true);
			double meanSum = 0;
			for (int userId : userIds) {
				double value = item.get(userId).value;
				meanSum += value;
			}
			this.itemMeans.put(itemId, meanSum / (double)(userIds.size()));
		}

		//Calculating user variances
		items.reset();
		while (items.next()) {
			RatingVector item = items.pick();
			if (item == null) continue;
		
			Set<Integer> userIds = item.fieldIds(true);
			int itemId = item.id();
			double varSum = 0;
			double itemMean = this.itemMeans.get(itemId);
			for (int userId : userIds) {
				double value = item.get(userId).value;
				double d = value - itemMean;
				varSum += d*d;
			}
			this.itemVars.put(itemId, varSum / (double)(userIds.size()));
		}
		
		items.close();
	}
	
	
	/**
	 * Getting the list of all similar measures in names.
	 * @return all similar measures.
	 */
	public List<String> getAllMeasures() {
		Set<String> mSet = Util.newSet();
		mSet.addAll(getMainMeasures());
		mSet.add(Measure.COSINEJ);
		mSet.add(Measure.PEARSONJ);
		mSet.add(Measure.MSDJ);
		mSet.add(Measure.TJM);
		
		List<String> measures = Util.newList();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}
	
	
	/**
	 * Getting the list of main similar measures in names.
	 * @return main similar measures.
	 */
	public List<String> getMainMeasures() {
		Set<String> mSet = Util.newSet();
		mSet.add(Measure.COSINE);
		mSet.add(Measure.COJ);
		mSet.add(Measure.PEARSON);
		mSet.add(Measure.COD);
		mSet.add(Measure.CPC);
		mSet.add(Measure.WPC);
		mSet.add(Measure.SPC);
		mSet.add(Measure.JACCARD);
		mSet.add(Measure.JACCARD2);
		mSet.add(Measure.MSD);
		mSet.add(Measure.URP);
		mSet.add(Measure.TRIANGLE);
		
		List<String> measures = Util.newList();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}

	
	/**
	 * Testing whether the specified measure is supported.
	 * @param measure specified measure.
	 * @return whether the specified measure is supported.
	 */
	public boolean isSupportedMeasure(String measure) {
		if (measure == null) return false;
		return getAllMeasures().contains(measure);
	}
	
	
	/**
	 * Getting the default similarity measure.
	 * @return default similar measure.
	 */
	protected String getDefaultMeasure() {
		return Measure.COSINE;
	}

	
	/**
	 * Getting the similarity measure.
	 * @return similar measure.
	 */
	public String getMeasure() {
		String measure = config.getAsString(MEASURE);
		if (measure == null)
			return getDefaultMeasure();
		else
			return measure;
	}
	
	
	/**
	 * Setting the similarity measure.
	 * @param measure the similarity measure.
	 */
	public synchronized void setMeasure(String measure) {
		config.put(MEASURE, measure);
		updateConfig(measure);
	}
	
	
	/**
	 * Checking whether the supported similarity can be cached. In some case, the algorithm is cached but the similarity measure is not cached.
	 * In current version of this class, the method always returns true.
	 * @return true if the supported similarity can be cached.
	 */
	protected boolean isCachedSim() {
		return true;
	}
	
	
	/**
	 * Calculating the similarity between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * If you only want to calculate the similarity between two rating vectors, two in put profiles are set to be null.
	 * If you only want to calculate the similarity between two profiles, two in put rating vectors are set to be null.
	 * In current implementation, only three similarity measures are supported such as Pearson, cosine, and hybrid.
	 * Hybrid measure means that profile is merged into rating vector as a unified vector for calculating Pearson measure or cosine measure.
	 * In current implementation, hybrid measure is not supported.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @param parameters extra parameters.
	 * @return similarity between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public synchronized double sim(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object...parameters) {
		String measure = getMeasure();
		if (!isCachedSim()) //In some case, the algorithm is cached but the similarity measure is not cached.
			return sim0(measure, vRating1, vRating2, profile1, profile2, parameters);
		
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return sim0(measure, vRating1, vRating2, profile1, profile2, parameters);
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
	 * @param params extra parameter.
	 * @return similarity between both two {@link RatingVector} (s) and two {@link Profile} (s) as usual.
	 */
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object...params) {
//		boolean hybrid = config.getAsBoolean(HYBRID);
//		if (!hybrid) {
//			profile1 = null;
//			profile2 = null;
//		}
		
		if (measure.equals(Measure.COSINE))
			return cosine(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.COSINEJ))
			return cosine(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.COJ))
			return coj(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.PEARSON))
			return corr(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.PEARSONJ))
			return corr(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.COD))
			return cod(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.CPC))
			return cpc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.WPC))
			return wpc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SPC))
			return spc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.JACCARD))
			return jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.JACCARD2))
			return jaccard2(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.MSD))
			return msd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.MSDJ))
			return msd(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.URP))
			return urp(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.TRIANGLE))
			return triangle(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.TJM))
			return triangle(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else
			return Constants.UNUSED;
	}
	
	
	/**
	 * Updating configuration according to specified measure.
	 * @param measure specified measure.
	 */
	protected void updateConfig(String measure) {
		if (config == null || measure == null) return;
		
		config.removeReadOnly(CALC_STATISTICS_FIELD);
		config.removeReadOnly(COSINE_NORMALIZED_FIELD);
		config.removeReadOnly(MSD_FRACTION_FIELD);
		if (measure.equals(Measure.COSINE)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.COSINEJ)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.COJ)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.PEARSON)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.PEARSONJ)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.COD)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.CPC)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.WPC)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.SPC)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.JACCARD)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.JACCARD2)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.MSD)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.MSDJ)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.URP)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.TRIANGLE)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
		else if (measure.equals(Measure.TJM)) {
			config.put(CALC_STATISTICS_FIELD, false);
			config.addReadOnly(CALC_STATISTICS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
		}
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
	@NextUpdate
	protected double cosine(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		boolean normalized = getConfig().getAsBoolean(COSINE_NORMALIZED_FIELD);
		return normalized ? vRating1.cosine(vRating2, this.ratingMedian) : vRating1.cosine(vRating2);

//		boolean normalized = getConfig().getAsBoolean(COSINE_NORMALIZED_FIELD);
//		if (profile1 == null || profile2 == null)
//			return normalized ? vRating1.cosine(vRating2, ratingMedian) : vRating1.cosine(vRating2);
//		
//		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
//		Vector vector1 = vectors[0];
//		Vector vector2 = vectors[1];
//		
//		return normalized ? vector1.cosine(vector2, this.ratingMedian) : vector1.cosine(vector2);
	}

	
	/**
	 * Calculating the COD (adjusted cosine) measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return ACOS measure between both two rating vectors.
	 */
	protected abstract double cod(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2);
	

	/**
	 * Calculating the COD (adjusted cosine) measure between two rating vectors.
	 * @param vRating1 the first rating vectors.
	 * @param vRating2 the second rating vectors.
	 * @param fieldMeans mean value of field ratings.
	 * @return ACOS (adjusted cosine) measure between two rating vectors.
	 */
	protected double cod(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
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
		
		if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
	}

	
	/**
	 * Calculating the Cosine-Jaccard measure between two pairs. Cosine-Jaccard is developed by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen
	 * @return Cosine-Jaccard measure between both two rating vectors and profiles.
	 */
	protected double coj(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> union = unionFieldIds(vRating1, vRating2);
		
		boolean normalized = getConfig().getAsBoolean(COSINE_NORMALIZED_FIELD);
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int fieldId : union) {
			double deviate1 = Constants.UNUSED;
			double deviate2 = Constants.UNUSED;
			if (vRating1.isRated(fieldId))
				deviate1 = vRating1.get(fieldId).value - (normalized ? this.ratingMedian : 0);
			if (vRating2.isRated(fieldId))
				deviate2 = vRating2.get(fieldId).value - (normalized ? this.ratingMedian : 0);
			
			if (Util.isUsed(deviate1))
				VX  += deviate1 * deviate1;
			if (Util.isUsed(deviate2))
				VY  += deviate2 * deviate2;
			if (Util.isUsed(deviate1) && Util.isUsed(deviate2))
				VXY += deviate1 * deviate2;
		}
		
		if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
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
	@NextUpdate
	protected double corr(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		return vRating1.corr(vRating2);
		
//		if (profile1 == null || profile2 == null)
//			return vRating1.corr(vRating2);
//
//		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
//		Vector vector1 = vectors[0];
//		Vector vector2 = vectors[1];
//		
//		return vector1.corr(vector2);
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
	@SuppressWarnings("unused")
	@Deprecated
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
	@Deprecated
	private Vector[] toNormVector(RatingVector vRating1, RatingVector vRating2) {
		
		List<Integer> common = Util.newList();
		common.addAll(vRating1.fieldIds(true));
		common.retainAll(vRating2.fieldIds(true));
		if (common.size() == 0)
			return new Vector[0];
		
		double n = getMaxRating() - getMinRating();
		
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
	@Deprecated
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
	 * Calculating the CPC (constrained Pearson correlation) measure between two pairs.
	 * It is also cosine normalized (CON) measure.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return CPC measure between both two rating vectors.
	 */
	protected double cpc(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return vRating1.cosine(vRating2, this.ratingMedian);
	}
	
	
	/**
	 * Calculating the WPC (weighted Pearson correlation) measure between two pairs. SMTP is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return WPCC measure between both two rating vectors.
	 */
	protected double wpc(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		double N = common.size();
		if (N <= WPC_THRESHOLD)
			return corr(vRating1, vRating2, profile1, profile2) * (N/WPC_THRESHOLD);
		else
			return corr(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the WPC (weighted Pearson correlation) measure between two pairs. SMTP is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return WPC measure between both two rating vectors.
	 */
	protected double spc(
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
	protected double jaccard(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		Set<Integer> union = unionFieldIds(vRating1, vRating2);
		if (union.size() == 0)
			return Constants.UNUSED;
		else
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
	protected double jaccard2(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> ratedIds1 = vRating1.fieldIds(true);
		Set<Integer> ratedIds2 = vRating2.fieldIds(true);
		if (ratedIds1.size() == 0 || ratedIds2.size() == 0)
			return Constants.UNUSED;
		
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		return (double)common.size() / (double)(ratedIds1.size()*ratedIds2.size());
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
	protected double msd(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double sum = 0;
		for (int id : common) {
			double d = (vRating1.get(id).value - vRating2.get(id).value);
			sum += d*d;
		}
		
		boolean fraction = config.getAsBoolean(MSD_FRACTION_FIELD);
		if (fraction)
			return 1 / (1 + sum/common.size());
		else {
			double maxRating = getMaxRating();
			return 1.0 - sum/(common.size()*maxRating*maxRating);
		}
	}
	
	
	/**
	 * Calculating the URP measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return URP measure between both two rating vectors and profiles.
	 */
	protected double urp(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double mean1 = vRating1.mean();
		double var1 = vRating1.mleVar();
		double mean2 = vRating2.mean();
		double var2 = vRating2.mleVar();
		
		return 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs(mean1-mean2)*Math.abs(var1-var2)));
	}

	
	/**
	 * Calculating the Triangle measure between two pairs.
	 * Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, and Fan Min developed the Triangle measure. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, Fan Min
	 * @return Triangle measure between both two rating vectors and profiles.
	 */
	protected double triangle(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return 1 - vRating1.distance(vRating2) / (vRating1.module()+vRating2.module());
	}
	
	
	@Override
	public Object cacheTask(int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object...params) {
		return SupportCacheAlg.cacheTask(this, id1, id2, cache, task, params);
	}

	
	@Override
	public Object cacheTask(int id, Map<Integer, Object> cache, Task task, Object... params) {
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
	 * Getting set of row identifiers.
	 * @return set of row identifiers.
	 */
	protected abstract Set<Integer> getRowIds();

	
	/**
	 * Getting rating vector given row ID (item ID or user ID).
	 * @param rowId specified row ID (item ID or user ID).
	 * @return rating vector given row ID (item ID or user ID).
	 */
	protected abstract RatingVector getRowRating(int rowId);

	
	/**
	 * Calculating mean of row rating vector.
	 * @param vRating specified row rating vector.
	 * @return mean of row rating vector.
	 */
	protected abstract double calcRowMean(RatingVector vRating);

	
	/**
	 * Getting set of column identifiers.
	 * @return set of column identifiers.
	 */
	protected abstract Set<Integer> getColumnIds();

	
	/**
	 * Getting rating vector given column ID (item ID or user ID).
	 * @param columnId specified column ID (item ID or user ID).
	 * @return rating vector given column ID (item ID or user ID).
	 */
	protected abstract RatingVector getColumnRating(int columnId);

	
	/**
	 * Calculating mean of column rating vector.
	 * @param vRating specified column rating vector.
	 * @return mean of column rating vector.
	 */
	protected abstract double calcColumnMean(RatingVector vRating);
	
	
	/**
	 * Computing common field IDs of two rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return common field IDs of two rating vectors.
	 */
	protected static Set<Integer> commonFieldIds(RatingVector vRating1, RatingVector vRating2) {
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
		Set<Integer> union = Util.newSet();
		union.addAll(vRating1.fieldIds(true));
		union.addAll(vRating2.fieldIds(true));
		return union;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(SUPPORT_CACHE_FIELD, SUPPORT_CACHE_DEFAULT);
		tempConfig.put(KNN, KNN_DEFAULT);
		tempConfig.put(CALC_STATISTICS_FIELD, CALC_STATISTICS_DEFAULT);
		tempConfig.put(MEASURE, getDefaultMeasure()); tempConfig.addReadOnly(MEASURE);
		tempConfig.put(HYBRID, false); tempConfig.addInvisible(HYBRID);
		tempConfig.put(SIMILARITY_THRESHOLD_FIELD, SIMILARITY_THRESHOLD_DEFAULT);
		tempConfig.put(COSINE_NORMALIZED_FIELD, COSINE_NORMALIZED_DEFAULT);
		tempConfig.put(MSD_FRACTION_FIELD, MSD_FRACTION_DEFAULT);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				if (key.equals(MEASURE)) {
					String measure = getAsString(MEASURE);
					measure = measure == null ? getDefaultMeasure() : measure;
					Serializable value = (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one similar measure", 
							"Choosing similar measure", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							getMainMeasures().toArray(), 
							measure);
					
					if (value == null) return null;
					
					int confirm = JOptionPane.showConfirmDialog(
							comp, 
							"Changing important property requires immediate appliance.\nAre you sure?", 
							"Attributes are modified",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (confirm == JOptionPane.YES_OPTION) {
						updateConfig(value.toString());
						return new ImportantProperty(value);
					}
					else
						return value;
				}
				else 
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};

		config.putAll(tempConfig);
		
		return config;
	}


	/**
	 * Getting similarity threshold.
	 * @param config specified configuration.
	 * @return similarity threshold.
	 */
	protected static double getSimThreshold(DataConfig config) {
		try {
			String thresholdText = config.getAsString(SIMILARITY_THRESHOLD_FIELD);
			if (thresholdText == null) return Constants.UNUSED;
			thresholdText = thresholdText.trim().toLowerCase();
			if (thresholdText.isEmpty() || thresholdText.equals("nan"))
				return Constants.UNUSED;
			else
				return config.getAsReal(SIMILARITY_THRESHOLD_FIELD);
		}
		catch (Throwable e) {}
		
		return Constants.UNUSED;
	}
	
	
	/**
	 * Getting mean of rating vector.
	 * @param cf nearest neighbors algorithm.
	 * @param means map of means.
	 * @param vRating specified rating vector. It can be null.
	 * @return mean of rating vector.
	 */
	protected static double calcMean(NeighborCF cf, Map<Integer, Double> means, RatingVector vRating) {
		if (means == null && vRating == null) return Constants.UNUSED;
		if (means == null) return vRating.mean();
		
		Integer id = vRating.id();
		if (means.containsKey(id))
			return means.get(id);
		else if (cf == null || !cf.isCached())
			return vRating.mean();
		else {
			double mean = vRating.mean();
			if (id >= 0) means.put(id, mean);
			return mean;
		}
	}
	
	
}
