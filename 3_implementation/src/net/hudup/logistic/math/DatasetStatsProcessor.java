/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.math;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;

/**
 * This is utility class which calculates probabilities on dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class DatasetStatsProcessor implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Specified dataset.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Constructor with specified dataset.
	 * @param dataset specified dataset.
	 */
	public DatasetStatsProcessor(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
	/**
	 * Calculating the probability that a user gives a rating value.
	 * @param userId given user id.
	 * @param rating rating value.
	 * @return probability that a given user gives a rating value.
	 */
	public double probUser(int userId, double rating) {
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		return prob(userId, rating, itemRatings, true);
	}
	
	
	/**
	 * Calculating probability of user ratings.
	 * @param uRatings collection of user rating pairs. Each pair have a key (item id) and a value (rating value).
	 * @return probability of user ratings.
	 */
	public double probUser(Collection<Pair> uRatings) {
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		return prob(uRatings, itemRatings, true);
	}
	
	
	/**
	 * Calculating the probability that an item receives a rating value.
	 * @param itemId item id.
	 * @param rating specified rating value.
	 * @return probability that an item receives a rating value.
	 */
	public double probItem(int itemId, double rating) {
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		return prob(itemId, rating, userRatings, true);
	}

	
	/**
	 * Calculating probability of item ratings.
	 * @param iRatings collection of item rating pairs. Each pair have a key (user id) and a value (rating value).
	 * @return probability of item ratings.
	 */
	public double probItem(Collection<Pair> iRatings) {
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		return prob(iRatings, userRatings, true);
	}
	
	
	/**
	 * Calculating probability of specified rating value.
	 * @param ratingValue specified rating value.
	 * @return probability of rating value
	 */
	public double probRating(double ratingValue) {
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		
		double prob = 0;
		try {
			
			long count = 0;
			long total = 0;
			while (userRatings.next()) {
				RatingVector userRating = userRatings.pick();
				if (userRating == null)
					continue;
				
				Collection<Rating> ratings = userRating.gets();
				for (Rating rating : ratings) {
					if (!rating.isRated())
						continue;
					
					total ++;
					if (ratingEquals(rating.value, ratingValue))
						count ++;
				}
			}
			
			if (total == 0)
				prob = Constants.UNUSED;
			else
				prob = (double)count / (double)total;
		}
		catch (Exception e) {
			LogUtil.trace(e);
			prob = Constants.UNUSED;
		}
		finally {
			try {
				userRatings.close();
			} 
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		return prob;
	}
	
	
	/**
	 * Calculating probability of (user or item) rating vectors on pairs.
	 * @param pairs collection of pairs. Each pair have a key (item id or user id) and a value (rating value).
	 * If rating vectors are user rating vectors, key is item id.
	 * If rating vectors are item rating vectors, key is user id.
	 * @param ratings fetcher of user rating vectors or item rating vectors.
	 * @param autoClose if true, the fetcher id closed automatically after the method finishes.
	 * @return probability of (user or item) rating vectors on pairs.
	 */
	private double prob(Collection<Pair> pairs, Fetcher<RatingVector> ratings, boolean autoClose) {
		
		double prob = 0;
		try {
			
			long count = 0;
			long total = 0;
			while (ratings.next()) {
				RatingVector rating = ratings.pick();
				if (rating == null)
					continue;
				
				total ++;
				
				boolean flag = true;
				for (Pair pair : pairs) {
					int id = pair.key();
					double value = pair.value();
					if (!rating.isRated(id)) {
						flag = false;
						break;
					}
					
					if (!ratingEquals(rating.get(id).value, value)) {
						flag = false;
						break;
					}
				}
				
				if (flag)
					count ++;
			}
			
			if (total == 0)
				prob = Constants.UNUSED;
			else
				prob = (double)count / (double)total;
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
			prob = Constants.UNUSED;
		}
		finally {
			try {
				if (autoClose) ratings.close();
			} 
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		return prob;
	}
	
	
	/**
	 * Calculating probability of a user (or an item) specified by id on rating vector.
	 * @param id id of a user (or an item).
	 * @param ratings rating vector of a user (or an item).
	 * @param autoClose if true, the fetcher id closed automatically after the method finishes.
	 * @return probability of a user (or an item) specified by id on rating vector.
	 */
	private double prob(int id, double value, Fetcher<RatingVector> ratings, boolean autoClose) {
		List<Pair> pairs = Util.newList();
		Pair pair = new Pair(id, value);
		pairs.add(pair);
		
		return prob(pairs, ratings, autoClose);
		
	}
	
	
	/**
	 * Comparing two rating values.
	 * @param ratingValue1 rating value 1.
	 * @param ratingValue2 rating value 2.
	 * @return whether two rating values are equal.
	 */
	protected boolean ratingEquals(double ratingValue1, double ratingValue2) {
		return ratingValue1 == ratingValue2;
	}
	
	
}
