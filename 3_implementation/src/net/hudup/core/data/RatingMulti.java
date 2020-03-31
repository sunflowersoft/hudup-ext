/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.Date;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;

/**
 * This class is firstly similar to {@link Rating} contains ratings of one user giving on many items or ratings of one item receiving from many users.
 * However, it stores the history of ratings that users have given on items. Every time a user rates on an item, such rating value is added into the history and so there is no overridden rating copying as {@link RatingVector} does.
 * Such history is referred by its new variable {@link #history}.
 * Programmers can keep track the long process that users have rated on items.
 * In current implementation, this class is still simple but it is very potential for innovative learning algorithms that takes advantages of the history of ratings for recommendation.
 * 
 * @author Loc Nguyen
 * @version 11
 *
 */
public class RatingMulti extends Rating {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * History of ratings that users have given on items.
	 */
	protected List<Rating> history = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	protected RatingMulti() {
		super();
	}
	
	
	/**
	 * Constructor with specified rating value.
	 * @param ratingValue specified rating value.
	 */
	public RatingMulti(double ratingValue) {
		this(new Rating(ratingValue));
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Constructor with specified rating.
	 * @param rating specified rating.
	 */
	public RatingMulti(Rating rating) {
//		if (!(history instanceof Serializable))
//			throw new RuntimeException("List is not serializable");
		
		addRating(rating);
	}
	
	
	/**
	 * Adding a rating into the rating history.
	 * @param rating specified rating.
	 */
	public void addRating(Rating rating) {
		int n = history.size();
		int index = -1;
		for (int i = n-1; i >= 0; i--) {
			if(history.get(i).ratedDate < rating.ratedDate) {
				index = i;
				break;
			}
		}
		
		if (index == -1)
			history.add(0, rating);
		else
			history.add(index + 1, rating);
		
		updateCurrentRating();
	}
	
	
	/**
	 * Although this class keeps a history of ratings in variable {@link #history}, there is always a current rating that user gave on item latest.
	 * Such rating is called current rating. This method updates the current rating.
	 */
	protected void updateCurrentRating() {
		int n = history.size();
		if (n > 0) {
			Rating lastRating = history.get(n-1);
			this.value = lastRating.value;
			this.contexts = lastRating.contexts;
			this.ratedDate = lastRating.ratedDate;
		}
		else {
			this.value = Constants.UNUSED;
			this.contexts.clear();
			this.ratedDate = new Date().getTime();
		}
	}
	
	
	/**
	 * Getting the size of rating history.
	 * @return size of rating history.
	 */
	public int size() {
		return history.size();
	}
	
	
	/**
	 * Getting the rating in history at specified index.
	 * @param index specified index.
	 * @return rating in history at specified index.
	 */
	public Rating get(int index) {
		return history.get(index);
	}
	
	
	/**
	 * Removing the rating in history at specified index.
	 * @param index specified index.
	 * @return the removed rating.
	 */
	public Rating remove(int index) {
		Rating removed = history.remove(index);
		updateCurrentRating();
		return removed;
	}
	
	
	@Override
	public List<Rating> getRatingList() {
		// TODO Auto-generated method stub
		if (history.size() > 0)
			return history;
		else
			return super.getRatingList();
	}


	/**
	 * Clearing this {@link RatingMulti}, which means that the rating history is make empty.
	 */
	public void clear() {
		history.clear();
		updateCurrentRating();
	}
	
	
	@Override
	public Object clone() {
		List<Rating> history = Util.newList(this.history.size());
		for (Rating rating : this.history) {
			Rating clonedRating = (Rating)rating.clone();
			history.add(clonedRating);
		}
		
		RatingMulti clonedRating = new RatingMulti();
		clonedRating.history = history;
		clonedRating.updateCurrentRating();
		
		return clonedRating;
	}


	@Override
	public void assign(Rating that) {
		// TODO Auto-generated method stub
		if (that instanceof RatingMulti) {
			this.history = ((RatingMulti)that).history;
		}
		else {
			this.history.clear();
			this.history.add(that);
		}
		
		this.updateCurrentRating();
	}

	
}
