package net.hudup.data.ext;

import java.util.Date;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.MetaFetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMulti;
import net.hudup.core.data.RatingTriple;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.UserRating;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.logistic.LogUtil;
import net.hudup.data.ProviderImpl;

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
	public Fetcher<RatingTriple> getRatings() {
		Fetcher<Profile> fetcher = assoc.getProfiles(
				getConfig().getRatingUnit(), null);
		
		return new MetaFetcher<Profile, RatingTriple>(fetcher) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public RatingTriple create(Profile u) {
				// TODO Auto-generated method stub
				if (u == null)
					return null;
				
				int userId = u.getValueAsInt(DataConfig.USERID_FIELD);
				int itemId = u.getValueAsInt(DataConfig.ITEMID_FIELD);
				double ratingValue = u.getValueAsReal(DataConfig.RATING_FIELD);
				if (userId < 0 || itemId < 0 || !Util.isUsed(ratingValue))
					return null;
				
				Rating rating = new Rating(ratingValue);
				RatingTriple triple = new RatingTriple(userId, itemId, rating);
				
				rating.ratedDate = u.getValueAsTime(DataConfig.RATING_DATE_FIELD); 
				
				ContextList contexts = ctsManager.getContexts(userId, itemId, rating.ratedDate);
				if (contexts != null && contexts.size() > 0)
					rating.contexts = contexts;
				
				return triple;
			}
		};
	}
	
	
	/**
	 * Inserting context with user identifier, item identifier, and rated date.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param context specified context.
	 * @param ratedDate rating date.
	 * @return whether update successfully.
	 */
	protected boolean insertContext(int userId, int itemId, Context context, long ratedDate) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getContextUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		profile.setValue(DataConfig.CTX_TEMPLATEID_FIELD, context.getTemplate().getId());
		profile.setValue(DataConfig.CTX_VALUE_FIELD, context.getValue());
		profile.setValue(DataConfig.RATING_DATE_FIELD, ratedDate);

		return assoc.insertProfile(getConfig().getContextUnit(), profile);
	}

	
	/**
	 * Updating context with user identifier and item identifier.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param context specified context.
	 * @param ratedDate rating date.
	 * @return update SQL
	 */
	@SuppressWarnings("unused")
	private boolean updateContext(int userId, int itemId, Context context, Date ratedDate) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getContextUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		profile.setValue(DataConfig.CTX_TEMPLATEID_FIELD, context.getTemplate().getId());
		profile.setValue(DataConfig.CTX_VALUE_FIELD, context.getValue());
		profile.setValue(DataConfig.RATING_DATE_FIELD, ratedDate);

		return assoc.updateProfile(getConfig().getContextUnit(), profile);
	}

	
	@Override
	public boolean insertRating(int userId, int itemId, Rating rating) {
		// TODO Auto-generated method stub
		if (rating instanceof RatingMulti) {
			boolean result = true;
			RatingMulti mrating = (RatingMulti)rating;
			for (int i = 0; i < mrating.size(); i++) {
				Rating r = mrating.get(0);
				result &= insertRating(userId, itemId, r); //Call recursively.
			}
			
			return result;
		}
		else {
			if (!rating.isRated())
				return false;
			
			boolean result = true;
			result &= insertRatingValue(userId, itemId, rating.value, rating.ratedDate);
			
			if (rating.contexts == null || rating.contexts.size() == 0)
				return result;
			
			for (int i = 0; i < rating.contexts.size(); i++) {
				Context context = rating.contexts.get(i);
				if (context.getValue() == null || context.getTemplate() == null || context.getTemplate().getId() == -1)
					continue;
				
				result &= insertContext(userId, itemId, context, rating.ratedDate);
			}
			
			return result;
		}
	}


	@Override
	public boolean updateRating(int userId, int itemId, Rating rating) {
		// TODO Auto-generated method stub
		if (rating instanceof RatingMulti) {
			boolean result = true;
			RatingMulti mrating = (RatingMulti)rating;
			for (int i = 0; i < mrating.size(); i++) {
				Rating r = mrating.get(0);
				result &= updateRating(userId, itemId, r); //Call recursively.
			}
			
			return result;
		}
		else {
			if (!rating.isRated())
				return false;
			
			boolean result = true;
			boolean update = containsRating(userId, itemId);
			if (update)
				result &= updateRatingValue(userId, itemId, rating.value, rating.ratedDate);
			else
				result &= insertRatingValue(userId, itemId, rating.value, rating.ratedDate);
			
			if (rating.contexts == null || rating.contexts.size() == 0)
				return result;
			
			for (int i = 0; i < rating.contexts.size(); i++) {
				Context context = rating.contexts.get(i);
				if (context.getValue() == null || context.getTemplate() == null || context.getTemplate().getId() == -1)
					continue;
				
				// Removing all context if update
				if (update) {
					Profile profile = new Profile(assoc.getAttributes(getConfig().getContextUnit()));
					profile.setValue(DataConfig.USERID_FIELD, userId);
					profile.setValue(DataConfig.ITEMID_FIELD, itemId);

					assoc.deleteProfile(getConfig().getContextUnit(), profile);
				}
				
				result &= insertContext(userId, itemId, context, rating.ratedDate);
			}
			
			return result;
		}
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

	
	@Override
	public boolean createSchema(AttributeList userAttList,
			AttributeList itemAttList) {
		// TODO Auto-generated method stub
		dropSchema();
		boolean result = true;
		
		DataConfig config = getConfig();
		
		
		//Creating configuration unit
		if (config.getConfigUnit() == null)
			config.setConfigUnit(DataConfig.CONFIG_UNIT);
		result &= createUnit(config.getConfigUnit(), AttributeList.defaultConfigAttributeList());
		
		
		// Creating user unit
		if (config.getUserUnit() == null)
			config.setUserUnit(DataConfig.USER_UNIT);
		//
		if (userAttList == null || userAttList.size() == 0) {
			userAttList = AttributeList.defaultUserAttributeList();
		}
		result &= assoc.createUnit(config.getUserUnit(), userAttList);
		
		
		// Creating item unit
		if (config.getItemUnit() == null)
			config.setItemUnit(DataConfig.ITEM_UNIT);
		//
		if (itemAttList == null || itemAttList.size() == 0) {
			itemAttList = AttributeList.defaultItemAttributeList();
		}
		result &= assoc.createUnit(config.getItemUnit(), itemAttList);
		
		
		// Creating rating unit
		if (config.getRatingUnit() == null)
			config.setRatingUnit(DataConfig.RATING_UNIT);
		result &= createUnit(config.getRatingUnit(), AttributeList.defaultRatingAttributeList2());

		
		// Creating nominal unit
		if (config.getNominalUnit() == null)
			config.setNominalUnit(DataConfig.NOMINAL_UNIT);
		//
		result &= createUnit(config.getNominalUnit(), AttributeList.defaultNominalAttributeList());

		
		// Creating account unit
		if (config.getAccountUnit() == null)
			config.setAccountUnit(DataConfig.ACCOUNT_UNIT);
		//
		result &= createUnit(config.getAccountUnit(), AttributeList.defaultAccountAttributeList());

		
		// Creating attribute map unit
		if (config.getAttributeMapUnit() == null)
			config.setAttributeMapUnit(DataConfig.ATTRIBUTE_MAP_UNIT);
		//
		//
		result &= createUnit(config.getAttributeMapUnit(), AttributeList.defaultAttributeMapAttributeList());


		// Creating context template unit
		if (config.getContextTemplateUnit() == null)
			config.setContextTemplateUnit(DataConfig.CONTEXT_TEMPLATE_UNIT);
		//
		result &= ctsManager.createContextTemplateUnit();
		
		
		// Creating context unit
		if (config.getContextUnit() == null)
			config.setContextUnit(DataConfig.CONTEXT_UNIT);
		//
		result &= createUnit(config.getContextUnit(), AttributeList.defaultContextAttributeList2());
		
		
		// Creating sample unit
		if (config.getSampleUnit() == null)
			config.setSampleUnit(DataConfig.SAMPLE_UNIT);
		//
		result &= createUnit(config.getSampleUnit(), AttributeList.defaultSampleAttributeList());

		
		// Updating minimum rating and maximum rating
		DatasetMetadata metadata = config.getMetadata();
		Profile cfgProfile = new Profile(getProfileAttributes(config.getConfigUnit()));
		cfgProfile.setValue(DataConfig.ATTRIBUTE_FIELD, DataConfig.MIN_RATING_FIELD);
		cfgProfile.setValue(DataConfig.ATTRIBUTE_VALUE_FIELD, metadata.minRating);
		result &= insertConfigProfile(cfgProfile);
		//
		cfgProfile.setValue(DataConfig.ATTRIBUTE_FIELD, DataConfig.MAX_RATING_FIELD);
		cfgProfile.setValue(DataConfig.ATTRIBUTE_VALUE_FIELD, metadata.maxRating);
		result &= insertConfigProfile(cfgProfile);
		
		
		// Updating administrator account
		Profile accProfile = new Profile(getProfileAttributes(config.getAccountUnit()));
		accProfile.setValue(DataConfig.ACCOUNT_NAME_FIELD, "admin");
		accProfile.setValue(DataConfig.ACCOUNT_PASSWORD_FIELD, Util.getCipher().md5Encrypt("admin"));
		accProfile.setValue(DataConfig.ACCOUNT_PRIVILEGES_FIELD, "255");
		result &= insertAccount(accProfile);
		
		return result;
	}
	
	
}
