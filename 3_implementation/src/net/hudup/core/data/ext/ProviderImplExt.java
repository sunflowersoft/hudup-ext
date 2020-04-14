/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ext;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMulti;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.UserRating;
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
		// TODO Auto-generated constructor stub
	}


	@Override
	public RatingVector getUserRatingVector(int userId) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
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
