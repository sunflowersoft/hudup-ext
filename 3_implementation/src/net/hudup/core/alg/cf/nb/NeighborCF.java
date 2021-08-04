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
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.Vector;
import net.hudup.core.parser.TextParserUtil;

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
 * <br>
 * Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, and Fan Min contributed Triangle measure and TJM measure.<br>
 * <br>
 * Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, Mayda Abdullateef Alrige contributed measures RDP, IPC, and IPWR.<br>
 * <br>
 * Vijay Verma and Rajesh Kumar Aggarwal contributed SMCC measure.<br>
 * <br>
 * Achraf Gazdar and Lotfi Hidri contributed Absolute Difference of Ratings (ADR) measure and OS measure.<br>
 * <br>
 * Ling-Jiao Chen, Zi-Ke Zhang, Jin-Hu Liu, Jian Gao, and Tao Zhou contributed resource-allocation (RA) measure..<br>
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
	protected static final int KNN_DEFAULT = 100;

	
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
	 * Entropy support mode.
	 */
	protected static final String ENTROPY_SUPPORT_FIELD = "entropy_support";

	
	/**
	 * Default value for entropy support mode.
	 */
	protected static final boolean ENTROPY_SUPPORT_DEFAULT = false;

	
	/**
	 * Cosine type.
	 */
	protected static final String COSINE_TYPE = "cosine_type";

	
	/**
	 * Normal cosine.
	 */
	protected static final String COSINE_TYPE_NORMAL = "cosine";
	
	
	/**
	 * Adjusted cosine.
	 */
	protected static final String COSINE_TYPE_ADJUSTED = "cod";

	
	/**
	 * Jaccard-like cosine.
	 */
	protected static final String COSINE_TYPE_JACCARD_LIKE = "coj";

	
	/**
	 * Jaccard cosine.
	 */
	protected static final String COSINE_TYPE_JACCARD = "cosinej";

	
	/**
	 * Cosine normalized mode.
	 */
	protected static final String COSINE_NORMALIZED_FIELD = "cosine_normalized";

	
	/**
	 * Default cosine normalized mode.
	 */
	protected static final boolean COSINE_NORMALIZED_DEFAULT = false;

	
	/**
	 * Cosine weighted mode.
	 */
	protected static final String COSINE_WEIGHTED_FIELD = "cosine_weighted";

	
	/**
	 * Default weighted mode.
	 */
	protected static final boolean COSINE_WEIGHTED_DEFAULT = false;

	
	/**
	 * Resource-allocation (RA) for cosine.
	 */
	protected static final String COSINE_RA_FIELD = "cosine_ra";
	
	
	/**
	 * Default value of resource-allocation (RA) for cosine.
	 */
	protected static final boolean COSINE_RA_DEFAULT = false;
	
	
	/**
	 * MSD type.
	 */
	protected static final String MSD_TYPE = "msd_type";

	
	/**
	 * Normal MSD.
	 */
	protected static final String MSD_TYPE_NORMAL = "msd";
	
	
	/**
	 * Jaccard MSD.
	 */
	protected static final String MSD_TYPE_JACCARD = "msdj";

	
	/**
	 * MSD fraction mode.
	 */
	protected static final String MSD_FRACTION_FIELD = "msd_fraction";

	
	/**
	 * Default MSD fraction mode.
	 */
	protected static final boolean MSD_FRACTION_DEFAULT = false;

	
	/**
	 * Pearson type.
	 */
	protected static final String PEARSON_TYPE = "pearson_type";

	
	/**
	 * Normal Pearson.
	 */
	protected static final String PEARSON_TYPE_NORMAL = "pearson";
	
	
	/**
	 * Jaccard Pearson.
	 */
	protected static final String PEARSON_TYPE_JACCARD = "pearsonj";

	
	/**
	 * Constrained Pearson.
	 */
	protected static final String PEARSON_TYPE_CPC = "cpc";

	
	/**
	 * Weighted Pearson.
	 */
	protected static final String PEARSON_TYPE_WPC = "wpc";

	
	/**
	 * Sigmoid Pearson.
	 */
	protected static final String PEARSON_TYPE_SPC = "spc";

	
	/**
	 * IPC.
	 */
	protected static final String PEARSON_TYPE_IPC = "ipc";

	
	/**
	 * Resource-allocation (RA) Pearson.
	 */
	protected static final String PEARSON_TYPE_RA = "ra";

	
	/**
	 * Pearson weighted mode.
	 */
	protected static final String PEARSON_WEIGHTED_FIELD = "pearson_weighted";

	
	/**
	 * Default Pearson weighted mode.
	 */
	protected static final boolean PEARSON_WEIGHTED_DEFAULT = false;

	
	/**
	 * Resource-allocation (RA) for Pearson.
	 */
	protected static final String PEARSON_RA_FIELD = "pearson_ra";
	
	
	/**
	 * Default value of resource-allocation (RA) for Pearson.
	 */
	protected static final boolean PEARSON_RA_DEFAULT = false;
	
	
	/**
	 * Threshold for WPCC (weighted Pearson correlation coefficient).
	 */
	protected static final double WPC_THRESHOLD = 50;

	
	/**
	 * Jaccard type.
	 */
	protected static final String JACCARD_TYPE = "jaccard_type";

	
	/**
	 * Normal Jaccard.
	 */
	protected static final String JACCARD_TYPE_NORMAL = "normal";
	
	
	/**
	 * Multiplication Jaccard.
	 */
	protected static final String JACCARD_TYPE_MULTI = "multi";

	
	/**
	 * Dice Jaccard.
	 */
	protected static final String JACCARD_TYPE_DICE = "dice";

	
	/**
	 * Percentage of Non Common Ratings (PNCR).
	 */
	protected static final String JACCARD_TYPE_PNCR = "pncr";

	
	/**
	 * Relevant Jaccard (IJ).
	 */
	public static final String JACCARD_TYPE_RJ = "rj";

	
	/**
	 * Rating Jaccard.
	 */
	public static final String JACCARD_TYPE_RATINGJ = "ratingj";

	
	/**
	 * Indexed Jaccard.
	 */
	public static final String JACCARD_TYPE_INDEXEDJ = "indexedj";

	
	/**
	 * Threshold of rating relevant measure.
	 */
	protected static final String RATINGJ_THRESHOLD_FIELD = "jaccard_ratingj_threshold";

	
	/**
	 * Default value for the threshold of rating relevant measure..
	 */
	protected static final double RATINGJ_THRESHOLD_DEFAULT = 0.1;

	
	/**
	 * Intervals of indexed Jaccard measures.
	 */
	protected static final String INDEXEDJ_INTERVALS_FIELD = "jaccard_indexedj_intervals";

	
	/**
	 * Default value for intervals of indexed Jaccard measures.
	 */
	protected static final String INDEXEDJ_INTERVALS_DEFAULT = "2, 4";

	
	/**
	 * Triangle type.
	 */
	protected static final String TRIANGLE_TYPE = "triangle_type";

	
	/**
	 * Normal triangle.
	 */
	protected static final String TRIANGLE_TYPE_NORMAL = "triangle";
	
	
	/**
	 * TJM.
	 */
	protected static final String TRIANGLE_TYPE_TJM = "tjm";

	
	/**
	 * IPWR alpha field.
	 */
	protected static final String IPWR_ALPHA_FIELD = "ipwr_alpha";

	
	/**
	 * Default IPWR alpha.
	 */
	protected static final double IPWR_ALPHA_DEFAULT = 0.5;

	
	/**
	 * IPWR beta field.
	 */
	protected static final String IPWR_BETA_FIELD = "ipwr_beta";

	
	/**
	 * Default IPWR beta.
	 */
	protected static final double IPWR_BETA_DEFAULT = 0.5;

	
	/**
	 * General rating median.
	 */
	protected double ratingMedian = DataConfig.RELEVANT_RATING_DEFAULT;

	
	/**
	 * General rating mean.
	 */
	private double ratingMean = Constants.UNUSED;

	
	/**
	 * General user variance.
	 */
	private double ratingVar = Constants.UNUSED;

	
	/**
	 * Internal item identifiers.
	 */
	private Set<Integer> userIds = Util.newSet();

	
	/**
	 * Internal user means.
	 */
	private Map<Integer, Double> userMeans = Util.newMap();

	
	/**
	 * Internal user variances.
	 */
	private Map<Integer, Double> userVars = Util.newMap();

	
	/**
	 * Internal item identifiers.
	 */
	private Set<Integer> itemIds = Util.newSet();

	
	/**
	 * Internal item means.
	 */
	private Map<Integer, Double> itemMeans = Util.newMap();

	
	/**
	 * Internal item variances.
	 */
	private Map<Integer, Double> itemVars = Util.newMap();
	
	
	/**
	 * Row similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> rowSimCache = Util.newMap();


	/**
	 * Column similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> columnSimCache = Util.newMap();

	
	/**
	 * Value cache.
	 */
	protected Map<Integer, Object> valueCache = Util.newMap();

	
	/**
	 * Dual value cache.
	 */
	protected Map<Integer, Object> dualValueCache = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public NeighborCF() {
		updateConfig(getMeasure());
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		super.setup(dataset, params);
		
		this.ratingMedian = getRelevantRatingThreshold();
		if (!Util.isUsed(this.ratingMedian)) this.ratingMedian = getRatingMean();
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
		this.valueCache.clear();
		this.dualValueCache.clear();
	}


	/**
	 * Getting rating mean.
	 * @return rating mean.
	 */
	protected double getRatingMean() {
		if (Util.isUsed(this.ratingMean)) return this.ratingMean;
		
		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		try {
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				Set<Integer> itemIds = user.fieldIds(true);
				if (itemIds.size() == 0) continue;
				
				for (int itemId : itemIds) {
					this.ratingMean += user.get(itemId).value;
					totalRatingCount++;
				}
			}
			if (totalRatingCount != 0)
				this.ratingMean = this.ratingMean / (double)totalRatingCount;
			users.close();
		} catch (Throwable e) {LogUtil.trace(e);}

		return ratingMean;
	}
	
	
	/**
	 * Getting rating variance.
	 * @return rating variance.
	 */
	protected double getRatingVar() {
		if (Util.isUsed(this.ratingVar)) return this.ratingVar;

		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		this.ratingVar = 0.0;
		try {
			//Calculating user means
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				Set<Integer> itemIds = user.fieldIds(true);
				if (itemIds.size() == 0) continue;
				
				for (int itemId : itemIds) {
					this.ratingMean += user.get(itemId).value;
					totalRatingCount++;
				}
			}
			if (totalRatingCount != 0)
				this.ratingMean = this.ratingMean / (double)totalRatingCount;
	
			//Calculating user variances
			users.reset();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				Set<Integer> itemIds = user.fieldIds(true);
				if (itemIds.size() == 0) continue;
				
				for (int itemId : itemIds) {
					double D = user.get(itemId).value - this.ratingMean;
					this.ratingVar += D*D;
				}
			}
			if (totalRatingCount != 0)
				this.ratingVar = this.ratingVar / (double)totalRatingCount;
			users.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		

		if (userVars.size() == 0) getUserVars();
		return ratingVar;
	}

	
	/**
	 * Getting user identifiers.
	 * @return set of user identifiers.
	 */
	protected Set<Integer> getUserIds() {
		if (this.userIds.size() > 0) return this.userIds;
		
		this.userIds.clear();
		try {
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user != null && user.fieldIds(true).size() > 0)
					this.userIds.add(user.id());
			}
			users.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		return this.userIds;
	}
	
	
	/**
	 * Getting user means.
	 * @return user means.
	 */
	protected Map<Integer, Double> getUserMeans() {
		if (this.userMeans.size() > 0) return this.userMeans;
		
		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		this.userIds.clear();
		this.userMeans.clear();
		try {
			//Calculating user means
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				Set<Integer> itemIds = user.fieldIds(true);
				if (itemIds.size() == 0) continue;
				
				int userId = user.id();
				this.userIds.add(userId);
				
				double meanSum = 0;
				for (int itemId : itemIds) {
					double value = user.get(itemId).value;
					meanSum += value;
					
					totalRatingCount++;
					this.ratingMean += value;
				}
				this.userMeans.put(userId, meanSum / (double)(itemIds.size()));
			}
			if (totalRatingCount != 0)
				this.ratingMean = this.ratingMean / (double)totalRatingCount;
			users.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		return this.userMeans;
	}
	
	
	/**
	 * Calculating user mean.
	 * @param vRating user rating vector.
	 * @return user mean.
	 */
	protected double calcUserMean(RatingVector vRating) {
		return calcMean(this, userMeans, vRating);
	}
	
	
	/**
	 * Getting user variances from specified dataset.
	 * @return user variances.
	 */
	protected Map<Integer, Double> getUserVars() {
		if (this.userVars.size() > 0) return this.userVars;
		
		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		this.ratingVar = 0.0;
		this.userIds.clear();
		this.userMeans.clear();
		this.userVars.clear();
		try {
			//Calculating user means
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				Set<Integer> itemIds = user.fieldIds(true);
				if (itemIds.size() == 0) continue;
				
				int userId = user.id();
				this.userIds.add(userId);
				
				double meanSum = 0;
				for (int itemId : itemIds) {
					double value = user.get(itemId).value;
					meanSum += value;
					
					totalRatingCount++;
					this.ratingMean += value;
				}
				this.userMeans.put(userId, meanSum / (double)(itemIds.size()));
			}
			if (totalRatingCount != 0)
				this.ratingMean = this.ratingMean / (double)totalRatingCount;
	
			//Calculating user variances
			users.reset();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				Set<Integer> itemIds = user.fieldIds(true);
				if (itemIds.size() == 0) continue;
				
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
			if (totalRatingCount != 0)
				this.ratingVar = this.ratingVar / (double)totalRatingCount;
			users.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return this.userVars;
	}

	
	/**
	 * Getting item identifiers.
	 * @return set of item identifiers.
	 */
	protected Set<Integer> getItemIds() {
		if (this.itemIds.size() > 0) return this.itemIds;
		
		this.itemIds.clear();
		try {
			Fetcher<RatingVector> items = dataset.fetchItemRatings();
			while (items.next()) {
				RatingVector item = items.pick();
				if (item != null && item.fieldIds(true).size() > 0) 
					this.itemIds.add(item.id());
			}
			items.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		return this.itemIds;
	}

	
	/**
	 * Getting item means.
	 * @return item means.
	 */
	protected Map<Integer, Double> getItemMeans() {
		if (this.itemMeans.size() > 0) return this.itemMeans;

		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		this.itemIds.clear();
		this.itemMeans.clear();
		try {
			//Calculating item means
			Fetcher<RatingVector> items = dataset.fetchItemRatings();
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				Set<Integer> userIds = item.fieldIds(true);
				if (userIds.size() == 0) continue;
				
				int itemId = item.id();
				this.itemIds.add(itemId);
				
				double meanSum = 0;
				for (int userId : userIds) {
					double value = item.get(userId).value;
					meanSum += value;
					
					totalRatingCount++;
					this.ratingMean += value;
				}
				this.itemMeans.put(itemId, meanSum / (double)(userIds.size()));
			}
			if (totalRatingCount != 0)
				this.ratingMean = this.ratingMean / (double)totalRatingCount;
			items.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		return this.itemMeans;
	}
	
	
	/**
	 * Calculating item mean.
	 * @param vRating item rating vector.
	 * @return item mean.
	 */
	protected double calcItemMean(RatingVector vRating) {
		return calcMean(this, itemMeans, vRating);
	}
	
	
	/**
	 * Getting item variances from specified dataset.
	 * @return item variances.
	 */
	protected Map<Integer, Double> getItemVars() {
		if (this.itemVars.size() > 0) return this.itemVars;

		int totalRatingCount = 0;
		this.ratingMean = 0.0;
		this.ratingVar = 0.0;
		this.itemIds.clear();
		this.itemMeans.clear();
		this.itemVars.clear();
		try {
			//Calculating item means
			Fetcher<RatingVector> items = dataset.fetchItemRatings();
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				Set<Integer> userIds = item.fieldIds(true);
				if (userIds.size() == 0) continue;
				
				int itemId = item.id();
				this.itemIds.add(itemId);
				
				double meanSum = 0;
				for (int userId : userIds) {
					double value = item.get(userId).value;
					meanSum += value;
					
					totalRatingCount++;
					this.ratingMean += value;
				}
				this.itemMeans.put(itemId, meanSum / (double)(userIds.size()));
			}
			if (totalRatingCount != 0)
				this.ratingMean = this.ratingMean / (double)totalRatingCount;

			//Calculating user variances
			items.reset();
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				Set<Integer> userIds = item.fieldIds(true);
				if (userIds.size() == 0) continue;
				
				int itemId = item.id();
				double varSum = 0;
				double itemMean = this.itemMeans.get(itemId);
				for (int userId : userIds) {
					double value = item.get(userId).value;
					double d = value - itemMean;
					varSum += d*d;
					
					double D = value - this.ratingMean;
					this.ratingVar += D*D;
				}
				this.itemVars.put(itemId, varSum / (double)(userIds.size()));
			}
			if (totalRatingCount != 0)
				this.ratingVar = this.ratingVar / (double)totalRatingCount;
			items.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return this.itemVars;
	}

	
	/**
	 * Getting the list of all similar measures in names.
	 * @return all similar measures.
	 */
	public List<String> getAllMeasures() {
		Set<String> mSet = Util.newSet();
		mSet.addAll(getMainMeasures());
		
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
		mSet.add(Measure.PEARSON);
		mSet.add(Measure.JACCARD);
		mSet.add(Measure.MSD);
		mSet.add(Measure.URP);
		mSet.add(Measure.TRIANGLE);
		mSet.add(Measure.RPB);
		mSet.add(Measure.IPWR);
		mSet.add(Measure.SMCC);
		mSet.add(Measure.ADR);
		mSet.add(Measure.OS);
		
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
		else if (measure.equals(Measure.PEARSON))
			return pearson(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.RPB))
			return rpb(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.JACCARD))
			return jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.MSD))
			return msd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.URP))
			return urp(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.TRIANGLE))
			return triangle(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SMCC))
			return smcc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.ADR))
			return adr(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.IPWR))
			return ipwr(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.OS))
			return os(vRating1, vRating2, profile1, profile2);
		else
			return Constants.UNUSED;
	}
	
	
	/**
	 * Updating configuration according to specified measure.
	 * @param measure specified measure.
	 */
	protected void updateConfig(String measure) {
		if (config == null || measure == null) return;
		
		config.addReadOnly(COSINE_NORMALIZED_FIELD);
		config.addReadOnly(COSINE_WEIGHTED_FIELD);
		config.addReadOnly(COSINE_RA_FIELD);
		config.addReadOnly(PEARSON_WEIGHTED_FIELD);
		config.addReadOnly(PEARSON_RA_FIELD);
		config.addReadOnly(MSD_FRACTION_FIELD);
		config.addReadOnly(ENTROPY_SUPPORT_FIELD);
		config.addReadOnly(RATINGJ_THRESHOLD_FIELD);
		config.addReadOnly(INDEXEDJ_INTERVALS_FIELD);
		config.addReadOnly(JACCARD_TYPE);
		config.addReadOnly(COSINE_TYPE);
		config.addReadOnly(PEARSON_TYPE);
		config.addReadOnly(MSD_TYPE);
		config.addReadOnly(TRIANGLE_TYPE);
		config.addReadOnly(IPWR_ALPHA_FIELD);
		config.addReadOnly(IPWR_BETA_FIELD);
		
		if (measure.equals(Measure.COSINE)) {
			config.removeReadOnly(ENTROPY_SUPPORT_FIELD);
			config.removeReadOnly(COSINE_NORMALIZED_FIELD);
			config.removeReadOnly(COSINE_WEIGHTED_FIELD);
			config.removeReadOnly(COSINE_RA_FIELD);
			config.removeReadOnly(COSINE_TYPE);
		}
		else if (measure.equals(Measure.PEARSON)) {
			config.removeReadOnly(ENTROPY_SUPPORT_FIELD);
			config.removeReadOnly(PEARSON_WEIGHTED_FIELD);
			config.removeReadOnly(PEARSON_RA_FIELD);
			config.removeReadOnly(PEARSON_TYPE);
		}
		else if (measure.equals(Measure.RPB)) {
		}
		else if (measure.equals(Measure.JACCARD)) {
			config.removeReadOnly(RATINGJ_THRESHOLD_FIELD);
			config.removeReadOnly(INDEXEDJ_INTERVALS_FIELD);
			config.removeReadOnly(JACCARD_TYPE);
		}
		else if (measure.equals(Measure.MSD)) {
			config.removeReadOnly(MSD_FRACTION_FIELD);
			config.removeReadOnly(MSD_TYPE);
		}
		else if (measure.equals(Measure.URP)) {
		}
		else if (measure.equals(Measure.TRIANGLE)) {
			config.removeReadOnly(TRIANGLE_TYPE);
		}
		else if (measure.equals(Measure.SMCC)) {
		}
		else if (measure.equals(Measure.ADR)) {
		}
		else if (measure.equals(Measure.IPWR)) {
			config.removeReadOnly(IPWR_ALPHA_FIELD);
			config.removeReadOnly(IPWR_BETA_FIELD);
		}
		else if (measure.equals(Measure.OS)) {
		}
	}
	
	
	/**
	 * Calculating the cosine measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return the cosine between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	protected double cosine(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ctype = config.getAsString(COSINE_TYPE);
		if (ctype.equals(COSINE_TYPE_NORMAL))
			return cosineNormal(vRating1, vRating2, profile1, profile2);
		else if (ctype.equals(COSINE_TYPE_ADJUSTED))
			return cod(vRating1, vRating2, profile1, profile2);
		else if (ctype.equals(COSINE_TYPE_JACCARD_LIKE))
			return coj(vRating1, vRating2, profile1, profile2);
		else if (ctype.equals(COSINE_TYPE_JACCARD))
			return jaccardNormal(vRating1, vRating2, profile1, profile2) * cosineNormal(vRating1, vRating2, profile1, profile2);
		else
			return cosineNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the normal cosine measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return the cosine between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	protected double cosineNormal(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		boolean normalized = config.getAsBoolean(COSINE_NORMALIZED_FIELD);
		return normalized ? cosine(vRating1, vRating2, this.ratingMedian) : cosine(vRating1, vRating2);

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
	protected abstract double cod(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2);
	

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

		boolean entropySupport = config.getAsBoolean(ENTROPY_SUPPORT_FIELD);
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int fieldId : common) {
			double mean = fieldMeans.get(fieldId);
			double dev1 = vRating1.get(fieldId).value - mean;
			double dev2 = vRating2.get(fieldId).value - mean;
			VX  += dev1 * dev1;
			VY  += dev2 * dev2;
			
			double entropy = 1;
			if (entropySupport) {
				entropy = calcEntropy(fieldId, isCached() ? this.valueCache : null);
				entropy = Util.isUsed(entropy) ? entropy : 1;
			}
			VXY += dev1 * dev2 * entropy;
		}
		
		return VXY / Math.sqrt(VX * VY);
	}

	
	/**
	 * Calculating the Jaccard-like cosine measure between two pairs. Jaccard-like cosine is developed by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen
	 * @return Jaccard-like cosine measure between both two rating vectors and profiles.
	 */
	protected double coj(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> union = unionFieldIds(vRating1, vRating2);
		
		boolean entropySupport = config.getAsBoolean(ENTROPY_SUPPORT_FIELD);
		boolean normalized = getConfig().getAsBoolean(COSINE_NORMALIZED_FIELD);
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int fieldId : union) {
			double dev1 = Constants.UNUSED;
			double dev2 = Constants.UNUSED;
			if (vRating1.isRated(fieldId))
				dev1 = vRating1.get(fieldId).value - (normalized ? this.ratingMedian : 0);
			if (vRating2.isRated(fieldId))
				dev2 = vRating2.get(fieldId).value - (normalized ? this.ratingMedian : 0);
			
			if (Util.isUsed(dev1)) VX  += dev1 * dev1;
			if (Util.isUsed(dev2)) VY  += dev2 * dev2;
			if (Util.isUsed(dev1) && Util.isUsed(dev2)) {
				double entropy = 1;
				if (entropySupport) {
					entropy = calcEntropy(fieldId, isCached() ? this.valueCache : null);
					entropy = Util.isUsed(entropy) ? entropy : 1;
				}
				VXY += dev1 * dev2 * entropy;
			}
		}
		
		return VXY / Math.sqrt(VX * VY);
	}
	
	
	/**
	 * Calculating the Pearson measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Pearson measure between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	protected double pearson(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ptype = config.getAsString(PEARSON_TYPE);
		if (ptype.equals(PEARSON_TYPE_NORMAL))
			return corr(vRating1, vRating2);
		else if (ptype.equals(PEARSON_TYPE_JACCARD))
			return corr(vRating1, vRating2) * jaccardNormal(vRating1, vRating2, profile1, profile2);
		else if (ptype.equals(PEARSON_TYPE_CPC))
			return cpc(vRating1, vRating2, profile1, profile2);
		else if (ptype.equals(PEARSON_TYPE_WPC))
			return wpc(vRating1, vRating2, profile1, profile2);
		else if (ptype.equals(PEARSON_TYPE_SPC))
			return spc(vRating1, vRating2, profile1, profile2);
		else if (ptype.equals(PEARSON_TYPE_IPC))
			return ipc(vRating1, vRating2, profile1, profile2);
		else
			return corr(vRating1, vRating2);
		
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
	private Vector[] toNormVector(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
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
		return cosine(vRating1, vRating2, this.ratingMedian);
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
			return corr(vRating1, vRating2) * (N/WPC_THRESHOLD);
		else
			return corr(vRating1, vRating2);
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
		
		return corr(vRating1, vRating2) / (1 + Math.exp(-N/2.0));
	}
	
	
	/**
	 * Calculating the IPC measure between two pairs.
	 * Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, and Mayda Abdullateef Alrige developed the IPC measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, Mayda Abdullateef Alrige
	 * @return IPC measure between both two rating vectors.
	 */
	protected double ipc(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> union = unionFieldIds(vRating1, vRating2);
		
		boolean entropySupport = config.getAsBoolean(ENTROPY_SUPPORT_FIELD);
		double VX = 0, VY = 0;
		double VXY = 0;
		double mean1 = vRating1.mean();
		double mean2 = vRating2.mean();
		for (int fieldId : union) {
			double mean = calcColumnMean(getColumnRating(fieldId), isCached() ? this.dualValueCache : null);
			double dev1 = Constants.UNUSED;
			double dev2 = Constants.UNUSED;
			if (vRating1.isRated(fieldId))
				dev1 = vRating1.get(fieldId).value * (mean1-mean);
			if (vRating2.isRated(fieldId))
				dev2 = vRating2.get(fieldId).value * (mean2-mean);
			
			if (Util.isUsed(dev1)) VX  += dev1 * dev1;
			if (Util.isUsed(dev2)) VY  += dev2 * dev2;
			if (Util.isUsed(dev1) && Util.isUsed(dev2)) {
				double entropy = 1;
				if (entropySupport) {
					entropy = calcEntropy(fieldId, isCached() ? this.valueCache : null);
					entropy = Util.isUsed(entropy) ? entropy : 1;
				}
				VXY += dev1 * dev2 * entropy;
			}
		}
		
		return VXY / Math.sqrt(VX * VY);
	}
	
	
	/**
	 * Calculating the RPB measure between two pairs.
	 * Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, and Mayda Abdullateef Alrige developed the RPB measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, Mayda Abdullateef Alrige
	 * @return RPB measure between both two rating vectors.
	 */
	protected double rpb(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		double min = getMinRating(), max = getMaxRating();
		vRating1 = vRating1.normalize(min, max);
		vRating2 = vRating2.normalize(min, max);
		
		double mean1 = vRating1.mean();
		double sd1 = Math.sqrt(vRating1.var());
		double mean2 = vRating2.mean();
		double sd2 = Math.sqrt(vRating2.var());
		
		return Math.cos(Math.abs(mean1-mean2) * Math.abs(sd1-sd2));
	}
	
	
	
	
	/**
	 * Calculating the IPWR measure between two pairs.
	 * Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, and Mayda Abdullateef Alrige developed the IPWR measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Mubbashir Ayub, Mustansar Ali Ghazanfar, Zahid MehmoodID, Tanzila Saba, Riad Alharbey, Asmaa Mahdi Munshi, Mayda Abdullateef Alrige
	 * @return IPWR measure between both two rating vectors.
	 */
	protected double ipwr(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		double alpha = config.getAsReal(IPWR_ALPHA_FIELD);
		double beta = config.getAsReal(IPWR_BETA_FIELD);
		return alpha*rpb(vRating1, vRating2, profile1, profile2) + beta*ipc(vRating1, vRating2, profile1, profile2);
	}

		
	/**
	 * Calculating the Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Jaccard measure between both two rating vectors and profiles.
	 */
	protected double jaccard(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		String jtype = config.getAsString(JACCARD_TYPE);
		if (jtype.equals(JACCARD_TYPE_NORMAL))
			return jaccardNormal(vRating1, vRating2, profile1, profile2);
		else if (jtype.equals(JACCARD_TYPE_MULTI))
			return jaccardMulti(vRating1, vRating2, profile1, profile2);
		else if (jtype.equals(JACCARD_TYPE_DICE))
			return jaccardDice(vRating1, vRating2, profile1, profile2);
		else if (jtype.equals(JACCARD_TYPE_PNCR))
			return Math.exp(jaccardNormal(vRating1, vRating2, profile1, profile2) - 1.0);
		else if (jtype.equals(JACCARD_TYPE_RJ))
			return jaccardRelevant(vRating1, vRating2, profile1, profile2);
		else if (jtype.equals(JACCARD_TYPE_RATINGJ))
			return jaccardRating(vRating1, vRating2, profile1, profile2);
		else if (jtype.equals(JACCARD_TYPE_INDEXEDJ))
			return jaccardIndexed(vRating1, vRating2, profile1, profile2);
		else
			return jaccardNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the normal Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Jaccard measure between both two rating vectors and profiles.
	 */
	protected double jaccardNormal(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);

		double n = common.size();
		double N = set1.size() + set2.size() - n;
		return n/N;
	}
	
	
	/**
	 * Calculating the multiplied Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Jaccard2 measure between both two rating vectors and profiles.
	 */
	protected double jaccardMulti(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);

		double n = common.size();
		double N = set1.size() * set2.size();
		return n/N;
	}

	
	/**
	 * Calculating the Dice Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Dice measure between both two rating vectors and profiles.
	 */
	protected double jaccardDice(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);

		double n = 2 * common.size();
		double N = set1.size() + set2.size();
		return n/N;
	}

	
	/**
	 * Calculating the relevant Jaccard (RJ) measure between two pairs.
	 * Sujoy Bag, Sri Krishna Kumar, and Manoj Kumar Tiwari developed the relevant Jaccard (RJ) measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Relevant Jaccard (RJ) measure between both two rating vectors and profiles.
	 * @author Sujoy Bag, Sri Krishna Kumar, Manoj Kumar Tiwari
	 */
	protected double jaccardRelevant(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);
		
		double n = common.size();
		if (n == 0 && (set1.size() != 0 || set2.size() != 0)) return 0;
		double n1 = set1.size() - n;
		double n2 = set2.size() - n;
		
		return 1 / (1 + 1/n + n1/(1+n1) + 1/(1+n2));
	}
	
	
	/**
	 * Calculating the rating Jaccard measure between two pairs.
	 * Mubbashir Ayub1, Mustansar Ali Ghazanfar1, Tasawer Khan1, Asjad Saleem developed the rating Jaccard measure. Loc Nguyen modified and implemented it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Rating Jaccard measure between both two rating vectors and profiles.
	 * @author Mubbashir Ayub1, Mustansar Ali Ghazanfar1, Tasawer Khan1, Asjad Saleem
	 */
	protected double jaccardRating(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);
		double N = set1.size() + set2.size() - common.size();
		if (N == 0) return Constants.UNUSED;

		int nt = 0;
		boolean equal = true;
		for (int fieldId : common) {
			double v1 = vRating1.get(fieldId).value;
			double v2 = vRating2.get(fieldId).value;
			if (v1 == v2) nt++;
			
			if (!equal) continue;
			if (v1 != v2) equal = false;
		}
		if (nt == 0) return 0;
		
		if (equal)
			nt++;
		else {
			double mean1 = vRating1.mean(), mean2 = vRating1.mean();
			double t = config.getAsReal(RATINGJ_THRESHOLD_FIELD);
			if (Util.isUsed(t)) {
				double bias = Math.abs(mean2 - mean1);
				if (bias <= t * Math.min(mean1, mean2)) nt++;
			}
		}
		
		return (double)nt / N;
	}
	
	
	/**
	 * Calculating the indexed Jaccard measure between two pairs.
	 * Soojung Lee developed the indexed Jaccard measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Indexed Jaccard measure between both two rating vectors and profiles.
	 * @author Soojung Lee
	 */
	protected double jaccardIndexed(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		List<Double> intervals = TextParserUtil.parseListByClass(getConfig().getAsString(INDEXEDJ_INTERVALS_FIELD), Double.class, ",");
		if (intervals.size() == 0) return jaccardNormal(vRating1, vRating2, profile1, profile2);
		
		Set<Integer> A = vRating1.fieldIds(true);
		Set<Integer> B = vRating2.fieldIds(true);
		List<Set<Integer>> setList1 = Util.newList(intervals.size());
		List<Set<Integer>> setList2 = Util.newList(intervals.size());
		for (int i = 0; i < intervals.size(); i++) {
			Set<Integer> set1 = Util.newSet();
			for (int id : A) {
				double v = vRating1.get(id).value;
				if (i == 0) {
					if (v < intervals.get(i)) set1.add(id);
				}
				else if (i < intervals.size() - 1 ) {
					if (v >= intervals.get(i) && v < intervals.get(i+1)) set1.add(id);
				}
				else {
					if (v >= intervals.get(i)) set1.add(id);
				}

			}
			setList1.add(set1);
			
			Set<Integer> set2 = Util.newSet();
			for (int id : B) {
				double v = vRating2.get(id).value;
				if (i == 0) {
					if (v < intervals.get(i)) set2.add(id);
				}
				else if (i < intervals.size() - 1 ) {
					if (v >= intervals.get(i) && v < intervals.get(i+1)) set2.add(id);
				}
				else {
					if (v >= intervals.get(i)) set2.add(id);
				}
			}
			setList2.add(set2);
		}
		
		double sumJ = 0;
		int M = 0;
		for (int i = 0; i < intervals.size(); i++) {
			Set<Integer> set1 = setList1.get(i);
			Set<Integer> set2 = setList2.get(i);
			if (set1.size() == 0 && set2.size() == 0) continue;
			
			Set<Integer> common = Util.newSet();
			common.addAll(set1);
			common.retainAll(set2);
			double n = common.size();
			double N = set1.size() + set2.size() - n;
			sumJ += n / N;
			M++;
		}
		
		return sumJ / M;
	}
	
	
	/**
	 * Calculating the MSD measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return MSD measure between both two rating vectors and profiles.
	 */
	protected double msd(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String mtype = config.getAsString(MSD_TYPE);
		if (mtype.equals(MSD_TYPE_NORMAL))
			return msdNormal(vRating1, vRating2, profile1, profile2);
		else if (mtype.equals(MSD_TYPE_JACCARD))
			return msdNormal(vRating1, vRating2, profile1, profile2) * jaccardNormal(vRating1, vRating2, profile1, profile2);
		else
			return msdNormal(vRating1, vRating2, profile1, profile2);
	}

	
	/**
	 * Calculating the normal MSD measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return normal MSD measure between both two rating vectors and profiles.
	 */
	protected double msdNormal(RatingVector vRating1, RatingVector vRating2,
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
			double range = getMaxRating() - getMinRating();
			return 1.0 - sum/(common.size()*range*range);
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
	 * @return Triangle measure between both two rating vectors.
	 */
	protected double triangle(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ttype = config.getAsString(TRIANGLE_TYPE);
		if (ttype.equals(TRIANGLE_TYPE_NORMAL))
			return triangleNormal(vRating1, vRating2, profile1, profile2);
		else if (ttype.equals(TRIANGLE_TYPE_TJM))
			return triangleNormal(vRating1, vRating2, profile1, profile2) * jaccardNormal(vRating1, vRating2, profile1, profile2);
		else
			return triangleNormal(vRating1, vRating2, profile1, profile2);
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
	 * @return Triangle measure between both two rating vectors.
	 */
	protected double triangleNormal(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		return 1 - vRating1.distance(vRating2) / (vRating1.module()+vRating2.module());
	}
	
	
	/**
	 * Calculating the SMCC measure between two pairs.
	 * Vijay Verma and Rajesh Kumar Aggarwal developed the MPIP. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return SMCC measure between both two rating vectors and profiles.
	 * @author Vijay Verma, Rajesh Kumar Aggarwal
	 */
	protected double smcc(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);
		double N = set1.size() + set2.size() - common.size();
		if (N == 0) return Constants.UNUSED;
		
		int matchedCount = 0;
		for (int id : common) {
			double v1 = vRating1.get(id).value;
			boolean r1 = Accuracy.isRelevant(v1, ratingMedian);
			double v2 = vRating2.get(id).value;
			boolean r2 = Accuracy.isRelevant(v2, ratingMedian);
			
			if ((r1 && r2) || ((!r1) && (!r2)) || (v1 == ratingMedian && v2 == ratingMedian))
				matchedCount++;
		}
		
		return (double)matchedCount / (double)N;
	}
	
	
	/**
	 * Calculating the Absolute Difference of Ratings (ADR) measure between two pairs.
	 * Achraf Gazdar and Lotfi Hidri developed the ADR. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Absolute Difference of Ratings (ADR) measure between both two rating vectors and profiles.
	 * @author Achraf Gazdar, Lotfi Hidri
	 */
	protected double adr(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double range = getMaxRating() - getMinRating();
		double adr = 0;
		for (int id : common) {
			double v1 = vRating1.get(id).value;
			double v2 = vRating2.get(id).value;
			adr += Math.exp(-Math.abs(v1-v2)/range);
		}
		
		return adr / (double)common.size();
	}
	
	
	/**
	 * Calculating the OS measure between two pairs.
	 * Achraf Gazdar and Lotfi Hidri developed the ADR. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return OS measure between both two rating vectors and profiles.
	 * @author Achraf Gazdar, Lotfi Hidri
	 */
	protected double os(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return adr(vRating1, vRating2, profile1, profile2) * Math.exp(jaccardNormal(vRating1, vRating2, profile1, profile2)-1.0);
	}
	
	
	/**
	 * Calculating the entropy of rating vector of given column identifier.
	 * @param columnId given column identifier.
	 * @param cachedMap cached map.
	 * @return the entropy of rating vector of given column identifier.
	 */
	protected double calcEntropy(int columnId,  Map<Integer, Object> cachedMap) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				double minRating = getMinRating();
				double maxRating = getMaxRating();
				double value = minRating;
				double e = 0;
				while (value <= maxRating) {
					double prob = prob(columnId, value, false);
					e += prob * Math.log(prob+Float.MIN_VALUE);
					value = value + 1;
				}
				
				return -e;
			}
		};
		
		if (cachedMap != null)
			return (double)cacheTask(columnId, cachedMap, task);
		else
			return (Double)task.perform();
	}

	
	/**
	 * Calculating the probability of specified identifier with given value.
	 * @param id specified identifier.
	 * @param value given value.
	 * @param isRow flag to indicate whether the specified identifier is of row rating vector.
	 * @return the probability of specified identifier with given value.
	 */
	protected double prob(int id, double value, boolean isRow) {
		Set<Integer> ids = isRow ? getColumnIds() : getRowIds();
		int n = 0, N = 0;
		for (int fieldId : ids) {
			RatingVector vRating = isRow ? getColumnRating(fieldId) : getRowRating(fieldId);
			if (vRating.isRated(id)) {
				N++;
				if (vRating.get(id).value == value) n++;
			}
		}
		
		return (double)n / (double)N;
	}
	
	
	/**
	 * Calculating the probability of specified identifier.
	 * @param id specified identifier.
	 * @param isRow flag to indicate whether the specified identifier is of row rating vector.
	 * @return the probability of specified identifier.
	 */
	protected double prob(int id, boolean isRow) {
		Set<Integer> ids = isRow ? getColumnIds() : getRowIds();
		int N = 0;
		for (int fieldId : ids) {
			RatingVector vRating = isRow ? getColumnRating(fieldId) : getRowRating(fieldId);
			if (vRating.isRated(id)) N++;
		}
		
		return (double)N / (double)ids.size();
	}

	
	/**
	 * Calculating the probability of column identifier.
	 * @param columnId column identifier.
	 * @return the probability of column identifier.
	 */
	protected abstract double prob(int columnId);
	
	
	/**
	 * Counting the number given specified identifier.
	 * @param id specified identifier.
	 * @param isRow flag to indicate whether rows are counted.
	 * @return the number given specified identifier.
	 */
	protected int count(int id, boolean isRow) {
		Set<Integer> ids = isRow ? getColumnIds() : getRowIds();
		int N = 0;
		for (int fieldId : ids) {
			RatingVector vRating = isRow ? getColumnRating(fieldId) : getRowRating(fieldId);
			if (vRating.isRated(id)) N++;
		}
		
		return N;
	}

	
	/**
	 * Calculating correlation coefficient between two rating vectors.
	 * @param thisVector the first rating vector.
	 * @param thatVector the second rating vector.
	 * @return correlation coefficient between two rating vectors.
	 */
	protected double corr(RatingVector thisVector, RatingVector thatVector) {
		Set<Integer> fieldIds = commonFieldIds(thisVector, thatVector);
		boolean entropySupport = config.getAsBoolean(ENTROPY_SUPPORT_FIELD);
		boolean isWeighted = config.getAsBoolean(PEARSON_WEIGHTED_FIELD);
		boolean ra = config.getAsBoolean(PEARSON_RA_FIELD);
		double VX = 0, VY = 0, VXY = 0;
		double mean1 = thisVector.mean();
		double mean2 = thatVector.mean();
		for (int fieldId : fieldIds) {
			double weight = 1;
			if (isWeighted) {
				weight = prob(fieldId);
				weight = Util.isUsed(weight) ? weight : 1;
			}
			
			double dev1 = thisVector.get(fieldId).value - mean1;
			double dev2 = thatVector.get(fieldId).value - mean2;
			VX  += dev1 * dev1 * weight;
			VY  += dev2 * dev2 * weight;
			
			double entropy = 1;
			if (entropySupport) {
				entropy = calcEntropy(fieldId, isCached() ? this.valueCache : null);
				entropy = Util.isUsed(entropy) ? entropy : 1;
			}
			double module = 1;
			if (ra) {
				module = calcModule(getColumnRating(fieldId), isCached() ? this.dualValueCache : null);
				module = Util.isUsed(module) ? module*module : 1;
			}
			VXY += dev1 * dev2 * weight * entropy / module;
		}
		
		return VXY / Math.sqrt(VX * VY);
	}

	
	/**
	 * Calculating correlation coefficient between two rating vectors.
	 * @param thisVector the first rating vector.
	 * @param thatVector the second rating vector.
	 * @return correlation coefficient between two rating vectors.
	 */
	protected double cosine(RatingVector thisVector, RatingVector thatVector) {
		return cosine(thisVector, thatVector, 0);
	}

	
	/**
	 * Calculating correlation coefficient between two rating vectors.
	 * @param thisVector the first rating vector.
	 * @param thatVector the second rating vector.
	 * @param average averaged value.
	 * @return correlation coefficient between two rating vectors.
	 */
	protected double cosine(RatingVector thisVector, RatingVector thatVector, double average) {
		Set<Integer> fieldIds = commonFieldIds(thisVector, thatVector);
		boolean entropySupport = config.getAsBoolean(ENTROPY_SUPPORT_FIELD);
		boolean isWeighted = config.getAsBoolean(COSINE_WEIGHTED_FIELD);
		boolean ra = config.getAsBoolean(COSINE_RA_FIELD);
		double VX = 0, VY = 0, VXY = 0;
		for (int fieldId : fieldIds) {
			double weight = 1;
			if (isWeighted) {
				weight = prob(fieldId);
				weight = Util.isUsed(weight) ? weight : 1;
			}

			double dev1 = thisVector.get(fieldId).value - average;
			double dev2 = thatVector.get(fieldId).value - average;
			VX  += dev1 * dev1 * weight;
			VY  += dev2 * dev2 * weight;
			
			double entropy = 1;
			if (entropySupport) {
				entropy = calcEntropy(fieldId, isCached() ? this.valueCache : null);
				entropy = Util.isUsed(entropy) ? entropy : 1;
			}
			double module = 1;
			if (ra) {
				module = calcModule(getColumnRating(fieldId), isCached() ? this.dualValueCache : null);
				module = Util.isUsed(module) ? module*module : 1;
			}
			VXY += dev1 * dev2 * weight * entropy / module;
		}
		
		return VXY / Math.sqrt(VX * VY);
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
	 * Calculating mean of row rating vector.
	 * @param vRating specified row rating vector.
	 * @param cachedMap cached map.
	 * @return mean of row rating vector.
	 */
	protected double calcRowMean(RatingVector vRating,  Map<Integer, Object> cachedMap) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return calcRowMean(vRating);
			}
		};
		
		if (cachedMap != null)
			return (double)cacheTask(vRating.id(), cachedMap, task);
		else
			return (Double)task.perform();
	}

	
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
	 * Calculating mean of column rating vector.
	 * @param vRating specified column rating vector.
	 * @param cachedMap cached map.
	 * @return mean of column rating vector.
	 */
	protected double calcColumnMean(RatingVector vRating,  Map<Integer, Object> cachedMap) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return calcColumnMean(vRating);
			}
		};
		
		if (cachedMap != null)
			return (double)cacheTask(vRating.id(), cachedMap, task);
		else
			return (Double)task.perform();
	}
	
	
	/**
	 * Calculating module (length) of row rating vector.
	 * @param vRating specified row rating vector.
	 * @param cachedMap cached map.
	 * @return mean of row rating vector.
	 */
	protected double calcModule(RatingVector vRating,  Map<Integer, Object> cachedMap) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return vRating.module();
			}
		};
		
		if (cachedMap != null)
			return (double)cacheTask(vRating.id(), cachedMap, task);
		else
			return (Double)task.perform();
	}

	
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
		tempConfig.put(MEASURE, getDefaultMeasure());
		tempConfig.put(HYBRID, false); tempConfig.addInvisible(HYBRID);
		tempConfig.put(SIMILARITY_THRESHOLD_FIELD, SIMILARITY_THRESHOLD_DEFAULT);
		tempConfig.put(ENTROPY_SUPPORT_FIELD, ENTROPY_SUPPORT_DEFAULT);
		tempConfig.put(COSINE_TYPE, COSINE_TYPE_NORMAL);
		tempConfig.put(COSINE_NORMALIZED_FIELD, COSINE_NORMALIZED_DEFAULT);
		tempConfig.put(COSINE_WEIGHTED_FIELD, COSINE_WEIGHTED_DEFAULT);
		tempConfig.put(COSINE_RA_FIELD, COSINE_RA_DEFAULT);
		tempConfig.put(PEARSON_TYPE, PEARSON_TYPE_NORMAL);
		tempConfig.put(PEARSON_WEIGHTED_FIELD, PEARSON_WEIGHTED_DEFAULT);
		tempConfig.put(PEARSON_RA_FIELD, PEARSON_RA_DEFAULT);
		tempConfig.put(MSD_TYPE, MSD_TYPE_NORMAL);
		tempConfig.put(MSD_FRACTION_FIELD, MSD_FRACTION_DEFAULT);
		tempConfig.put(JACCARD_TYPE, JACCARD_TYPE_NORMAL);
		tempConfig.put(RATINGJ_THRESHOLD_FIELD, RATINGJ_THRESHOLD_DEFAULT);
		tempConfig.put(INDEXEDJ_INTERVALS_FIELD, INDEXEDJ_INTERVALS_DEFAULT);
		tempConfig.put(TRIANGLE_TYPE, TRIANGLE_TYPE_NORMAL);
		tempConfig.put(IPWR_ALPHA_FIELD, IPWR_ALPHA_DEFAULT);
		tempConfig.put(IPWR_BETA_FIELD, IPWR_BETA_DEFAULT);

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
				else if (key.equals(JACCARD_TYPE)) {
					String jtype = getAsString(JACCARD_TYPE);
					jtype = jtype == null ? JACCARD_TYPE_NORMAL : jtype;
					List<String> jtypes = Util.newList();
					jtypes.add(JACCARD_TYPE_DICE);
					jtypes.add(JACCARD_TYPE_MULTI);
					jtypes.add(JACCARD_TYPE_NORMAL);
					jtypes.add(JACCARD_TYPE_PNCR);
					jtypes.add(JACCARD_TYPE_RJ);
					jtypes.add(JACCARD_TYPE_RATINGJ);
					jtypes.add(JACCARD_TYPE_INDEXEDJ);
					Collections.sort(jtypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one Jaccard type", 
						"Choosing Jaccard type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						jtypes.toArray(new String[] {}), 
						jtype);
				}
				else if (key.equals(COSINE_TYPE)) {
					String ctype = getAsString(COSINE_TYPE);
					ctype = ctype == null ? COSINE_TYPE_NORMAL : ctype;
					List<String> ctypes = Util.newList();
					ctypes.add(COSINE_TYPE_NORMAL);
					ctypes.add(COSINE_TYPE_ADJUSTED);
					ctypes.add(COSINE_TYPE_JACCARD_LIKE);
					ctypes.add(COSINE_TYPE_JACCARD);
					Collections.sort(ctypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one cosine type", 
						"Choosing cosine type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						ctypes.toArray(new String[] {}), 
						ctype);
				}
				else if (key.equals(PEARSON_TYPE)) {
					String ptype = getAsString(PEARSON_TYPE);
					ptype = ptype == null ? PEARSON_TYPE_NORMAL : ptype;
					List<String> ptypes = Util.newList();
					ptypes.add(PEARSON_TYPE_NORMAL);
					ptypes.add(PEARSON_TYPE_JACCARD);
					ptypes.add(PEARSON_TYPE_CPC);
					ptypes.add(PEARSON_TYPE_WPC);
					ptypes.add(PEARSON_TYPE_SPC);
					ptypes.add(PEARSON_TYPE_IPC);
					ptypes.add(PEARSON_TYPE_RA);
					Collections.sort(ptypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one Pearson type", 
						"Choosing Pearson type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						ptypes.toArray(new String[] {}), 
						ptype);
				}
				else if (key.equals(MSD_TYPE)) {
					String mtype = getAsString(MSD_TYPE);
					mtype = mtype == null ? MSD_TYPE_NORMAL : mtype;
					List<String> mtypes = Util.newList();
					mtypes.add(MSD_TYPE_NORMAL);
					mtypes.add(MSD_TYPE_JACCARD);
					Collections.sort(mtypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one MSD type", 
						"Choosing MSD type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						mtypes.toArray(new String[] {}), 
						mtype);
				}
				else if (key.equals(TRIANGLE_TYPE)) {
					String ttype = getAsString(TRIANGLE_TYPE);
					ttype = ttype == null ? TRIANGLE_TYPE_NORMAL : ttype;
					List<String> ttypes = Util.newList();
					ttypes.add(TRIANGLE_TYPE_NORMAL);
					ttypes.add(TRIANGLE_TYPE_TJM);
					Collections.sort(ttypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one triangle type", 
						"Choosing triangle type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						ttypes.toArray(new String[] {}), 
						ttype);
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
	private static double calcMean(NeighborCF cf, Map<Integer, Double> means, RatingVector vRating) {
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
