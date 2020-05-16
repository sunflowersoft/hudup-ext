/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;

/**
 * This class represents a semi-scanner which is a scanner but store frequent-access rating matrix in memory.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SemiScanner extends ScannerImpl {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * In-memory user rating matrix.
	 */
	protected Map<Integer, RatingVector> userRatingMap = null;

	
	/**
	 * In-memory item rating matrix.
	 */
	protected Map<Integer, RatingVector> itemRatingMap = null;

	
	/**
	 * Indicating whether or not rating matrix is loaded.
	 */
	protected boolean loaded = false;

	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public SemiScanner(DataConfig config) {
		super(config);
		
		loadRatingData();
	}


	/**
	 * Loading rating matrix.
	 */
	protected void loadRatingData() {
		
		loaded = false;
		
		if (userRatingMap == null)
			userRatingMap = Util.newMap();
		else
			userRatingMap.clear();
		Fetcher<RatingVector> uFetcher = super.fetchUserRatings();
		userRatingMap.putAll(RatingVector.transfer(uFetcher, false, true));
		
		if (itemRatingMap == null)
			itemRatingMap = Util.newMap();
		else
			itemRatingMap.clear();
		Collection<RatingVector> users = userRatingMap.values();
		for (RatingVector user : users) {
			if (user == null || user.size() == 0)
				continue;
			
			Set<Integer> itemIds = user.fieldIds();
			for (int itemId : itemIds) {
				if (!user.isRated(itemId))
					continue;
				
				Rating rating = user.get(itemId);
				RatingVector item = itemRatingMap.get(itemId);
				if (item == null) {
					item = new ItemRating(itemId);
					itemRatingMap.put(itemId, item);
				}
				item.put(user.id(), rating);
			}
				
		}
		
		loaded = true;
	}
	
	
	@Override
	public Fetcher<Integer> fetchUserIds() {
		if (!loaded)
			return super.fetchUserIds();
		else
			return new MemFetcher<Integer>(userRatingMap.keySet());
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		if (!loaded)
			return super.fetchItemIds();
		else
			return new MemFetcher<Integer>(itemRatingMap.keySet());
	}
	
	
	@Override
	public Rating getRating(int userId, int itemId) {
		if (!loaded)
			return super.getRating(userId, itemId);
		else {
			if (!userRatingMap.containsKey(userId))
				return null;
			RatingVector user = userRatingMap.get(userId);
			return user.get(itemId);
		}
	}
	

	@Override
	public RatingVector getUserRating(int userId) {
		if (!loaded)
			return super.getUserRating(userId);
		else if (!userRatingMap.containsKey(userId))
			return null;
		else
			return userRatingMap.get(userId);
	}
	

	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		if (!loaded)
			return super.fetchUserRatings();
		else
			return new MemFetcher<RatingVector>(userRatingMap.values());
	}
	

	@Override
	public RatingVector getItemRating(int itemId) {
		if (!loaded)
			return super.getItemRating(itemId);
		else if (!itemRatingMap.containsKey(itemId))
			return null;
		else
			return itemRatingMap.get(itemId);
	}

	
	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		if (!loaded)
			return super.fetchItemRatings();
		else
			return new MemFetcher<RatingVector>(itemRatingMap.values());
	}

	
	/**
	 * Enhancing internal data structures.
	 */
	public void enhance() {
		userRatingMap.keySet();
		userRatingMap.values();
		userRatingMap.entrySet();
		
		itemRatingMap.keySet();
		itemRatingMap.values();
		itemRatingMap.entrySet();
	}

	
	@Override
	public Object clone() {
		SemiScanner scanner = new SemiScanner(config);
		scanner.enhance();
		
		return scanner;
	}


	@Override
	public void clear() {
		super.clear();
		
		itemRatingMap.clear();
		userRatingMap.clear();
	}

	
}
