/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.util.List;

/**
 * This class is an abstract knowledge base for recommendation.
 * For example, method {@link #estimate(int, int)} estimates a rating value of specified user giving on specified item.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class KBaseRecommend extends KBaseAbstract {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	protected KBaseRecommend() {

	}


	/**
	 * Getting list of item identifiers.
	 * @return list of item id (s)
	 */
	public abstract List<Integer> getItemIds();


	/**
	 * This method estimates a rating value of specified user giving on specified item.
	 * @param userId specified user identifier (user ID).
	 * @param itemId specified item identifier (item ID).
	 * @return estimated rating value.
	 */
	public abstract double estimate(int userId, int itemId);
	
	
}
