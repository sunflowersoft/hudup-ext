package net.hudup.core.alg.cf;

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
import net.hudup.core.alg.RecommendParam;
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
import net.hudup.core.logistic.NextUpdate;
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
 * Author Hyung Jun Ahn contributed PIP measure.<br>
 * Authors Keunho Choi and Yongmoo Suh contributed PC measure.<br>
 * Authors Suryakant and Tripti Mahara contributed MMD measure and CjacMD measure.<br>
 * Authors Shuang-Bo Sun, Zhi-Heng Zhang, Xin-Ling Dong, Heng-Ru Zhang, Tong-Jun Li, Lin Zhang, and Fan Min contributed Triangle measure and TJM measure.<br>
 * Authors Junmei Feng, Xiaoyi Fengs, Ning Zhang, and Jinye Peng contributed Feng model.<br>
 * Authors Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, and Xiaohan Yin contributed Mu measure.<br>
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
	 * Value bins.
	 */
	public static final String VALUE_BINS_FIELD = "value_bins";

	
	/**
	 * Default value bins.
	 */
	public static final String VALUE_BINS_DEFAULT = "1, 2, 3, 4, 5";

	
//	/**
//	 * Name of hybrid measure.
//	 */
//	public static final String HYBRID = "hybrid";

	
	/**
	 * Name of cosine measure.
	 */
	public static final String COSINE = "cosine";

	
	/**
	 * Name of cosine + Jaccard measure.
	 */
	public static final String COSINEJ = "cosinej";

	
	/**
	 * Name of pseudo Cosine + Jaccard.
	 */
	public static final String COJ = "coj";

	
	/**
	 * Name of Pearson measure.
	 */
	public static final String PEARSON = "pearson";

	
	/**
	 * Name of Pearson + Jaccard measure.
	 */
	public static final String PEARSONJ = "pearsonj";

	
	/**
	 * Name of adjusted cosine measure.
	 */
	public static final String COD = "cod";

	
	/**
	 * Name of constrained Pearson correlation coefficient measure. It is also cosine normalized (CON) measure.
	 */
	public static final String CPC = "cpc";
	
	
	/**
	 * Name of weighted Pearson correlation coefficient measure.
	 */
	public static final String WPC = "wpc";
	
	
	/**
	 * Name of sigmoid Pearson correlation coefficient measure.
	 */
	public static final String SPC = "spc";

	
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
	 * Name of SRC measure.
	 */
	public static final String SRC = "src";

	
	/**
	 * Name of PIP measure.
	 */
	public static final String PIP = "pip";

	
	/**
	 * Name of PC measure.
	 */
	public static final String PC = "pc";

	
	/**
	 * Name of MMD measure.
	 */
	public static final String MMD = "mmd";

	
	/**
	 * Name of CjacMD measure which is developed by Suryakant and Tripti Mahara.
	 */
	public static final String CJACMD = "mmd";

	
	/**
	 * Name of Triangle measure.
	 */
	public static final String TRIANGLE = "triangle";

	
	/**
	 * Name of TJM measure.
	 */
	public static final String TJM = "tjm";

	
	/**
	 * Name of Feng measure.
	 */
	public static final String FENG = "feng";

	
	/**
	 * Name of Mu measure.
	 */
	public static final String MU = "mu";

	
	/**
	 * Cosine normalized mode.
	 */
	public static final String COSINE_NORMALIZED_FIELD = "cos_normalized";

	
	/**
	 * Default cosine normalized mode.
	 */
	public static final boolean COSINE_NORMALIZED_DEFAULT = false;

	
	/**
	 * BCF median mode.
	 */
	public static final String BCF_MEDIAN_MODE_FIELD = "bcf_median";

	
	/**
	 * Default BCF median mode.
	 */
	public static final boolean BCF_MEDIAN_MODE_DEFAULT = true;

	
	/**
	 * Mu alpha field.
	 */
	public static final String MU_ALPHA_FIELD = "mu_alpha";

	
	/**
	 * Default Mu alpha.
	 */
	public static final double MU_ALPHA_DEFAULT = 0.5;

	
	/**
	 * Threshold for WPCC (weight weighted Pearson correlation coefficient).
	 */
	public static final double WPC_THRESHOLD = 50;

	
	/**
	 * Rating median.
	 */
	protected double ratingMedian = (DataConfig.MIN_RATING_DEFAULT + DataConfig.MAX_RATING_DEFAULT) / 2.0;

	
	/**
	 * General user mean.
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
	
	
	/**
	 * User rating cache (user id, item id, rating value).
	 */
	protected Map<Integer, Map<Integer, Object>> userRatingCache = Util.newMap();
	
	
	/**
	 * Row similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> rowSimCache = Util.newMap();


	/**
	 * Column similarity cache.
	 */
	protected Map<Integer, Map<Integer, Object>> columnSimCache = Util.newMap();

	
	/**
	 * Column module (column vector length) cache.
	 */
	protected Map<Integer, Object> bcfColumnModuleCache = Util.newMap();

	
	/**
	 * Value bins.
	 */
	protected List<Double> valueBins = Util.newList();
	
	
	/**
	 * Rank bins.
	 */
	protected Map<Double, Integer> rankBins = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public NeighborCF() {
		// TODO Auto-generated constructor stub
		
	}


	@Override
	public synchronized void setup(Dataset dataset, Serializable... params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		
		this.ratingMedian = (this.config.getMinRating() + this.config.getMaxRating()) / 2.0;
		
		this.userRatingCache.clear();
		this.rowSimCache.clear();
		this.columnSimCache.clear();
		this.bcfColumnModuleCache.clear();

		this.valueBins = extractConfigValueBins();
		this.rankBins = convertValueBinsToRankBins(this.valueBins);
		
		updateUserMeanVars(dataset);
		updateItemMeanVars(dataset);
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		
		this.ratingMedian = Constants.UNUSED;
		this.ratingMean = Constants.UNUSED;
		this.ratingVar = Constants.UNUSED;
		this.userMeans.clear();
		this.userVars.clear();
		
		this.itemIds.clear();
		this.itemMeans.clear();
		this.itemVars.clear();
		this.userIds.clear();
		
		this.userRatingCache.clear();
		this.rowSimCache.clear();
		this.columnSimCache.clear();
		this.bcfColumnModuleCache.clear();
		
		this.rankBins.clear();
		this.valueBins.clear();
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
	 * Getting the list of supported similar measures in names.
	 * @return supported similar measures.
	 */
	public List<String> getSupportedSimilarMeasures() {
		// TODO Auto-generated method stub
		Set<String> mSet = Util.newSet();
		mSet.add(COSINE);
//		mSet.add(COSINEJ);
		mSet.add(COJ);
		mSet.add(PEARSON);
//		mSet.add(PEARSONJ);
		mSet.add(COD);
		mSet.add(CPC);
		mSet.add(WPC);
		mSet.add(SPC);
		mSet.add(JACCARD);
		mSet.add(JACCARD2);
		mSet.add(MSD);
//		mSet.add(MSDJ);
		mSet.add(PSS);
//		mSet.add(NHSM);
		mSet.add(BCF);
//		mSet.add(BCFJ);
		mSet.add(SRC);
		mSet.add(PIP);
		mSet.add(PC);
		mSet.add(MMD);
//		mSet.add(CJACMD);
		mSet.add(TRIANGLE);
//		mSet.add(TJM);
		mSet.add(FENG);
		mSet.add(MU);
		
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
	protected String getDefaultSimilarMeasure() {
		return COSINE;
	}

	
	/**
	 * Getting the similarity measure.
	 * @return similar measure.
	 */
	public String getSimilarMeasure() {
		String measure = config.getAsString(MEASURE);
		if (measure == null)
			return getDefaultSimilarMeasure();
		else
			return measure;
	}
	
	
	/**
	 * Setting the similarity measure.
	 * @param measure the similarity measure.
	 */
	public void setSimilarMeasure(String measure) {
		config.put(MEASURE, measure);
	}
	
	
	/**
	 * Checking whether the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}).
	 * @return true if the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}). Otherwise, return false.
	 */
	public boolean requireDiscreteRatingBins() {
		return requireDiscreteRatingBins(getSimilarMeasure());
	}
	
	
	/**
	 * Given specified measure, checking whether the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}).
	 * @param measure specified measure.
	 * @return true if the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}). Otherwise, return false.
	 */
	protected boolean requireDiscreteRatingBins(String measure) {
		if (measure == null)
			return false;
		else if (measure.equals(BCF) || measure.equals(BCFJ) ||  measure.equals(MMD))
			return true;
		else
			return false;
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		if (param.ratingVector == null) //Consider not estimating yet.
			return null;
		if (!isCached())
			return estimate0(param, queryIds);
		
		int userId = param.ratingVector.id();
		if (userId < 0) //user is not stored in database.
			return estimate0(param, queryIds);
			
		if (this.userRatingCache.containsKey(userId)) { //Already estimated
			Map<Integer, Object> ratingMap = this.userRatingCache.get(userId);
			if (ratingMap == null) {
				ratingMap = Util.newMap();
				this.userRatingCache.put(userId, ratingMap);
			}
				
			RatingVector result = param.ratingVector.newInstance(true);
			Set<Integer> queryIds2 = Util.newSet();
			queryIds2.addAll(queryIds);
			if (ratingMap.size() > 0) {
				for (int itemId : queryIds) {
					if (!ratingMap.containsKey(itemId))
						continue;
					
					queryIds2.remove(itemId);
					double ratingValue = (double)ratingMap.get(itemId); //cache can store unused rating value.
					if (Util.isUsed(ratingValue))
						result.put(itemId, ratingValue);
				}
				if (queryIds2.size() == 0)
					return result.size() == 0 ? null : result;
			}
			
			RatingVector result2 = estimate0(param, queryIds2);
			if (result2 == null || result2.size() == 0) {
				for (int itemId : queryIds2) {
					ratingMap.put(itemId, Constants.UNUSED); //Consider estimated.
				}
				return result.size() == 0 ? null : result;
			}
			
			Set<Integer> itemIds = result2.fieldIds(); //Resulted items are always rated.
			for (int itemId : itemIds) {
				double value = result2.get(itemId).value;
				ratingMap.put(itemId, value);
				result.put(itemId, value);
			}
			return result.size() == 0 ? null : result;
		}
		
		RatingVector result = estimate0(param, queryIds);
		Map<Integer, Object> ratingMap = Util.newMap();
		userRatingCache.put(userId, ratingMap); //Consider estimated.
		if (result == null) return null;
		
		Set<Integer> itemIds = result.fieldIds(); //Resulted items are always rated.
		for (int itemId : itemIds) {
			double value = result.get(itemId).value;
			ratingMap.put(itemId, value);
		}
		return result.size() == 0 ? null : result;
	}
	
	
	/**
	 * This method is very important, which is used to estimate rating values of given items (users) without caching. Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and a set of item (user) identifiers.
	 * The output result is a set of predictive or estimated rating values of items (users) specified by the second input parameter.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users). Return null if cannot estimate.
	 */
	protected abstract RatingVector estimate0(RecommendParam param, Set<Integer> queryIds);
	
	
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
	 * @param parameters extra parameters.
	 * @return similarity between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public synchronized double similar(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object...parameters) {
		String measure = getSimilarMeasure();

		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return similarAsUsual(measure, vRating1, vRating2, profile1, profile2, parameters);
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
	protected double similarAsUsual(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object...params) {
//		boolean hybrid = config.getAsBoolean(HYBRID);
//		if (!hybrid) {
//			profile1 = null;
//			profile2 = null;
//		}
		
		if (measure.equals(COSINE))
			return cosine(vRating1, vRating2, profile1, profile2);
		if (measure.equals(COSINEJ))
			return cosine(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		if (measure.equals(COJ))
			return coj(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PEARSON))
			return corr(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PEARSONJ))
			return corr(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(COD))
			return cod(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(CPC))
			return cpc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(WPC))
			return wpc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(SPC))
			return spc(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(JACCARD))
			return jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(JACCARD2))
			return jaccard2(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(MSD))
			return msd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(MSDJ))
			return msd(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PSS))
			return pss(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(NHSM))
			return nhsm(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(BCF))
			return bcf(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(BCFJ))
			return bcfj(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(SRC))
			return src(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PIP))
			return pip(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(PC)) {
			if ((params == null) || (params.length < 1) || !(params[0] instanceof Number))
				return Constants.UNUSED;
			else {
				int fixedColumnId = ((Number)(params[0])).intValue();
				return pc(vRating1, vRating2, profile1, profile2, fixedColumnId);
			}
		}
		else if (measure.equals(MMD))
			return mmd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(CJACMD))
			return cosine(vRating1, vRating2, profile1, profile2) + mmd(vRating1, vRating2, profile1, profile2) + jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(TRIANGLE))
			return triangle(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(TJM))
			return triangle(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(FENG))
			return feng(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(MU))
			return mu(vRating1, vRating2, profile1, profile2);
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
	@NextUpdate
	protected double cosine(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		boolean normalized = getConfig().getAsBoolean(COSINE_NORMALIZED_FIELD);
		return normalized ? vRating1.cosine(vRating2, this.ratingMedian) : vRating1.cosine(vRating2);

//		boolean normalized = getConfig().getAsBoolean(COSINE_NORMALIZED_FIELD);
//		if (profile1 == null || profile2 == null)
//			return normalized ? vRating1.cosine(vRating2, this.ratingMedian) : vRating1.cosine(vRating2);
//		
//		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
//		Vector vector1 = vectors[0];
//		Vector vector2 = vectors[1];
//		
//		return normalized ? vector1.cosine(vector2, this.ratingMedian) : vector1.cosine(vector2);
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
	public static double cod(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
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
		
		if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
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
	 * Calculating the Spearman&apos; Rank Correlation (SRC) measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Spearman&apos; Rank Correlation (SRC) measure between both two rating vectors and profiles.
	 */
	protected double src(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Map<Double, Integer> bins = rankBins;
		if (bins.isEmpty())
			bins = extractRankBins(vRating1, vRating2);

		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double sum = 0;
		for (int id : common) {
			double v1 = vRating1.get(id).value;
			int r1 = bins.get(v1);
			double v2 = vRating2.get(id).value;
			int r2 = bins.get(v2);
			
			int d = r1 - r2;
			sum += d*d;
		}
		
		double n = common.size();
		return 1.0 - 6*sum/(n*(n*n-1));
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
	protected abstract double pss(RatingVector vRating1, RatingVector vRating2,
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
	protected double nhsm(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double urp = urp(vRating1, vRating2, profile1, profile2);
		double jaccard2 = jaccard2(vRating1, vRating2, profile1, profile2);
		return pss(vRating1, vRating2, profile1, profile2) * jaccard2 * urp;
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
			double d = (vRating1.get(id).value - vRating2.get(id).value) / this.config.getMaxRating();
			sum += d*d;
		}
		
		return 1.0 - sum / common.size();
	}
	
	
	/**
	 * Calculate the Bhattacharyya measure from specified rating vectors. BC measure is modified by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return Bhattacharyya measure from specified rating vectors.
	 */
	@NextUpdate
	protected double bc(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				List<Double> bins = valueBins;
				if (bins.isEmpty())
					bins = extractValueBins(vRating1, vRating2);
				
				Set<Integer> ids1 = vRating1.fieldIds(true);
				Set<Integer> ids2 = vRating2.fieldIds(true);
				int n1 = ids1.size();
				int n2 = ids2.size();
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
		};
		
		return (double)cacheTask(vRating1.id(), vRating2.id(), this.columnSimCache, task);
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
	@NextUpdate
	protected double bcf(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		Set<Integer> columnIds1 = vRating1.fieldIds(true);
		Set<Integer> columnIds2 = vRating2.fieldIds(true);
		if (columnIds1.size() == 0 || columnIds2.size() == 0)
			return Constants.UNUSED;
		
		double bcSum = 0;
		boolean medianMode = getConfig().getAsBoolean(BCF_MEDIAN_MODE_FIELD);
		for (int columnId1 : columnIds1) {
			RatingVector columnVector1 = getColumnRating(columnId1);
			if (columnVector1 == null) continue;
			double columnModule1 = bcfCalcColumnModule(columnVector1);
			if (!Util.isUsed(columnModule1) || columnModule1 == 0) continue;
			
			double value1 = medianMode? vRating1.get(columnId1).value-this.ratingMedian : vRating1.get(columnId1).value-vRating1.mean();
			for (int columnId2 : columnIds2) {
				RatingVector columnVector2 = columnId2 == columnId1 ? columnVector1 : getColumnRating(columnId2);
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
	protected double bcfj(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return bcf(vRating1, vRating2, profile1, profile2) + jaccard(vRating1, vRating2, profile1, profile2);
	}
	
	
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

	
	/**
	 * Calculating the PIP measure between two pairs. PIP measure is developed by Hyung Jun Ahn, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Hyung Jun Ahn.
	 * @return NHSM measure between both two rating vectors and profiles.
	 */
	protected abstract double pip(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2);
	
	
	/**
	 * Calculating the PIP measure between two rating vectors. PIP measure is developed by Hyung Jun Ahn and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param ratingMedian general mean.
	 * @param fieldMeans map of field means.
	 * @author Hyung Jun Ahn.
	 * @return PIP measure between two rating vectors.
	 */
	protected double pip(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double pip = 0.0;
		for (int id : common) {
			double r1 = vRating1.get(id).value;
			double r2 = vRating2.get(id).value;
			boolean agreed = agree(r1, r2);
			
			double d = agreed ? Math.abs(r1-r2) : 2*Math.abs(r1-r2);
			double pro = (2*(config.getMaxRating()-config.getMinRating())+1) - d;
			pro = pro*pro;
			
			double impact = (Math.abs(r1-ratingMedian)+1) * (Math.abs(r2-ratingMedian)+1);
			if (!agreed)
				impact = 1 / impact;
			
			double mean = fieldMeans.get(id);
			double pop = 1;
			if ((r1 > mean && r2 > mean) || (r1 < mean && r2 < mean)) {
				double bias = (r1+r2)/2 - mean;
				pop = 1 + bias*bias;
			}
			
			pip += pro * impact * pop;
		}
		
		return pip;
	}

	
	/**
	 * Calculating the PC measure between two rating vectors. PC measure is developed by Keunho Choi and Yongmoo Suh. It implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @param fixedColumnId fixed column identifier.
	 * @author Hyung Jun Ahn.
	 * @return PC measure between both two rating vectors and profiles.
	 */
	protected abstract double pc(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2, int fixedColumnId);
	
	
	/**
	 * Calculating the PC measure between two rating vectors. PC measure is developed by Keunho Choi and Yongmoo Suh. It implemented by Loc Nguyen.
	 * @param vRating1 the first rating vectors.
	 * @param vRating2 the second rating vectors.
	 * @param fixedColumnId fixed field (column) identifier.
	 * @param fieldMeans mean value of field ratings.
	 * @author Keunho Choi, Yongmoo Suh
	 * @return PC measure between two rating vectors.
	 */
	protected double pc(RatingVector vRating1, RatingVector vRating2, int fixedColumnId, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;

		double vx = 0, vy = 0;
		double vxy = 0;
		for (int fieldId : common) {
			double mean = fieldMeans.get(fieldId);
			double d1 = vRating1.get(fieldId).value - mean;
			double d2 = vRating2.get(fieldId).value - mean;
			
			Task columnSimTask = new Task() {
				
				@Override
				public Object perform(Object...params) {
					RatingVector fixedColumnVector = getColumnRating(fixedColumnId);
					RatingVector columnVector = getColumnRating(fieldId);
					
					if (fixedColumnVector == null || columnVector == null)
						return Constants.UNUSED;
					else
						return fixedColumnVector.corr(columnVector);
				}
			};
			double columnSim = (double)cacheTask(fixedColumnId, fieldId, this.columnSimCache, columnSimTask);
			columnSim = columnSim * columnSim;
			
			vx  += d1 * d1 * columnSim;
			vy  += d2 * d2 * columnSim;
			vxy += d1 * d2 * columnSim;
		}
		
		if (vx == 0 || vy == 0)
			return Constants.UNUSED;
		else
			return vxy / Math.sqrt(vx * vy);
	}

	
	/**
	 * Calculating the Mean Measure of Divergence (MMD) measure between two pairs.
	 * Suryakant and Tripti Mahara proposed use of MMD for collaborative filtering. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Suryakant, Tripti Mahara
	 * @return MMD measure between both two rating vectors and profiles.
	 */
	protected double mmd(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> ids1 = vRating1.fieldIds(true);
		Set<Integer> ids2 = vRating2.fieldIds(true);
		int N1 = ids1.size();
		int N2 = ids2.size();
		if (N1 == 0 || N2 == 0) return Constants.UNUSED;
		
		List<Double> bins = valueBins;
		if (bins.isEmpty())
			bins = extractValueBins(vRating1, vRating2);
		double sum = 0;
		for (double bin : bins) {
			int n1 = 0, n2 = 0;
			for (int id1 : ids1) {
				if (vRating1.get(id1).value == bin)
					n1++;
			}
			for (int id2 : ids2) {
				if (vRating2.get(id2).value == bin)
					n2++;
			}
			
			double thetaBias = mmdTheta(n1, N1) - mmdTheta(n2, N2);
			sum += thetaBias*thetaBias - 1/(0.5+n1) - 1/(0.5+n2); 
		}
		
		return 1 / (1 + sum/bins.size());
	}
	
	
	/**
	 * Theta transformation of Mean Measure of Divergence (MMD) measure.
	 * The default implementation is Grewal&apos; transformation.
	 * @param n number of observations having a trait.
	 * @param N number of observations
	 * @return Theta transformation of Mean Measure of Devergence (MMD) measure.
	 */
	protected double mmdTheta(int n, int N) {
		return 1 / Math.sin(1-2*(n/N));
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
	
	
	/**
	 * Calculating the Feng measure between two pairs.
	 * Junmei Feng, Xiaoyi Fengs, Ning Zhang, and Jinye Peng developed the Triangle measure. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Junmei Feng, Xiaoyi Fengs, Ning Zhang, Jinye Peng
	 * @return Feng measure between both two rating vectors and profiles.
	 */
	protected double feng(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		double s1 = coj(vRating1, vRating2, profile1, profile2);

		Set<Integer> ids1 = vRating1.fieldIds(true);
		Set<Integer> ids2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(ids1);
		common.retainAll(ids2);
		double s2 = 1 / ( 1 + Math.exp(-common.size()*common.size()/(ids1.size()*ids2.size())) );
		
		double s3 = urp(vRating1, vRating2, profile1, profile2);
		
		return s1 * s2 * s3;
	}
	
	
	/**
	 * Calculating the Mu measure between two pairs.
	 * Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, and Xiaohan Yin developed Mu measure. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, Xiaohan Yin
	 * @return Mu measure between both two rating vectors and profiles.
	 */
	@NextUpdate
	protected double mu(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double alpha = config.getAsReal(MU_ALPHA_FIELD);
		double pearson = corr(vRating1, vRating2, profile1, profile2);
		double hg = 1 - bc(vRating1, vRating2, profile1, profile2);
//		double hg = bc(vRating1, vRating2, profile1, profile2);
		double jaccard = jaccard(vRating1, vRating2, profile1, profile2);
		
		return alpha*pearson + (1-alpha)*(hg+jaccard);
	}
	
	
	/**
	 * Getting rating vector given column ID (item ID or user ID) for BCF measure.
	 * @param columnId specified column ID (item ID or user ID).
	 * @return rating vector given column ID (item ID or user ID).
	 */
	protected abstract RatingVector getColumnRating(int columnId);

	
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
	 * Checking whether two ratings are agreed.
	 * @param rating1 first rating.
	 * @param rating2 second rating.
	 * @return true if two ratings are agreed.
	 */
	protected boolean agree(double rating1, double rating2) {
		if ( (rating1 > this.ratingMedian && rating2 < this.ratingMedian) || (rating1 < this.ratingMedian && rating2 > this.ratingMedian) )
			return false;
		else
			return true;
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

	
	/**
	 * Converting value bins into rank bins.
	 * @param valueBins value bins
	 * @return rank bins.
	 */
	protected static Map<Double, Integer> convertValueBinsToRankBins(List<Double> valueBins) {
		if (valueBins == null || valueBins.size() == 0)
			return Util.newMap();
		
		Collections.sort(valueBins);
		Map<Double, Integer> rankBins = Util.newMap();
		int n = valueBins.size();
		for (int i = 0; i < n; i++) {
			rankBins.put(valueBins.get(i), n-i);
		}
		
		return rankBins;
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
	 * Extracting rank bins from two specified rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return Extracted rank bins from two specified rating vectors.
	 */
	protected static Map<Double, Integer> extractRankBins(RatingVector vRating1, RatingVector vRating2) {
		List<Double> valueBins = extractValueBins(vRating1, vRating2);
		return convertValueBinsToRankBins(valueBins);
	}
	
	
	/**
	 * Extracting value bins from configuration.
	 * @return extracted value bins from configuration.
	 */
	protected List<Double> extractConfigValueBins() {
		if (!getConfig().containsKey(VALUE_BINS_FIELD))
			return Util.newList();
		
		return TextParserUtil.parseListByClass(
				getConfig().getAsString(VALUE_BINS_FIELD),
				Double.class,
				",");
	}
	
	
	/**
	 * Extracting rank bins from configuration.
	 * @return extracted SRC rank bins from configuration.
	 */
	protected Map<Double, Integer> extractConfigRankBins() {
		List<Double> valueBins = extractConfigValueBins();
		return convertValueBinsToRankBins(valueBins);
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(MEASURE, getDefaultSimilarMeasure());
		tempConfig.put(COSINE_NORMALIZED_FIELD, COSINE_NORMALIZED_DEFAULT);
		tempConfig.put(VALUE_BINS_FIELD, VALUE_BINS_DEFAULT);
		tempConfig.put(BCF_MEDIAN_MODE_FIELD, BCF_MEDIAN_MODE_DEFAULT);
		tempConfig.put(MU_ALPHA_FIELD, MU_ALPHA_DEFAULT);
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
