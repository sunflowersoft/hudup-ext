package net.hudup.logistic.math;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class DatasetStatsProcessor implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * 
	 * @param dataset
	 */
	public DatasetStatsProcessor(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param rating
	 * @return probability
	 */
	public double probUser(int userId, double rating) {
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		double prob = prob(userId, rating, itemRatings, false);
		
		try {
			itemRatings.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prob;
	}
	
	
	/**
	 * 
	 * @param uRatings
	 * @return probability
	 */
	public double probUser(Collection<Pair> uRatings) {
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		double prob = prob(uRatings, itemRatings, false);
		
		try {
			itemRatings.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prob;
	}
	
	
	/**
	 * 
	 * @param itemId
	 * @param rating
	 * @return probability
	 */
	public double probItem(int itemId, double rating) {
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		double prob = prob(itemId, rating, userRatings, false);
		
		try {
			userRatings.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prob;
	}

	
	/**
	 * 
	 * @param iRatings
	 * @return probability
	 */
	public double probItem(Collection<Pair> iRatings) {
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		double prob = prob(iRatings, userRatings, false);
		
		try {
			userRatings.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prob;
	}
	
	
	/**
	 * 
	 * @param ratingValue
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
				prob = 0;
			else
				prob = (double)count / (double)total;
		}
		catch (Exception e) {
			e.printStackTrace();
			prob = 0;
		}
		finally {
			try {
				userRatings.close();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return prob;
	}
	
	
	/**
	 * 
	 * @param pairs
	 * @param ratings
	 * @param autoReset
	 * @return probability
	 */
	private double prob(Collection<Pair> pairs, Fetcher<RatingVector> ratings, boolean autoReset) {
		
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
				prob = 0;
			else
				prob = (double)count / (double)total;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			prob = 0;
		}
		finally {
			try {
				if (autoReset)
					ratings.reset();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return prob;
	}
	
	
	/**
	 * 
	 * @param id
	 * @param ratings
	 * @param autoReset
	 * @return probability
	 */
	private double prob(int id, double value, Fetcher<RatingVector> ratings, boolean autoReset) {
		List<Pair> pairs = Util.newList();
		Pair pair = new Pair(id, value);
		pairs.add(pair);
		
		return prob(pairs, ratings, autoReset);
		
	}
	
	
	/**
	 * 
	 * @param ratingValue1
	 * @param ratingValue2
	 * @return whether two rating values are equal
	 */
	protected boolean ratingEquals(double ratingValue1, double ratingValue2) {
		return ratingValue1 == ratingValue2;
	}
	
	
}
