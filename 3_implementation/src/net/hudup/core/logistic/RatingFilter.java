/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This interface, called {@code rating filter} specifies how to select (filter) a rating value based on comparing such rating value with a referred rating value.
 * Any class that implements this interface must define the method {@link #accept(double, double)} to make such comparison or filtering).
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public interface RatingFilter {
	
	
	/**
	 * Any class that implements this interface must define this method to select (filter) a rating value based on comparing such rating value with a referred rating value..
	 * 
	 * @param ratingValue specified rating value.
	 * @param referredRatingValue referred rating value.
	 * 
	 * @return whether specified value accepted as filter value
	 */
	boolean accept(double ratingValue, double referredRatingValue);
	
	
}


