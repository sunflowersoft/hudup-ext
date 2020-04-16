/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.InterchangeAttributeMap;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.MemProfiles;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.data.ProviderAssoc.CsvReader;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMulti;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.SnapshotImpl;
import net.hudup.core.data.UserRating;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.ContextValue;
import net.hudup.core.data.ctx.ContextValueImpl;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents an extension of snapshot which supports dyadic data.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class SnapshotImplExt extends SnapshotImpl {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SnapshotImplExt() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	/**
	 * Create snapshot from resource specified by data configuration.
	 * @param config data configuration referring to specified resource (database, file directory, etc.).
	 * @return dyadic snapshot created from resource specified by data configuration.
	 */
	public static SnapshotImplExt create(DataConfig config) {
		
		DataDriver ddriver = DataDriver.create(config.getStoreUri());
		if (ddriver != null && ddriver.getType() == DataType.file) {
			SnapshotImplExt snapshot = fileCreate(config);
			if (snapshot != null)
				return snapshot;
		}
		
		ProviderImplExt provider = new ProviderImplExt(config);
		
		try {
			
			// Getting user id
			List<Integer> userIds = Util.newList();
			FetcherUtil.fillCollection(
					userIds, 
					provider.getProfileIds(config.getUserUnit()), 
					true);

			// Getting user profiles
			AttributeList userAttributes = provider.getUserAttributes();
			Map<Integer, Profile> userProfileMap = Util.newMap();
			for (int userId : userIds) {
				Profile userProfile = provider.getUserProfile(userId);
				
				userProfileMap.put(userId, userProfile);
			}
			MemProfiles userProfiles = MemProfiles.assign(userAttributes, userProfileMap);

			
			// Getting item id (s)
			List<Integer> itemIds = Util.newList();
			FetcherUtil.fillCollection(
					itemIds, 
					provider.getProfileIds(config.getItemUnit()), 
					true);

			// Getting item profiles
			AttributeList itemAttributes = provider.getItemAttributes();
			Map<Integer, Profile> itemProfileMap = Util.newMap();
			for (int itemId : itemIds) {
				Profile itemProfile = provider.getItemProfile(itemId);
				
				itemProfileMap.put(itemId, itemProfile);
			}
			MemProfiles itemProfiles = MemProfiles.assign(itemAttributes, itemProfileMap);

			
			// Getting external user record map
			Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
			for (int userId : userIds) {
				InternalRecord internalRecord = new InternalRecord(
						config.getUserUnit(), DataConfig.USERID_FIELD, userId);
				InterchangeAttributeMap attributeMap = provider.getAttributeMap(internalRecord);
				if (attributeMap != null)
					externalUserRecordMap.put(userId, attributeMap.externalRecord);
			}
			
			// Getting external item record map
			Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();
			for (int itemId : itemIds) {
				InternalRecord internalRecord = new InternalRecord(
						config.getItemUnit(), DataConfig.ITEMID_FIELD, itemId);
				InterchangeAttributeMap attributeMap = provider.getAttributeMap(internalRecord);
				if (attributeMap != null)
					externalItemRecordMap.put(itemId, attributeMap.externalRecord);
			}

			
			// Getting rating vectors
			Map<Integer, RatingVector> userRatingMap = Util.newMap();
			Map<Integer, RatingVector> itemRatingMap = Util.newMap();
			for (int userId : userIds) {
				RatingVector user = provider.getUserRatingVector(userId);
				if (user == null || user.size() == 0)
					continue;
				
				userRatingMap.put(userId, user);
				Set<Integer> itemIdSet = user.fieldIds();
				for (int itemId : itemIdSet) {
					if (!user.isRated(itemId))
						continue;
					
					Rating rating = user.get(itemId);
					RatingVector item = itemRatingMap.get(itemId);
					if (item == null) {
						item = new ItemRating(itemId);
						itemRatingMap.put(itemId, item);
					}
					item.put(userId, rating);
				}
			}
			//Filling additional user profiles and item profiles which can be lacked.
			//Fixing bug date: 2019.07.09
			userProfiles.fillUnion(userRatingMap.keySet(), DataConfig.USERID_FIELD);
			itemProfiles.fillUnion(itemRatingMap.keySet(), DataConfig.ITEMID_FIELD);
			
			
			// Getting sample profiles
			List<Profile> sampleProfiles = Util.newList();
			Fetcher<Profile> sampleFetcher = provider.getProfiles(config.getSampleUnit(), null);
			while(sampleFetcher.next()) {
				Profile profile = sampleFetcher.pick();
				if (profile != null)
					sampleProfiles.add(profile);
			}
			sampleFetcher.close();
			
			
			SnapshotImplExt dataset = new SnapshotImplExt();
			dataset.config = config;
			dataset.externalUserRecordMap = externalUserRecordMap;
			dataset.userRatingMap = userRatingMap;
			dataset.userProfiles = userProfiles;
			
			dataset.itemRatingMap = itemRatingMap;
			dataset.itemProfiles = itemProfiles;
			dataset.externalItemRecordMap = externalItemRecordMap;
			
			dataset.ctSchema = (ContextTemplateSchema) provider.getCTSManager().getCTSchema().transfer();
			dataset.ctsProfiles = provider.getCTSManager().createCTSProfiles();
			dataset.sampleProfiles = sampleProfiles;
			
			dataset.enhance();
			return dataset;
			
		}
		
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			provider.close();
		}
		
		return null;
	}

	
	/**
	 * Creating snapshot from file.
	 * @param config configuration of specified file.
	 * @return dyadic snapshot from file.
	 */
	private static SnapshotImplExt fileCreate(DataConfig config) {
		// TODO Auto-generated method stub
		
		ProviderImpl provider = new ProviderImpl(config);
		ProviderAssoc assoc = Util.getFactory().createProviderAssoc(config);
		
		// Reading user profiles
		MemProfiles userProfiles = MemProfiles.createEmpty();
		CsvReader userReader = null;
		try {
			userReader = assoc.getReader(config.getUserUnit());
			if(userReader != null) {
				Map<Integer, Profile> userProfileMap = Util.newMap();
				userReader.readHeader();
				String[] header = userReader.getHeader();
				AttributeList userAtts = new AttributeList();
				userAtts.parseText(Arrays.asList(header));
				while (userReader.readRecord()) {
					String[] record = userReader.getRecord();
					Profile profile = Profile.create(record, userAtts);
					if (profile != null)
						userProfileMap.put(profile.getIdValueAsInt(), profile);
				}
				
				userProfiles = MemProfiles.assign(userAtts, userProfileMap);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (userReader != null)
					userReader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		// Reading item profiles
		MemProfiles itemProfiles = MemProfiles.createEmpty();
		CsvReader itemReader = null;
		try {
			itemReader = assoc.getReader(config.getItemUnit());
			if (itemReader != null) {
				Map<Integer, Profile> itemProfileMap = Util.newMap();
				itemReader.readHeader();
				String[] header = itemReader.getHeader();
				AttributeList itemAtts = new AttributeList();
				itemAtts.parseText(Arrays.asList(header));
				while (itemReader.readRecord()) {
					String[] record = itemReader.getRecord();
					Profile profile = Profile.create(record, itemAtts);
					if (profile != null)
						itemProfileMap.put(profile.getIdValueAsInt(), profile);
				}
				itemProfiles = MemProfiles.assign(itemAtts, itemProfileMap);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (itemReader != null)
					itemReader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		// Reading attribute map
		Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
		Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();
		CsvReader attMapReader = null;
		try {
			attMapReader = assoc.getReader(config.getAttributeMapUnit());
			if (attMapReader != null) {
				attMapReader.readHeader();
				String[] header = attMapReader.getHeader();
				AttributeList attMapAtts = new AttributeList();
				attMapAtts.parseText(Arrays.asList(header));
				while (attMapReader.readRecord()) {
					String[] record = attMapReader.getRecord();
					Profile profile = Profile.create(record, attMapAtts);
					if (profile == null)
						continue;
					
					String internalUnit = profile.getValueAsString(DataConfig.INTERNAL_UNIT_FIELD);
					if ( (!internalUnit.equals(config.getUserUnit())) && (!internalUnit.equals(config.getItemUnit())) )
						continue;
					
					String internalAtt = profile.getValueAsString(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD);
					Object internalValue = profile.getValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD);
					if (!(internalValue instanceof Serializable))
						continue;
					InternalRecord internalRecord = new InternalRecord(internalUnit, internalAtt, (Serializable) internalValue);
					
					String externalUnit = profile.getValueAsString(DataConfig.EXTERNAL_UNIT_FIELD);
					String externalAtt = profile.getValueAsString(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD);
					Object externalValue = profile.getValue(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD);
					if (!(externalValue instanceof Serializable))
						continue;
					ExternalRecord externalRecord = new ExternalRecord(externalUnit, externalAtt, (Serializable) externalValue);
					
					InterchangeAttributeMap attmap = new InterchangeAttributeMap(internalRecord, externalRecord);
					if (!attmap.isValid())
						continue;
					
					if (internalUnit.equals(config.getUserUnit()) && internalAtt.equals(DataConfig.USERID_FIELD))
						externalUserRecordMap.put(Integer.parseInt(internalValue.toString()), externalRecord);
					
					if (internalUnit.equals(config.getItemUnit()) && internalAtt.equals(DataConfig.ITEMID_FIELD))
						externalItemRecordMap.put(Integer.parseInt(internalValue.toString()), externalRecord);
				}//End while
				
			}//End if
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (attMapReader != null)
					attMapReader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		// Getting rating vectors
		Map<Integer, RatingVector> userRatingMap = Util.newMap();
		Map<Integer, RatingVector> itemRatingMap = Util.newMap();
		CsvReader ratingReader = null;
		try {	
			ratingReader = assoc.getReader(config.getRatingUnit());
			if (ratingReader != null) {
				ratingReader.readHeader();
				String[] header = ratingReader.getHeader();
				AttributeList ratingAtts = new AttributeList();
				ratingAtts.parseText(Arrays.asList(header));
				while (ratingReader.readRecord()) {
					String[] record  = ratingReader.getRecord(); 
					Profile profile = Profile.create(record, ratingAtts);
					if (profile == null)
						continue;
					
					int userId = profile.getValueAsInt(DataConfig.USERID_FIELD);
					int itemId = profile.getValueAsInt(DataConfig.ITEMID_FIELD);
					double ratingValue = profile.getValueAsReal(DataConfig.RATING_FIELD);
					if (userId < 0 || itemId < 0 || !Util.isUsed(ratingValue))
						continue;
					
					Rating monoRating = new Rating(ratingValue);
					monoRating.ratedDate = profile.getValueAsTime(DataConfig.RATING_DATE_FIELD);
					
					RatingVector user = null;
					if (userRatingMap.containsKey(userId))
						user = userRatingMap.get(userId);
					else {
						user = new UserRating(userId);
						userRatingMap.put(userId, user);
					}
					
					RatingVector item = null;
					if (itemRatingMap.containsKey(itemId))
						item = itemRatingMap.get(itemId);
					else {
						item = new ItemRating(itemId);
						itemRatingMap.put(itemId, item);
					}
					
					RatingMulti rating = null;
					if (user.contains(itemId)) {
						rating = (RatingMulti)user.get(itemId);
						rating.addRating(monoRating);
					}
					else {
						rating = new RatingMulti(monoRating);
						user.put(itemId, rating);
						item.put(userId, rating);
					}
				}
				//Filling additional user profiles and item profiles which can be lacked.
				userProfiles.fillUnion(userRatingMap.keySet(), DataConfig.USERID_FIELD);
				itemProfiles.fillUnion(itemRatingMap.keySet(), DataConfig.ITEMID_FIELD);
			}//End if
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (ratingReader != null)
					ratingReader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		// Getting context
		ContextTemplateSchema ctSchema = (ContextTemplateSchema) provider.getCTSManager().getCTSchema().transfer();
		CsvReader ctxReader = null;
		try {
			ctxReader = assoc.getReader(config.getContextUnit());
			if (ctxReader != null) {
				ctxReader.readHeader();
				String[] header = ctxReader.getHeader();
				AttributeList ctxAtts = new AttributeList();
				ctxAtts.parseText(Arrays.asList(header));
				while (ctxReader.readRecord()) {
					String[] record = ctxReader.getRecord(); 
					Profile profile = Profile.create(record, ctxAtts);
					if (profile == null)
						continue;
					
					int userId = profile.getValueAsInt(DataConfig.USERID_FIELD);
					int itemId = profile.getValueAsInt(DataConfig.ITEMID_FIELD);
					int ctxTemplateId = profile.getValueAsInt(DataConfig.CTX_TEMPLATEID_FIELD);
					if (userId < 0 || itemId < 0 || ctxTemplateId < 0)
						continue;
					
					RatingVector vRating = userRatingMap.get(userId);
					if (vRating == null)
						continue;
					Rating rating = vRating.get(itemId);
					if (rating == null)
						continue;
					
					ContextTemplate template = ctSchema.getRootById(ctxTemplateId);
					if (template == null)
						continue;
					
					Object value = profile.getValue(DataConfig.CTX_VALUE_FIELD);
					if (value == null || !(value instanceof Serializable))
						value = null;
					
					Attribute attribute = template.getAttribute();
					ContextValue ctxValue = ContextValueImpl.create(attribute, (Serializable) value);
					Context context = Context.create(template, ctxValue);
					if (context != null)
						rating.contexts.add(context);
				}
			}//End if
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (ctxReader != null)
					ctxReader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		// Reading sample profiles
		List<Profile> sampleProfiles = Util.newList();
		CsvReader sampleReader = null;
		try {
			sampleReader = assoc.getReader(config.getSampleUnit());
			if (sampleReader != null) {
				sampleReader.readHeader();
				String[] header = sampleReader.getHeader();
				AttributeList sampleAtts = new AttributeList();
				sampleAtts.parseText(Arrays.asList(header));
				while (sampleReader.readRecord()) {
					String[] record = sampleReader.getRecord(); 
					Profile profile = Profile.create(record, sampleAtts);
					if (profile != null)
						sampleProfiles.add(profile);
				}
			}//End if
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (sampleReader != null)
					sampleReader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		
		SnapshotImplExt snapshot = null;
		try {
			snapshot = new SnapshotImplExt();
			snapshot.assign(
					config, 
					externalUserRecordMap, 
					userRatingMap, 
					userProfiles, 
					externalItemRecordMap, 
					itemRatingMap, 
					itemProfiles, 
					ctSchema, 
					provider.getCTSManager().createCTSProfiles(),
					sampleProfiles);
			
			snapshot.enhance();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			if (snapshot != null)
				snapshot.clear();
			
			snapshot = null;
		}
		finally {
			try {
				provider.close();
				assoc.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		return snapshot;
	}


}
