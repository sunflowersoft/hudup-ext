/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.ProviderAssoc.CsvReader;
import net.hudup.core.data.ctx.CTSMemMultiProfiles;
import net.hudup.core.data.ctx.CTSMultiProfiles;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.ContextTemplateSchemaImpl;
import net.hudup.core.data.ctx.ContextValue;
import net.hudup.core.data.ctx.ContextValueImpl;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is the default implementation of {@link Snapshot}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SnapshotImpl extends Snapshot {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This variable store user external records. It must be serializable in remote call.
	 */
	protected Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
	
	
	/**
	 * User rating matrix. It must be serializable in remote call.
	 */
	protected Map<Integer, RatingVector> userRatingMap = Util.newMap();
	
	
	/**
	 * This variable stores all user profiles. It is serializable here.
	 */
	protected MemProfiles userProfiles = MemProfiles.createEmpty();
	
	
	/**
	 * This variable store item external records. It must be serializable in remote call.
	 */
	protected Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();

	
	/**
	 * Item rating matrix. It must be serializable in remote call.
	 */
	protected Map<Integer, RatingVector> itemRatingMap = Util.newMap();
	
	
	/**
	 * Item profiles. It is serializable here.
	 */
	protected MemProfiles itemProfiles = MemProfiles.createEmpty();
	
	
	/**
	 * Context template schema. It is serializable here.
	 */
	protected ContextTemplateSchema ctSchema = ContextTemplateSchemaImpl.create();

	
	/**
	 * Profiles of context template schema. It is serializable here.
	 */
	protected CTSMultiProfiles ctsProfiles = CTSMemMultiProfiles.create();
	
	
	/**
	 * Other sample. It must be serializable in remote call.
	 */
	protected List<Profile> sampleProfiles = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public SnapshotImpl() {
//		if ( !(userRatingMap instanceof Serializable) || 
//				!(userProfiles instanceof Serializable) ||
//				!(externalUserRecordMap instanceof Serializable) ||
//			 	!(itemRatingMap instanceof Serializable) ||
//				!(externalItemRecordMap instanceof Serializable) ||
//			 	!(itemProfiles instanceof Serializable) ||
//			 	!(ctSchema instanceof Serializable) ||
//			 	!(ctsProfiles instanceof Serializable) ||
//		 		!(sampleProfiles instanceof Serializable) )
//			
//			 throw new RuntimeException("Not serializable class");
	}
	

	@Override
	public Fetcher<Integer> fetchUserIds() {
		return userProfiles.fetchIds();
	}

	
	@Override
	public Collection<Integer> fetchUserIds2() {
		return userProfiles.fetchIds2();
	}

	
	@Override
	public int getUserId(Serializable externalUserId) {
		Set<Integer> userIds = externalUserRecordMap.keySet();
		for (int userId : userIds) {
			ExternalRecord record = getUserExternalRecord(userId);
			if (record.value.equals(externalUserId))
				return userId;
		}
		
		return -1;
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		return externalUserRecordMap.get(userId);
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		return itemProfiles.fetchIds();
	}
	
	
	@Override
	public Collection<Integer> fetchItemIds2() {
		return itemProfiles.fetchIds2();
	}
	
	
	@Override
	public int getItemId(Serializable externalItemId) {
		Set<Integer> itemIds = externalItemRecordMap.keySet();
		for (int itemId : itemIds) {
			ExternalRecord record = getItemExternalRecord(itemId);
			if (record.value.equals(externalItemId))
				return itemId;
		}
		
		return -1;
	}
	
	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		return externalItemRecordMap.get(itemId);
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		if (!userRatingMap.containsKey(userId))
			return null;
		RatingVector user = userRatingMap.get(userId);
		return user.get(itemId);
	}
	
	
	@Override
	public void putRating(int userId, int itemId, Rating rating) {
		RatingVector user = null;
		if (userRatingMap.containsKey(userId))
			user = userRatingMap.get(userId); 
		else {
			user = new UserRating(userId);
			userRatingMap.put(userId, user);
		}
		user.put(itemId, rating);
		
		RatingVector item = null;
		if (itemRatingMap.containsKey(itemId))
			item = itemRatingMap.get(itemId); 
		else {
			item = new ItemRating(itemId);
			itemRatingMap.put(itemId, item);
		}
		item.put(userId, rating);
		
		userProfiles.fillUnion(Arrays.asList(userId), DataConfig.USERID_FIELD);
		itemProfiles.fillUnion(Arrays.asList(itemId), DataConfig.ITEMID_FIELD);
	}
	
	
	@Override
	public RatingVector getUserRating(int userId) {
		if (!userRatingMap.containsKey(userId))
			return null;
		
		return userRatingMap.get(userId);
	}
	

	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		return new MemFetcher<RatingVector>(userRatingMap.values());
	}
	

	@Override
	public Collection<RatingVector> fetchUserRatings2() {
		return userRatingMap.values();
	}
	

	@Override
	public RatingVector getItemRating(int itemId) {
		if (!itemRatingMap.containsKey(itemId))
			return null;
		
		return itemRatingMap.get(itemId);
	}

	
	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		return new MemFetcher<RatingVector>(itemRatingMap.values());
	}


	@Override
	public Collection<RatingVector> fetchItemRatings2() {
		return itemRatingMap.values();
	}


	@Override
	public RatingMatrix createUserMatrix() {
		return new DatasetAssoc(this).createMatrix(true);
	}


	@Override
	public RatingMatrix createItemMatrix() {
		return new DatasetAssoc(this).createMatrix(false);
	}


	@Override
	public Profile getUserProfile (int userId) {
		return userProfiles.get(userId);
	}
	
	
	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		return userProfiles.fetch();
	}
	
	
	@Override
	public Collection<Profile> fetchUserProfiles2() {
		return userProfiles.fetch2();
	}
	
	
	@Override
	public AttributeList getUserAttributes() {
		return userProfiles.getAttributes();
	}
	
	
	@Override
	public Profile getItemProfile(int itemId) {
		return itemProfiles.get(itemId);
	}

	
	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		return itemProfiles.fetch();
	}

	
	@Override
	public Collection<Profile> fetchItemProfiles2() {
		return itemProfiles.fetch2();
	}

	
	@Override
	public AttributeList getItemAttributes() {
		return itemProfiles.getAttributes();
	}

	
	@Override
	public Profile profileOf(Context context) {
		return ctsProfiles.profileOf(context);
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		return ctsProfiles.get(ctxTemplateId);
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		return ctSchema;
	}


	@Override
	public Fetcher<Profile> fetchSample() {
		return new MemFetcher<Profile>(sampleProfiles);
	}


	@Override
	public Collection<Profile> fetchSample2() {
		return sampleProfiles;
	}


	@Override
	public Object clone() {
		
		SnapshotImpl snapshot = new SnapshotImpl();
		
		snapshot.assign(
				(DataConfig)config.clone(),
				ExternalRecord.clone(externalUserRecordMap), 
				RatingVector.clone2(userRatingMap), 
				(MemProfiles)userProfiles.clone(),
				ExternalRecord.clone(externalItemRecordMap), 
				RatingVector.clone2(itemRatingMap),
				(MemProfiles)itemProfiles.clone(),
				(ContextTemplateSchema) ctSchema.transfer(),
				(CTSMultiProfiles) ctsProfiles.clone(),
				DSUtil.clone(sampleProfiles));
		
		return snapshot;
	}

	
	/**
	 * Assigning all internal variables of this snapshot by respective specified parameters.
	 * @param config specified configuration.
	 * @param externalUserRecordMap specified external user records.
	 * @param userRatingMap specified user rating matrix.
	 * @param userProfiles specified user profiles
	 * @param externalItemRecordMap specified external item records.
	 * @param itemRatingMap specified item rating matrix.
	 * @param itemProfiles specified item profiles.
	 * @param ctxTemplateSchema specified context template schema.
	 * @param ctsProfiles specified profiles of context template schema.
	 * @param sampleProfiles specified other sample.
	 */
	public void assign(
			DataConfig config,
			Map<Integer, ExternalRecord> externalUserRecordMap, 
			Map<Integer, RatingVector> userRatingMap, 
			MemProfiles userProfiles,
			Map<Integer, ExternalRecord> externalItemRecordMap, 
			Map<Integer, RatingVector> itemRatingMap,
			MemProfiles itemProfiles,
			ContextTemplateSchema ctxTemplateSchema,
			CTSMultiProfiles ctsProfiles,
			List<Profile> sampleProfiles) {
		
		this.config = config;

		this.externalUserRecordMap = externalUserRecordMap;
		this.userRatingMap = userRatingMap;
		this.userProfiles = userProfiles;
		
		this.externalItemRecordMap = externalItemRecordMap;
		this.itemRatingMap = itemRatingMap;
		this.itemProfiles = itemProfiles;
		
		this.ctSchema = ctxTemplateSchema;
		this.ctsProfiles = ctsProfiles;
		this.sampleProfiles = sampleProfiles;
		
		this.enhance();
	}

	
	@Override
	public void assign(Snapshot snapshot) {
		SnapshotImpl mem = (SnapshotImpl)snapshot;
		
		assign(
				mem.config,
				mem.externalUserRecordMap, 
				mem.userRatingMap, 
				mem.userProfiles, 
				mem.externalItemRecordMap, 
				mem.itemRatingMap, 
				mem.itemProfiles,
				mem.ctSchema,
				mem.ctsProfiles,
				mem.sampleProfiles);
	}
	
	
	@Override
	public void assignNull() {
		this.config = null;
		this.externalUserRecordMap = null; 
		this.userRatingMap = null; 
		this.userProfiles = null; 
		this.externalItemRecordMap = null; 
		this.itemRatingMap = null; 
		this.itemProfiles = null;
		this.ctSchema = null;
		this.ctsProfiles = null;
		this.sampleProfiles = null;
	}
	
	
	/**
	 * Enhancing internal structures.
	 */
	public void enhance() {
		userRatingMap.keySet();
		userRatingMap.values();
		userRatingMap.entrySet();
		
		externalUserRecordMap.keySet();
		externalUserRecordMap.values();
		externalUserRecordMap.entrySet();
		
		itemRatingMap.keySet();
		itemRatingMap.values();
		itemRatingMap.entrySet();
		
		externalItemRecordMap.keySet();
		externalItemRecordMap.values();
		externalItemRecordMap.entrySet();

		((MemProfiles)userProfiles).enhance();
		((MemProfiles)itemProfiles).enhance();
	}

	
	@Override
	public Dataset selectByContexts(ContextList contexts) {
		
		Map<Integer, RatingVector> newUserRatingMap = RatingVector.select(userRatingMap, contexts);
		Set<Integer> newUserIds = newUserRatingMap.keySet();
		MemProfiles newUserProfiles = (MemProfiles) userProfiles.transfer();
		newUserProfiles.fillAs(newUserIds);

		Map<Integer, RatingVector> newItemRatingMap = RatingVector.select(itemRatingMap, contexts);
		Set<Integer> newItemIds = newItemRatingMap.keySet();
		MemProfiles newItemProfiles = (MemProfiles) itemProfiles.transfer();
		newItemProfiles.fillAs(newItemIds);
		
		ContextTemplateSchema newCtxTemplateSchema = (ContextTemplateSchema)ctSchema.transfer();
		CTSMemMultiProfiles newCtsProfiles = (CTSMemMultiProfiles) ctsProfiles.transfer();
		List<Profile> newSampleProfiles = DSUtil.transfer(sampleProfiles);

		Map<Integer, ExternalRecord> newExternalUserRecordMap = Util.newMap();
		for (int userId : newUserIds) {
			newExternalUserRecordMap.put(userId, externalUserRecordMap.get(userId));
		}
		
		Map<Integer, ExternalRecord> newExternalItemRecordMap = Util.newMap();
		for (int itemId : newItemIds) {
			newExternalItemRecordMap.put(itemId, externalItemRecordMap.get(itemId));
		}
		
		SnapshotImpl snapshot = new SnapshotImpl();
		
		snapshot.assign(
				(DataConfig) config.clone(),
				newExternalUserRecordMap, 
				newUserRatingMap, 
				newUserProfiles, 
				newExternalItemRecordMap, 
				newItemRatingMap, 
				newItemProfiles,
				newCtxTemplateSchema,
				newCtsProfiles,
				newSampleProfiles);
		
		return snapshot;
	}


	@Override
	public CTSMultiProfiles getCTSMultiProfiles() {
		return ctsProfiles;
	}

	
	@Override
	public void clear() {
		super.clear();
		
		if (itemRatingMap != null) itemRatingMap.clear();
		if (itemProfiles != null) itemProfiles.clear();
		if (externalItemRecordMap != null) externalItemRecordMap.clear();
		
		if (userRatingMap != null) userRatingMap.clear();
		if (userProfiles != null) userProfiles.clear();
		if (externalUserRecordMap != null) externalUserRecordMap.clear();
		
		if (ctSchema != null) ctSchema.clear();
		if (ctsProfiles != null) ctsProfiles.clear();
		if (sampleProfiles != null) sampleProfiles.clear();
	}


	/**
	 * Creating snapshot by triples of ratings.
	 * @param tripleList specified triples of ratings.
	 * @param datasetMetadata dataset meta-data.
	 * @return snapshot created from triples of ratings.
	 */
	public static SnapshotImpl create(Collection<RatingTriple> tripleList, DatasetMetadata datasetMetadata) {
		
		SnapshotImpl snapshot = new SnapshotImpl();
		DataConfig config = new DataConfig();
		config.setMetadata(datasetMetadata);
		snapshot.setConfig(config);
		
		for (RatingTriple triple : tripleList) {
			int userId = triple.userId();
			int itemId = triple.itemId();

			RatingVector userRating = null;
			if (snapshot.userRatingMap.containsKey(userId))
				userRating = snapshot.userRatingMap.get(userId);
			else {
				userRating = new UserRating(userId);
				snapshot.userRatingMap.put(userId, userRating);
			}
			userRating.put(itemId, triple.getRating());

			RatingVector itemRating = null;
			if (snapshot.itemRatingMap.containsKey(itemId))
				itemRating = snapshot.itemRatingMap.get(itemId);
			else {
				itemRating = new ItemRating(itemId);
				snapshot.itemRatingMap.put(itemId, itemRating);
			}
			itemRating.put(userId, triple.getRating());
		}
		
		snapshot.userProfiles = MemProfiles.createEmpty(DataConfig.USERID_FIELD, Type.integer);
		((MemProfiles)snapshot.userProfiles).fillUnion(snapshot.userRatingMap.keySet(), DataConfig.USERID_FIELD);
		
		snapshot.itemProfiles = MemProfiles.createEmpty(DataConfig.ITEMID_FIELD, Type.integer);
		((MemProfiles)snapshot.itemProfiles).fillUnion(snapshot.itemRatingMap.keySet(), DataConfig.ITEMID_FIELD);

		snapshot.enhance();
		return snapshot;
	}
	
	
	/**
	 * Creating snapshot by specified rating matrix.
	 * @param matrix specified rating matrix.
	 * @param userMatrix if true the, the specified rating matrix is user rating matrix.
	 * @return snapshot created from rating matrix.
	 */
	public static SnapshotImpl create(RatingMatrix matrix, boolean userMatrix) {
		SnapshotImpl snapshot = new SnapshotImpl();
		DataConfig config = new DataConfig();
		config.setMetadata(matrix.metadata.to());
		snapshot.setConfig(config);

		List<Integer> userIdList = userMatrix ?  matrix.rowIdList : matrix.columnIdList;
		List<Integer> itemIdList = userMatrix ?  matrix.columnIdList : matrix.rowIdList;
		
		int m = userIdList.size();
		int n = itemIdList.size();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				double ratingValue = userMatrix ? matrix.matrix[i][j] : matrix.matrix[j][i];
				if (!Util.isUsed(ratingValue))
					continue;
				
				int userId = userIdList.get(i);
				int itemId = itemIdList.get(j);
				Rating rating = new Rating(ratingValue);
				
				RatingVector userRating = null;
				if (snapshot.userRatingMap.containsKey(userId))
					userRating = snapshot.userRatingMap.get(userId);
				else {
					userRating = new UserRating(userId);
					snapshot.userRatingMap.put(userId, userRating);
				}
				userRating.put(itemId, rating);

				RatingVector itemRating = null;
				if (snapshot.itemRatingMap.containsKey(itemId))
					itemRating = snapshot.itemRatingMap.get(itemId);
				else {
					itemRating = new ItemRating(itemId);
					snapshot.itemRatingMap.put(itemId, itemRating);
				}
				itemRating.put(userId, rating);
				
			} // end for column
			
		} // end for row
		
		
		snapshot.userProfiles = MemProfiles.createEmpty(DataConfig.USERID_FIELD, Type.integer);
		((MemProfiles)snapshot.userProfiles).fillUnion(snapshot.userRatingMap.keySet(), DataConfig.USERID_FIELD);
		
		snapshot.itemProfiles = MemProfiles.createEmpty(DataConfig.ITEMID_FIELD, Type.integer);
		((MemProfiles)snapshot.itemProfiles).fillUnion(snapshot.itemRatingMap.keySet(), DataConfig.ITEMID_FIELD);
		
		snapshot.enhance();
		return snapshot;
	}
	
	
	/**
	 * Create snapshot from resource specified by data configuration.
	 * @param config data configuration referring to specified resource (database, file directory, etc.).
	 * @return {@link SnapshotImpl} created from resource specified by data configuration.
	 */
	public static SnapshotImpl create(DataConfig config) {
		
		DataDriver ddriver = DataDriver.create(config.getStoreUri());
		if (ddriver != null && ddriver.getType() == DataType.file) {
			SnapshotImpl snapshot = fileCreate(config);
			if (snapshot != null)
				return snapshot;
		}
		
		ProviderImpl provider = new ProviderImpl(config);
		
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
			
			
			SnapshotImpl dataset = new SnapshotImpl();
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
	 * @return {@link SnapshotImpl} from file.
	 */
	private static SnapshotImpl fileCreate(DataConfig config) {
		
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
					
					Rating rating = new Rating(ratingValue);
					rating.ratedDate = profile.getValueAsTime(DataConfig.RATING_DATE_FIELD);
					
					RatingVector user = null;
					if (userRatingMap.containsKey(userId))
						user = userRatingMap.get(userId);
					else {
						user = new UserRating(userId);
						userRatingMap.put(userId, user);
					}
					user.put(itemId, rating);
					
					RatingVector item = null;
					if (itemRatingMap.containsKey(itemId))
						item = itemRatingMap.get(itemId);
					else {
						item = new ItemRating(itemId);
						itemRatingMap.put(itemId, item);
					}
					item.put(userId, rating);
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

		
		SnapshotImpl snapshot = null;
		try {
			snapshot = new SnapshotImpl();
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
