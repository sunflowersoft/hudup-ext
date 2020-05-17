/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import net.hudup.core.Util;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is an extension of provider implementation which supports dyadic data.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class ProviderImplExt extends ProviderImpl {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with configuration.
	 * @param config configuration.
	 */
	public ProviderImplExt(DataConfig config) {
		super(config);
	}


	@Override
	public RatingVector getUserRatingVector(int userId) {
		
		RatingVector user = new UserRating(userId);
		
		Profile select = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		select.setValue(DataConfig.USERID_FIELD, userId);
		Fetcher<Profile> fetcher = assoc.getProfiles(
				getConfig().getRatingUnit(), select);
		try {
			
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int itemId = profile.getValueAsInt(DataConfig.ITEMID_FIELD);
				double ratingValue = profile.getValueAsReal(DataConfig.RATING_FIELD);
				if (itemId < 0 || !Util.isUsed(ratingValue))
					continue;
				
				Rating rating = new Rating(ratingValue);
				
				rating.ratedDate = profile.getValueAsTime(DataConfig.RATING_DATE_FIELD); 
				
				ContextList contexts = ctsManager.getContexts(userId, itemId, rating.ratedDate);
				if (contexts != null && contexts.size() > 0)
					rating.contexts = contexts;
				
				RatingMulti mrating = null;
				if (user.contains(itemId)) {
					mrating = (RatingMulti)user.get(itemId);
					mrating.addRating(mrating);
				}
				else {
					mrating = new RatingMulti(rating);
					user.put(itemId, mrating);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		if (user.size() == 0)
			return null;
		else
			return user;
	}


	@Override
	public RatingVector getItemRatingVector(int itemId) {
		RatingVector item = new ItemRating(itemId);
		
		Profile select = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		select.setValue(DataConfig.USERID_FIELD, itemId);
		Fetcher<Profile> fetcher = assoc.getProfiles(
				getConfig().getRatingUnit(), select);
		try {
			
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int userId = profile.getValueAsInt(DataConfig.USERID_FIELD);
				double ratingValue = profile.getValueAsReal(DataConfig.RATING_FIELD);
				if (userId < 0 || !Util.isUsed(ratingValue))
					continue;
				
				Rating rating = new Rating(ratingValue);
				
				rating.ratedDate = profile.getValueAsTime(DataConfig.RATING_DATE_FIELD); 
				
				ContextList contexts = ctsManager.getContexts(userId, itemId, rating.ratedDate);
				if (contexts != null && contexts.size() > 0)
					rating.contexts = contexts;
				
				RatingMulti mrating = null;
				if (item.contains(userId)) {
					mrating = (RatingMulti)item.get(userId);
					mrating.addRating(mrating);
				}
				else {
					mrating = new RatingMulti(rating);
					item.put(userId, mrating);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		if (item.size() == 0)
			return null;
		else
			return item;
	}

	
}
