/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.DefaultCTSManager;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.Indicator;
import net.hudup.core.parser.ScannerParser;

/**
 * This class is the default implementation of {@link Provider} to process dataset.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProviderImpl implements Provider {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Associator of  provider.
	 */
	protected ProviderAssoc assoc = null;
	
	
	/**
	 * Manager of Context template schema.
	 */
	protected CTSManager ctsManager = null;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public ProviderImpl(DataConfig config) {
		this.assoc = Util.getFactory().createProviderAssoc(config);
		
		try {
			complete();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Completing the initialization.
	 */
	private void complete() throws Exception {
		UnitList unitList = getUnitList();
		DataConfig config = getConfig();
		
		config.fillUnitList(unitList);
		config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		
		if ( config.getConfigUnit() != null && 
				unitList.contains(config.getConfigUnit()) ) {
		
			Profile minCondition = new Profile(assoc.getAttributes(getConfig().getConfigUnit()));
			minCondition.setValue(DataConfig.ATTRIBUTE_FIELD, DataConfig.MIN_RATING_FIELD);
			Profile minProfile = getProfile(config.getConfigUnit(), minCondition);
			
			Profile maxCondition = new Profile(assoc.getAttributes(getConfig().getConfigUnit()));
			maxCondition.setValue(DataConfig.ATTRIBUTE_FIELD, DataConfig.MAX_RATING_FIELD);
			Profile maxProfile = getProfile(config.getConfigUnit(), maxCondition);
			
			if (minProfile != null && maxProfile != null) {
				double min = minProfile.getValueAsReal(DataConfig.ATTRIBUTE_VALUE_FIELD);
				double max = maxProfile.getValueAsReal(DataConfig.ATTRIBUTE_VALUE_FIELD);
				if (Util.isUsed(min) && Util.isUsed(max)) {
					DatasetMetadata metadata = config.getMetadata();
					metadata.minRating = min;
					metadata.maxRating = max;
					config.setMetadata(metadata);
				}
			}
		} // End if
		
		
		String ctsmName = config.getAsString(DataConfig.CTS_MANAGER_NAME_FIELD);
		if (ctsmName == null)
			ctsManager = new DefaultCTSManager();
		else {
			ctsManager = (CTSManager) PluginStorage.getCTSManagerReg().query(ctsmName);
			ctsManager = ctsManager == null ? new DefaultCTSManager() : (CTSManager) ctsManager.newInstance();
		}
		
		config.put(DataConfig.CTS_MANAGER_NAME_FIELD, ctsManager.getName());
		ctsManager.setup(config);
	}

	
	@Override
	public CTSManager getCTSManager() {
		return ctsManager;
	}

	
	@Override
	public DataConfig getConfig() {
		return assoc.getConfig();
	}

	
	@Override
	public ProviderAssoc getAssoc() {
		return assoc;
	}


	/**
	 * Testing whether dataset contains specified configuration attribute.
	 * @param cfgAttribute specified configuration attribute.
	 * @return whether contains configuration attribute.
	 */
	protected boolean containsConfigProfile(String cfgAttribute) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getConfigUnit()));
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, cfgAttribute);
		
		return assoc.containsProfile(getConfig().getConfigUnit(), profile);
	}
	
	
	/**
	 * Testing whether dataset contains specified user id.
	 * @param userId specified user id.
	 * @return whether contains user id.
	 */
	protected boolean containsUserProfile(int userId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getUserUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		
		return assoc.containsProfile(getConfig().getUserUnit(), profile);
	}
	

	/**
	 * Testing whether dataset contains specified item id.
	 * @param itemId specified item id.
	 * @return whether contains item id.
	 */
	protected boolean containsItemProfile(int itemId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getItemUnit()));
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		
		return assoc.containsProfile(getConfig().getItemUnit(), profile);
	}

	
	/** 
	 * Testing whether dataset contains rating at specified user id and item id.
	 * @param userId specified user id.
	 * @param itemId specified item id.
	 * @return whether contains rating of user id and item id
	 */
	protected boolean containsRating(int userId, int itemId) {
		
		Profile profile = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		
		return assoc.containsProfile(getConfig().getRatingUnit(), profile);
	}
	
	
	/**
	 * Testing whether dataset contains nominal at specified unit, attribute name, and nominal index.
	 * @param unitName specified unit.
	 * @param attName specified attribute name.
	 * @param nominalIdx specified nominal index.
	 * @return whether contains nominal at specified unit, attribute name, and nominal index.
	 */
	protected boolean containsNominal(String unitName, String attName, int nominalIdx) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getNominalUnit()));

		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, unitName);
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, attName);
		profile.setValue(DataConfig.NOMINAL_INDEX_FIELD, nominalIdx);
		
		return assoc.containsProfile(getConfig().getNominalUnit(), profile);
	}
	
	
	/**
	 * Testing whether dataset contains specified internal record.
	 * @param internalRecord specified internal record.
	 * @return whether contains specified internal record.
	 */
	protected boolean containsAttributeMap(InternalRecord internalRecord) {
		if (!internalRecord.isValid())
			return false;;
		
		Profile profile = new Profile(
			assoc.getAttributes(getConfig().getAttributeMapUnit()));
		profile.setValue(DataConfig.INTERNAL_UNIT_FIELD, internalRecord.unit);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, internalRecord.attribute);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, internalRecord.value);
		
		return assoc.containsProfile(getConfig().getAttributeMapUnit(), profile);
	}
	
	
	/**
	 * Testing whether dataset contains specified account.
	 * @param accName specified account name.
	 * @return whether contains specified account.
	 */
	protected boolean containsAccount(String accName) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getAccountUnit()));
		profile.setValue(DataConfig.ACCOUNT_NAME_FIELD, accName);
		
		return assoc.containsProfile(getConfig().getAccountUnit(), profile);
	}

	
	/**
	 * Testing whether dataset contains specified context template.
	 * @param ctxTemplateId specified context template id.
	 * @return whether contains specified context template.
	 */
	protected boolean containsContextTemplate(int ctxTemplateId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getContextTemplateUnit()));
		profile.setValue(DataConfig.CTX_TEMPLATEID_FIELD, ctxTemplateId);
		
		return assoc.containsProfile(getConfig().getContextTemplateUnit(), profile);
	}

	
	/**
	 * Testing whether dataset contains specified context template along with specified user id and item id.
	 * @param userId specified user id.
	 * @param itemId specified item id
	 * @param ctxTemplateId specified context template id.
	 * @return whether contains specified context template along with specified user id and item id.
	 */
	protected boolean containsContext(int userId, int itemId, int ctxTemplateId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getContextUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		profile.setValue(DataConfig.CTX_TEMPLATEID_FIELD, ctxTemplateId);
		
		return assoc.containsProfile(getConfig().getContextUnit(), profile);
	}

	
	@Override
	public Profile getConfigProfile(String cfgAttribute) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getConfigUnit()));
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, cfgAttribute);
		
		return assoc.getProfile(getConfig().getConfigUnit(), profile);
	}

	
	@Override
	public boolean insertConfigProfile(Profile cfgProfile) {
		return assoc.insertProfile(getConfig().getConfigUnit(), cfgProfile);
	}

	
	@Override
	public boolean updateConfigProfile(Profile cfgProfile) {
		Object idValue = cfgProfile.getIdValue();
		String att = (idValue == null ? null : idValue.toString());
		
		if (att != null && containsConfigProfile(att))
			return assoc.updateProfile(getConfig().getConfigUnit(), cfgProfile);
		else
			return insertConfigProfile(cfgProfile);
	}

	
	@Override
	public boolean deleteConfigProfile(String cfgAttribute) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getConfigUnit()));
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, cfgAttribute);
		
		return assoc.deleteProfile(getConfig().getConfigUnit(), profile);
	}

	
	@Override
	public Fetcher<RatingTriple> getRatings() {
		Fetcher<Profile> fetcher = assoc.getProfiles(getConfig().getRatingUnit(), null);
		
		return new MetaFetcher<Profile, RatingTriple>(fetcher) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public RatingTriple create(Profile u) {
				return RatingTriple.create(u, ctsManager);
			}
		};
	}
	
	
	
	@Override
	public Collection<RatingTriple> getRatings2() {
		Collection<Profile> ratings = assoc.getProfiles2(getConfig().getRatingUnit(), null);
		List<RatingTriple> triples = Util.newList(ratings.size());
		for (Profile rating : ratings) {
			RatingTriple triple = RatingTriple.create(rating, ctsManager);
			if (triple != null) triples.add(triple);
		}
		
		return triples;
	}


	/**
	 * Inserting rating value with user identifier, item identifier, and rating date.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param ratingValue rating value.
	 * @param ratedDate rating date.
	 * @return whether insert successfully.
	 */
	protected boolean insertRatingValue(int userId, int itemId, double ratingValue, long ratedDate) {
		if (!containsUserProfile(userId)) {
			Profile user = new Profile(getUserAttributes());
			user.setValue(DataConfig.USERID_FIELD, userId);
			insertUserProfile(user, null);
		}
		
		if (!containsItemProfile(itemId)) {
			Profile item = new Profile(getItemAttributes());
			item.setValue(DataConfig.ITEMID_FIELD, itemId);
			insertItemProfile(item, null);
		}
		
		Profile profile = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		profile.setValue(DataConfig.RATING_FIELD, ratingValue);
		profile.setValue(DataConfig.RATING_DATE_FIELD, ratedDate);
		
		return assoc.insertProfile(getConfig().getRatingUnit(), profile);
	}

	
	/**
	 * Updating rating value with user identifier, item identifier, and rating date.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param ratingValue rating value.
	 * @param ratedDate rating date.
	 * @return whether update successfully 
	 */
	protected boolean updateRatingValue(int userId, int itemId, double ratingValue, long ratedDate) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		profile.setValue(DataConfig.RATING_FIELD, ratingValue);
		profile.setValue(DataConfig.RATING_DATE_FIELD, ratedDate);
		
		return assoc.updateProfile(getConfig().getRatingUnit(), profile);
	}
	
	
	/**
	 * Inserting context with user identifier, item identifier, and rated date.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param context specified context.
	 * @param ratedDate rating date.
	 * @return whether insert successfully.
	 */
	private boolean insertContext(int userId, int itemId, Context context, long ratedDate) {
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
	 * @return whether update successfully.
	 */
	@SuppressWarnings("unused")
	private boolean updateContext(int userId, int itemId, Context context, long ratedDate) {
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
	public boolean insertRating(RatingVector vRating) {
		boolean result = true;

		Set<Integer> fieldIds = vRating.fieldIds(true);
		for (int fieldId : fieldIds) {
			Rating rating = vRating.get(fieldId);
			int userId = (vRating instanceof UserRating ? vRating.id() : fieldId);
			int itemId = (vRating instanceof UserRating ? fieldId : vRating.id());
			
			result &= insertRating(userId, itemId, rating);
		}
		
		return result;
	}


	@Override
	public boolean updateRating(int userId, int itemId, Rating rating) {
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
	public boolean updateRating(RatingVector vRating) {
		Set<Integer> fieldIds = vRating.fieldIds(true);
		boolean result = true;
		
		for (int fieldId : fieldIds) {
			Rating rating = vRating.get(fieldId);
			int userId = (vRating instanceof UserRating ? vRating.id() : fieldId);
			int itemId = (vRating instanceof UserRating ? fieldId : vRating.id());
			
			result &= updateRating(userId, itemId, rating);
		}
		
		return result;
	}

	
	/**
	 * Deleting ratings of specified user identifier and item identifier.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @return whether delete rating successfully.
	 */
	private boolean deleteRating(int userId, int itemId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getContextUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);

		return assoc.deleteProfile(getConfig().getRatingUnit(), profile);
	}

	
	@Override
	public boolean deleteRating(RatingVector vRating) {
		int id = vRating.id();
		
		Set<Integer> fieldIds = vRating.fieldIds();
		
		boolean result = true;
		for (int fieldId : fieldIds) {
			if (vRating instanceof UserRating)
				result &= deleteRating(id, fieldId);
			else
				result &= deleteRating(fieldId, id);
		}
		
		return result;
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
				
				user.put(itemId, ratingValue);
				Rating rating = user.get(itemId);
				
				rating.ratedDate = profile.getValueAsTime(DataConfig.RATING_DATE_FIELD); 
				
				ContextList contexts = ctsManager.getContexts(userId, itemId);
				if (contexts != null && contexts.size() > 0)
					rating.contexts = contexts;
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
	public boolean deleteUserRating(int userId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		
		return assoc.deleteProfile(getConfig().getRatingUnit(), profile);
	}

	
	@Override
	public Profile getUserProfile(int userId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getUserUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		
		return assoc.getProfile(getConfig().getUserUnit(), profile);
	}

	
	@Override
	public AttributeList getUserAttributes() {
		return assoc.getAttributes(getConfig().getUserUnit());
	}


	@Override
	public InterchangeAttributeMap getUserAttributeMap(int userId) {
		InternalRecord internalRecord = new InternalRecord(
				getConfig().getUserUnit(), DataConfig.USERID_FIELD, userId);
		return getAttributeMap(internalRecord);
	}
	
	
	@Override
	public boolean insertUserProfile(Profile user, ExternalRecord externalRecord) {
		int userId = user.getIdValueAsInt();
		Attribute userIdAtt = user.getIdAtt();
		
		DataConfig config = getConfig();
		if (userId < 0 && userIdAtt != null) {
			if (!userIdAtt.isAutoInc()) {
				int maxId = assoc.getProfileMaxId(config.getUserUnit());
				if (maxId <= 0)
					userId = 1;
				else
					userId = maxId + 1;
					
				user.setValue(DataConfig.USERID_FIELD, userId);
			}
			
		}
		
		boolean result = assoc.insertProfile(config.getUserUnit(), user);
		
		if (result && userIdAtt != null && externalRecord != null && externalRecord.isValid()) {
			if (userId < 0)
				userId = assoc.getProfileMaxId(config.getUserUnit());
			
			InternalRecord internalRecord = new InternalRecord(
					config.getUserUnit(), 
					DataConfig.USERID_FIELD, 
					userId);
			
			InterchangeAttributeMap attributeMap = new InterchangeAttributeMap(internalRecord, externalRecord);
			result &= updateAttributeMap(attributeMap);
		}

		return result;
	}

	
	@Override
	public boolean updateUserProfile(Profile user) {
		if (containsUserProfile(user.getIdValueAsInt()))
			return assoc.updateProfile(getConfig().getUserUnit(), user);
		else
			return assoc.insertProfile(getConfig().getUserUnit(), user);
	}


	@Override
	public boolean deleteUserProfile(int userId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getUserUnit()));
		profile.setValue(DataConfig.USERID_FIELD, userId);
		
		return assoc.deleteProfile(getConfig().getUserUnit(), profile);
	}

	
	@Override
	public RecommendParam getRecommendParam(int userId) {
		RatingVector vRating = getUserRatingVector(userId);
		Profile profile = getUserProfile(userId);
		
		return new RecommendParam(vRating, profile);
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
				
				item.put(userId, ratingValue);
				Rating rating = item.get(userId);
				
				rating.ratedDate = profile.getValueAsTime(DataConfig.RATING_DATE_FIELD); 
				
				ContextList contexts = ctsManager.getContexts(userId, itemId);
				if (contexts != null && contexts.size() > 0)
					rating.contexts = contexts;
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
	public boolean deleteItemRating(int itemId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getRatingUnit()));
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		
		return assoc.deleteProfile(getConfig().getRatingUnit(), profile);
	}

	
	@Override
	public Profile getItemProfile(int itemId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getItemUnit()));
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		
		return assoc.getProfile(getConfig().getItemUnit(), profile);
	}

	
	@Override
	public AttributeList getItemAttributes() {
		return assoc.getAttributes(getConfig().getItemUnit());
	}

	
	@Override
	public InterchangeAttributeMap getItemAttributeMap(int itemId) {
		InternalRecord internalRecord = new InternalRecord(
				getConfig().getItemUnit(), DataConfig.ITEMID_FIELD, itemId);
		return getAttributeMap(internalRecord);
	}

	
	@Override
	public boolean insertItemProfile(Profile item, ExternalRecord externalRecord) {
		int itemId = item.getIdValueAsInt();
		Attribute itemIdAtt = item.getIdAtt();

		DataConfig config = getConfig();
		if (itemId < 0 && itemIdAtt != null) {
			if (!itemIdAtt.isAutoInc()) {
				int maxId = assoc.getProfileMaxId(config.getItemUnit());
				if (maxId <= 0)
					itemId = 1;
				else
					itemId = maxId + 1;

				item.setValue(DataConfig.ITEMID_FIELD, itemId);
			}
			
		}
		
		boolean result = assoc.insertProfile(config.getItemUnit(), item);
		
		if (result && itemIdAtt != null && externalRecord != null && externalRecord.isValid()) {
			if (itemId < 0)
				itemId = assoc.getProfileMaxId(config.getItemUnit());

			InternalRecord internalRecord = new InternalRecord(
					config.getItemUnit(), 
					DataConfig.ITEMID_FIELD, 
					itemId);
			
			InterchangeAttributeMap attributeMap = new InterchangeAttributeMap(internalRecord, externalRecord);
			result &= updateAttributeMap(attributeMap);
		}
		
		return result;
	}

	
	@Override
	public boolean updateItemProfile(Profile item) {
		if (containsItemProfile(item.getIdValueAsInt()))
			return assoc.updateProfile(getConfig().getItemUnit(), item);
		else
			return assoc.insertProfile(getConfig().getItemUnit(), item);
	}

	
	@Override
	public boolean deleteItemProfile(int itemId) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getItemUnit()));
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		
		return assoc.deleteProfile(getConfig().getItemUnit(), profile);
	}

	
	@Override
	public NominalList getNominalList(String unitName, String attribute) {
		return assoc.getNominalList(unitName, attribute);
	}

	
	@Override
	public boolean insertNominal(String unitName, String attName,
			Nominal nominal) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getNominalUnit()));
		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, unitName);
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, attName);
		profile.setValue(DataConfig.NOMINAL_INDEX_FIELD, nominal.getIndex());
		profile.setValue(DataConfig.NOMINAL_VALUE_FIELD, nominal.getValue());
		profile.setValue(DataConfig.NOMINAL_PARENT_INDEX_FIELD, nominal.getParentIndex());
		
		return assoc.insertProfile(getConfig().getNominalUnit(), profile);
	}


	@Override
	public boolean updateNominal(String unitName, String attName,
			Nominal nominal) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getNominalUnit()));
		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, unitName);
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, attName);
		profile.setValue(DataConfig.NOMINAL_INDEX_FIELD, nominal.getIndex());
		profile.setValue(DataConfig.NOMINAL_VALUE_FIELD, nominal.getValue());
		profile.setValue(DataConfig.NOMINAL_PARENT_INDEX_FIELD, nominal.getParentIndex());
		
		if (containsNominal(unitName, attName, nominal.getIndex()))
			return assoc.updateProfile(getConfig().getNominalUnit(), profile);
		else
			return assoc.insertProfile(getConfig().getNominalUnit(), profile);
	}

	
	@Override
	public boolean deleteNominal(String unitName, String attName) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getNominalUnit()));
		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, unitName);
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, attName);
		
		return assoc.deleteProfile(getConfig().getNominalUnit(), profile);
	}

	
	@Override
	public boolean deleteNominal(String unitName) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getNominalUnit()));
		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, unitName);
		
		return assoc.deleteProfile(getConfig().getNominalUnit(), profile);
	}

	
	@Override
	public InterchangeAttributeMap getAttributeMap(InternalRecord internalRecord) {
		if (!internalRecord.isValid())
			return null;
		
		Profile condition = new Profile(
			assoc.getAttributes(getConfig().getAttributeMapUnit()));
		condition.setValue(DataConfig.INTERNAL_UNIT_FIELD, internalRecord.unit);
		condition.setValue(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, internalRecord.attribute);
		condition.setValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, internalRecord.value);
		
		Profile profile = assoc.getProfile(getConfig().getAttributeMapUnit(), condition);
		if (profile == null)
			return null;
		
		InterchangeAttributeMap attributeMap = new InterchangeAttributeMap();
		attributeMap.internalRecord = internalRecord;
		
		String externalUnit = profile.getValueAsString(DataConfig.EXTERNAL_UNIT_FIELD);
		String externalAtt = profile.getValueAsString(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD);
		Object externalValue = profile.getValue(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD);
		if (!(externalValue instanceof Serializable))
			return null;
		
		attributeMap.externalRecord = new ExternalRecord(externalUnit, externalAtt, (Serializable) externalValue);
		if (!attributeMap.isValid())
			return null;
		else
			return attributeMap;
		
	}

	
	@Override
	public InterchangeAttributeMap getAttributeMapByExternal(
			ExternalRecord externalRecord) {
		if (!externalRecord.isValid())
			return null;
		
		Profile condition = new Profile(
				assoc.getAttributes(getConfig().getAttributeMapUnit()));
		condition.setValue(DataConfig.EXTERNAL_UNIT_FIELD, externalRecord.unit);
		condition.setValue(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD, externalRecord.attribute);
		condition.setValue(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD, externalRecord.value);
			
		Fetcher<Profile> fetcher = assoc.getProfiles(getConfig().getAttributeMapUnit(), condition);
		InterchangeAttributeMap attributeMap = null;
		try {
			if (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					throw new Exception("Null profile");
				
				attributeMap = new InterchangeAttributeMap();
				attributeMap.externalRecord = externalRecord;
				
				String internalUnit = profile.getValueAsString(DataConfig.INTERNAL_UNIT_FIELD);
				String internalAtt = profile.getValueAsString(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD);
				Object internalValue = profile.getValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD);
				if (!(internalValue instanceof Serializable))
					throw new Exception("Internal value is not serializable");
				
				attributeMap.internalRecord = new InternalRecord(
						internalUnit, internalAtt, (Serializable) internalValue);
				
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			attributeMap = null;
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
		
		if (attributeMap == null || !attributeMap.isValid())
			return null;
		else
			return attributeMap;
	}

	
	@Override
	public boolean insertAttributeMap(InterchangeAttributeMap attributeMap) {
		if (!attributeMap.isValid())
			return false;
		
		Profile profile = new Profile(
				assoc.getAttributes(getConfig().getAttributeMapUnit()));
		
		profile.setValue(DataConfig.INTERNAL_UNIT_FIELD, attributeMap.internalRecord.unit);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, attributeMap.internalRecord.attribute);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, attributeMap.internalRecord.value);
		
		profile.setValue(DataConfig.EXTERNAL_UNIT_FIELD, attributeMap.externalRecord.unit);
		profile.setValue(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD, attributeMap.externalRecord.attribute);
		profile.setValue(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD, attributeMap.externalRecord.value);
		
		return assoc.insertProfile(getConfig().getAttributeMapUnit(), profile);
	}
	
	
	@Override
	public boolean updateAttributeMap(InterchangeAttributeMap attributeMap) {
		if (!attributeMap.isValid())
			return false;
		
		if (containsAttributeMap(attributeMap.internalRecord)) {
			Profile profile = new Profile(
					assoc.getAttributes(getConfig().getAttributeMapUnit()));
			
			profile.setValue(DataConfig.INTERNAL_UNIT_FIELD, attributeMap.internalRecord.unit);
			profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, attributeMap.internalRecord.attribute);
			profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, attributeMap.internalRecord.value);
			
			profile.setValue(DataConfig.EXTERNAL_UNIT_FIELD, attributeMap.externalRecord.unit);
			profile.setValue(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD, attributeMap.externalRecord.attribute);
			profile.setValue(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD, attributeMap.externalRecord.value);
			
			return assoc.updateProfile(getConfig().getAttributeMapUnit(), profile);
		}
		else
			return insertAttributeMap(attributeMap);
	}

	
	@Override
	public boolean deleteAttributeMap(InternalRecord internalRecord) {
		if (!internalRecord.isValid())
			return false;
		
		Profile profile = new Profile(
				assoc.getAttributes(getConfig().getAttributeMapUnit()));
		
		profile.setValue(DataConfig.INTERNAL_UNIT_FIELD, internalRecord.unit);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, internalRecord.attribute);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, internalRecord.value);

		return assoc.deleteProfile(getConfig().getAttributeMapUnit(), profile);
	}

	
	@Override
	public boolean deleteAttributeMap(String internalUnit) {
		if (internalUnit == null || internalUnit.isEmpty())
			return false;
		
		Profile profile = new Profile(
				assoc.getAttributes(getConfig().getAttributeMapUnit()));
		profile.setValue(DataConfig.INTERNAL_UNIT_FIELD, internalUnit);

		return assoc.deleteProfile(getConfig().getAttributeMapUnit(), profile);
	}

	
	@Override
	public Profile getAccount(String accName) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getAccountUnit()));
		profile.setValue(DataConfig.ACCOUNT_NAME_FIELD, accName);
		
		return assoc.getProfile(getConfig().getAccountUnit(), profile);
	}

	
	/**
	 * Validating account and password.
	 * @param account given account name.
	 * @param password given password.
	 * @return whether account and password are valid.
	 */
	private boolean validateAccount(String account, String password) {
		Profile profile = getAccount(account);
		if (profile == null) return false;
		
		String pwd = profile.getValueAsString(DataConfig.ACCOUNT_PASSWORD_FIELD);
		if (pwd == null) return false;
		
		String digest = Util.getCipher().md5Encrypt(password);
		if (!digest.equals(pwd)) return false;
		
		return true;
	}
	
	
	@Override
	public boolean validateAccount(String account, String password, int privileges) {
		if (!validateAccount(account, password)) return false;
		
		Profile profile = getAccount(account);
		int priv = profile.getValueAsInt(DataConfig.ACCOUNT_PRIVILEGES_FIELD);
		if ((priv & privileges) != privileges)
			return false;
		else
			return true;
	}

	
	@Override
	public int getPrivileges(String account, String password) {
		if (!validateAccount(account, password)) return 0;
		
		Profile profile = getAccount(account);
		return profile.getValueAsInt(DataConfig.ACCOUNT_PRIVILEGES_FIELD);
	}


	@Override
	public boolean insertAccount(Profile acc) {
		return assoc.insertProfile(getConfig().getAccountUnit(), acc);
	}
	
	
	@Override
	public boolean updateAccount(Profile acc) {
		if (containsAccount(acc.getValueAsString(DataConfig.ACCOUNT_NAME_FIELD)))
			return assoc.updateProfile(getConfig().getAccountUnit(), acc);
		else
			return insertAccount(acc);
	}

	
	@Override
	public boolean deleteAccount(String accName) {
		Profile profile = new Profile(assoc.getAttributes(getConfig().getAccountUnit()));
		profile.setValue(DataConfig.ACCOUNT_NAME_FIELD, accName);
		
		return assoc.deleteProfile(getConfig().getAccountUnit(), profile);
	}

	
	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		return assoc.getProfile(profileUnit, condition);
	}

	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit, Profile condition) {
		return assoc.getProfiles(profileUnit, condition);
	}


	@Override
	public Collection<Profile> getProfiles2(String profileUnit, Profile condition) {
		return assoc.getProfiles2(profileUnit, condition);
	}


	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		return assoc.getProfiles(selectSql, condition);
	}


	@Override
	public Collection<Profile> getProfiles2(ParamSql selectSql, Profile condition) {
		return assoc.getProfiles2(selectSql, condition);
	}


	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		return assoc.getProfileIds(profileUnit);
	}


	@Override
	public Collection<Integer> getProfileIds2(String profileUnit) {
		return assoc.getProfileIds2(profileUnit);
	}


	@Override
	public int getProfileMaxId(String profileUnit) {
		return assoc.getProfileMaxId(profileUnit);
	}

	
	@Override
	public AttributeList getProfileAttributes(String profileUnit) {
		return assoc.getAttributes(profileUnit);
	}

	
	@Override
	public AttributeList getProfileAttributes(ParamSql selectSql, Profile condition) {
		return assoc.getAttributes(selectSql, condition);
	}


	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		return assoc.insertProfile(profileUnit, profile);
	}

	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		return assoc.updateProfile(profileUnit, profile);
	}

	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		return assoc.deleteProfile(profileUnit, condition);
	}

	
	@Override
	public UnitList getUnitList() {
		return assoc.getUnitList();
	}

	
	@Override
	public boolean createUnit(String unitName, AttributeList attList) {
		return assoc.createUnit(unitName, attList);
	}

	
	@Override
	public boolean deleteUnitData(String unitName) {
		return assoc.deleteUnitData(unitName);
	}

	
	@Override
	public boolean dropUnit(String unitName) {
		return assoc.dropUnit(unitName);
	}

	
	@Override
	public boolean createSchema(AttributeList userAttList,
			AttributeList itemAttList) {
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
		result &= createUnit(config.getRatingUnit(), AttributeList.defaultRatingAttributeList());

		
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
		result &= createUnit(config.getContextUnit(), AttributeList.defaultContextAttributeList());
		
		
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
		accProfile.setValue(DataConfig.ACCOUNT_NAME_FIELD, DataConfig.ADMIN_ACCOUNT);
		accProfile.setValue(DataConfig.ACCOUNT_PASSWORD_FIELD, Util.getCipher().md5Encrypt(DataConfig.ADMIN_PASSWORD));
		accProfile.setValue(DataConfig.ACCOUNT_PRIVILEGES_FIELD, "255");
		result &= insertAccount(accProfile);
		
		return result;
	}

	
	@Override
	public boolean dropSchema() {
		boolean result = true;
		UnitList tblList = getUnitList();
		
		DataConfig config = getConfig();
		
		if (tblList.contains(config.getConfigUnit()))
			result &= dropUnit(config.getConfigUnit());
		
		if (tblList.contains(config.getRatingUnit()))
			result &= dropUnit(config.getRatingUnit());
		
		if (tblList.contains(config.getNominalUnit()))
			result &= dropUnit(config.getNominalUnit());

		if (tblList.contains(config.getUserUnit()))
			result &= dropUnit(config.getUserUnit());
			
		if (tblList.contains(config.getItemUnit()))
			result &= dropUnit(config.getItemUnit());
		
		if (tblList.contains(config.getAccountUnit()))
			result &= dropUnit(config.getAccountUnit());
		
		if (tblList.contains(config.getAttributeMapUnit()))
			result &= dropUnit(config.getAttributeMapUnit());

		if (tblList.contains(config.getContextUnit()))
			result &= dropUnit(config.getContextUnit());

		if (tblList.contains(config.getContextTemplateUnit())) {
			for (int i = 0; i < tblList.size(); i++) {
				Unit unit = tblList.get(i);
				String unitName = unit.getName();
				if (unitName.startsWith(config.getContextTemplateUnit()) && 
						unitName.endsWith(DataConfig.PROFILE_FIELD))
					result &= dropUnit(unitName);
					
			}
			result &= dropUnit(config.getContextTemplateUnit());
		}
		
		if (tblList.contains(config.getSampleUnit()))
			result &= dropUnit(config.getSampleUnit());

		
		return result;
	}

	
	@Override
	public boolean importData(Provider src, boolean create,
			ProgressListener registeredListener) {
		
		int progressTotal = 7;
		int progressStep = 0;
		
		boolean result = true;
		DataConfig thisConfig = getConfig();
		DataConfig srcConfig = src.getConfig();
		
		// 1. Create template schema
		if (create) {
			result &= createSchema(src.getUserAttributes(), src.getItemAttributes());
		}
		else {
			
//			// Importing context template schema
//			try {
//				this.ctsManager.importCTSchema(src.getCTSManager());
//			}
//			catch (Throwable e) {
//				LogUtil.trace(e);
//			}
			
			// Importing user and item attributes
			try {
				AttributeList userAttList = src.getUserAttributes();
				AttributeList itemAttList = src.getItemAttributes();
				
				if (userAttList.size() > 0) {
					
					if (thisConfig.getUserUnit() == null)
						thisConfig.setUserUnit(DataConfig.USER_UNIT);
					
					dropUnit(thisConfig.getUserUnit());
					createUnit(thisConfig.getUserUnit(), userAttList);
				}
				
				if (itemAttList.size() > 0)
				{
					if (thisConfig.getItemUnit() == null)
						thisConfig.setItemUnit(DataConfig.ITEM_UNIT);
	
					dropUnit(thisConfig.getItemUnit());
					createUnit(thisConfig.getItemUnit(), itemAttList);
				}
				
				result &= true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				result &= false;
			}
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Template schema created"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}
		
		
		// 2. Configuring
		Fetcher<Profile> configs = src.getProfiles(srcConfig.getConfigUnit(), null);
		try {
			deleteUnitData(thisConfig.getConfigUnit());
			while (configs.next()) {
				Profile config = configs.pick();
				insertConfigProfile(config);
			}
			thisConfig.setMetadata(srcConfig.getMetadata());
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}
		finally {
			try {
				configs.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Dataset configured"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}
		
		
		// 3. Inserting users
		Fetcher<Profile> users = src.getProfiles(srcConfig.getUserUnit(), null);
		try {
			deleteUnitData(thisConfig.getUserUnit());
			deleteAttributeMap(thisConfig.getUserUnit());
			
			while (users.next()) {
				Profile user = users.pick();
				if (user == null)
					continue;
				
				assoc.insertProfile(thisConfig.getUserUnit(), user);
				
				InterchangeAttributeMap attributeMap = src.getUserAttributeMap(user.getIdValueAsInt());
				if (attributeMap != null)
					insertAttributeMap(attributeMap);
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}
		finally {
			try {
				users.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		// Updating user nominal
		try {
			deleteNominal(thisConfig.getUserUnit());

			AttributeList userAttList = src.getUserAttributes();
			for (int i = 0; i < userAttList.size(); i++) {
				Attribute att = userAttList.get(i);
				
				if (att.getType() != Type.nominal)
					continue;
				
				NominalList nominals = att.getNominalList();
				for (int j = 0; j < nominals.size(); j++) {
					Nominal nominal = nominals.get(j);
					insertNominal(thisConfig.getUserUnit(), att.getName(), nominal);
					
					result &= true;
				}
				
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}

		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "User profiles inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 4. Inserting items
		Fetcher<Profile> items = src.getProfiles(srcConfig.getItemUnit(), null);
		try {
			deleteUnitData(thisConfig.getItemUnit());
			deleteAttributeMap(thisConfig.getItemUnit());

			while (items.next()) {
				Profile item = items.pick();
				if (item == null)
					continue;
				
				assoc.insertProfile(thisConfig.getItemUnit(), item);
				
				InterchangeAttributeMap attributeMap = src.getItemAttributeMap(item.getIdValueAsInt());
				if (attributeMap != null)
					insertAttributeMap(attributeMap);
				
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			result &= false;
		}
		finally {
			try {
				items.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		// Updating item nominal
		try {
			deleteNominal(thisConfig.getItemUnit());

			AttributeList itemAttList = src.getItemAttributes();
			for (int i = 0; i < itemAttList.size(); i++) {
				Attribute att = itemAttList.get(i);
				
				if (att.getType() != Type.nominal)
					continue;
				
				NominalList nominals = att.getNominalList();
				for (int j = 0; j < nominals.size(); j++) {
					
					Nominal nominal = nominals.get(j);
					insertNominal(thisConfig.getItemUnit(), att.getName(), nominal);
				}
				
			}
			
			result &= true;
		}
		catch(Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}
		
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Item profiles inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 5. Inserting ratings
		Fetcher<RatingTriple> triples = src.getRatings();
		try {
			deleteUnitData(thisConfig.getRatingUnit());
			deleteUnitData(thisConfig.getContextUnit());

			while (triples.next()) {
				RatingTriple triple = triples.pick();
				if (triple == null)
					continue;
				
				insertRating(triple.userId(), triple.itemId(), triple.getRating());
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			result &= false;
		}
		finally {
			try {
				triples.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Ratings inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 6. Importing context template schema
		try {
			this.ctsManager.importCTSchema(src.getCTSManager());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
						new ProgressEvent(this, progressTotal, ++progressStep, "Context template schema imported"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 7. Inserting samples
		Fetcher<Profile> sampleProfiles = src.getProfiles(srcConfig.getSampleUnit(), null);
		try {
			deleteUnitData(thisConfig.getSampleUnit());

			while (sampleProfiles.next()) {
				Profile sampleProfile = sampleProfiles.pick();
				if (sampleProfile == null)
					continue;
				
				assoc.insertProfile(thisConfig.getSampleUnit(), sampleProfile);
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			result &= false;
		}
		finally {
			try {
				sampleProfiles.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Sample inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}
		
		
		return result;
	}

	
	@Override
	public boolean importData(Dataset src, boolean create,
			ProgressListener registeredListener) {
		
		if (src instanceof Pointer)
			return importData(new ProviderImpl(src.getConfig()), create, registeredListener);
		
		int progressTotal = 7;
		int progressStep = 0;
		
		boolean result = true;
		DataConfig thisConfig = getConfig();
		DataConfig srcConfig = src.getConfig();
		
		// 1. Create template schema
		if (create) {
			result &= createSchema(src.getUserAttributes(), src.getItemAttributes());
		}
		else {
			
//			// Importing context template schema
//			try {
//				this.ctsManager.importCTSchema(src);
//			}
//			catch (Throwable e) {
//				LogUtil.trace(e);
//			}
			
			// Importing user and item attributes
			try {
				AttributeList userAttList = src.getUserAttributes();
				AttributeList itemAttList = src.getItemAttributes();
				
				if (userAttList.size() > 0) {
					
					if (thisConfig.getUserUnit() == null)
						thisConfig.setUserUnit(DataConfig.USER_UNIT);
					
					dropUnit(thisConfig.getUserUnit());
					createUnit(thisConfig.getUserUnit(), userAttList);
				}
				
				if (itemAttList.size() > 0)
				{
					if (thisConfig.getItemUnit() == null)
						thisConfig.setItemUnit(DataConfig.ITEM_UNIT);
	
					dropUnit(thisConfig.getItemUnit());
					createUnit(thisConfig.getItemUnit(), itemAttList);
				}
				
				result &= true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				result &= false;
			}
			
		} // end if
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Template schema created"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}
	
		
		// 2. Configuring
		try {
			deleteUnitData(thisConfig.getConfigUnit());
			DatasetMetadata meatadata = srcConfig.getMetadata();
			List<Profile> cfgProfileList = meatadata.toProfiles(
				getProfileAttributes(thisConfig.getConfigUnit()));
			
			for (Profile cfgProfile : cfgProfileList) {
				insertConfigProfile(cfgProfile);
			}
			thisConfig.setMetadata(meatadata);
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Dataset configured"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}
		
		
		// 3. Inserting users
		Fetcher<Profile> users = src.fetchUserProfiles();
		try {
			deleteUnitData(thisConfig.getUserUnit());
			deleteAttributeMap(thisConfig.getUserUnit());
			
			while (users.next()) {
				Profile user = users.pick();
				if (user == null)
					continue;
				
				assoc.insertProfile(thisConfig.getUserUnit(), user);
				
				int userId = user.getIdValueAsInt();
				ExternalRecord externalRecord = src.getUserExternalRecord(userId);
				if (externalRecord == null)
					continue;
				
				InternalRecord internalRecord = new InternalRecord(
						getConfig().getUserUnit(), DataConfig.USERID_FIELD, userId);
				InterchangeAttributeMap attributeMap = new InterchangeAttributeMap(internalRecord, externalRecord);
				if (attributeMap != null)
					insertAttributeMap(attributeMap);
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}
		finally {
			try {
				users.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		// Updating user nominal
		try {
			deleteNominal(thisConfig.getUserUnit());

			AttributeList userAttList = src.getUserAttributes();
			for (int i = 0; i < userAttList.size(); i++) {
				Attribute att = userAttList.get(i);
				
				if (att.getType() != Type.nominal)
					continue;
				
				NominalList nominals = att.getNominalList();
				for (int j = 0; j < nominals.size(); j++) {
					Nominal nominal = nominals.get(j);
					insertNominal(thisConfig.getUserUnit(), att.getName(), nominal);
					
					result &= true;
				}
				
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}

		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "User profiles inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 4. Inserting items
		Fetcher<Profile> items = src.fetchItemProfiles();
		try {
			deleteUnitData(thisConfig.getItemUnit());
			deleteAttributeMap(thisConfig.getItemUnit());

			while (items.next()) {
				Profile item = items.pick();
				if (item == null)
					continue;
				
				assoc.insertProfile(thisConfig.getItemUnit(), item);
				
				int itemId = item.getIdValueAsInt();
				ExternalRecord externalRecord = src.getItemExternalRecord(itemId);
				if (externalRecord == null)
					continue;
				
				InternalRecord internalRecord = new InternalRecord(
						getConfig().getItemUnit(), DataConfig.ITEMID_FIELD, itemId);
				InterchangeAttributeMap attributeMap = new InterchangeAttributeMap(internalRecord, externalRecord);
				if (attributeMap != null)
					insertAttributeMap(attributeMap);
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			result &= false;
		}
		finally {
			try {
				items.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		// Updating item nominal
		try {
			deleteNominal(thisConfig.getItemUnit());

			AttributeList itemAttList = src.getItemAttributes();
			for (int i = 0; i < itemAttList.size(); i++) {
				Attribute att = itemAttList.get(i);
				
				if (att.getType() != Type.nominal)
					continue;
				
				NominalList nominals = att.getNominalList();
				for (int j = 0; j < nominals.size(); j++) {
					
					Nominal nominal = nominals.get(j);
					insertNominal(thisConfig.getItemUnit(), att.getName(), nominal);
				}
				
			}
			
			result &= true;
		}
		catch(Throwable e) {
			LogUtil.trace(e);
			result &= false;
		}
		
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Item profiles inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 5. Inserting ratings
		Fetcher<RatingVector> ratings = src.fetchUserRatings();
		try {
			deleteUnitData(thisConfig.getRatingUnit());
			deleteUnitData(thisConfig.getContextUnit());
			
			while (ratings.next()) {
				RatingVector uRating = ratings.pick();
				if (uRating == null)
					continue;
				
				int userId = uRating.id();
				Set<Integer> itemIds = uRating.fieldIds(true);
				for (int itemId : itemIds) {
					insertRating(userId, itemId, uRating.get(itemId));
				}
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			result &= false;
		}
		finally {
			try {
				ratings.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Ratings inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 6. Importing context template schema
		try {
			this.ctsManager.importCTSchema(src);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Context template schema imported"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		// 7. Inserting samples
		Fetcher<Profile> sampleProfiles = src.fetchSample();
		try {
			deleteUnitData(thisConfig.getSampleUnit());

			while (sampleProfiles.next()) {
				Profile sampleProfile = sampleProfiles.pick();
				if (sampleProfile == null)
					continue;
				
				assoc.insertProfile(thisConfig.getSampleUnit(), sampleProfile);
			}
			
			result &= true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			result &= false;
		}
		finally {
			try {
				sampleProfiles.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null) {
			try {
				registeredListener.receiveProgress(
					new ProgressEvent(this, progressTotal, ++progressStep, "Sample inserted"));
			} catch (Throwable ex) {ex.printStackTrace();}
		}

		
		return result;
	}

	
	@Override
	public boolean importData(DataConfig src, boolean create,
			ProgressListener registeredListener) {
		
		DatasetParser parser = src.getParser();
		if ( (parser == null) || 
				(parser instanceof Indicator) || 
				(parser instanceof ScannerParser)) {
			
			Provider provider = new ProviderImpl(src);
			return importData(provider, create, registeredListener);
		}
		else {
			try {
				Dataset dataset = parser.parse(src);
				return importData(dataset, create, registeredListener);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				return false;
			}
		}
	}


	@Override
	public void close() {
		
		try {
			if (ctsManager != null)
				ctsManager.close();
			
			ctsManager = null;
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		try {
			if (assoc != null)
				assoc.close();
			
			assoc = null;
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}

	}


	@Override
	public Object clone() {
		DataConfig config = (DataConfig)getConfig().clone();
		
		return new ProviderImpl(config);
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}

	
}
