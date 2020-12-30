/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is the default implementation of Scanner.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ScannerImpl extends Scanner {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Internal provider.
	 */
	protected Provider provider = null;
	
	
	/**
	 * Internal user attribute list.
	 */
	protected AttributeList userAttributes = null;
	
	
	/**
	 * Internal item attribute list.
	 */
	protected AttributeList itemAttributes = null;;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public ScannerImpl(DataConfig config) {
		this (new ProviderImpl(config));
	}
	
	
	/**
	 * Constructor with specified provider.
	 * @param provider specified provider.
	 */
	protected ScannerImpl(Provider provider) {
		this.provider = provider;
		this.userAttributes = provider.getUserAttributes();
		this.itemAttributes = provider.getItemAttributes();
		
		this.config = provider.getConfig();
		
	}
	
	
	@Override
	public Fetcher<Integer> fetchUserIds() {
		return provider.getProfileIds(config.getUserUnit());
	}

	
	@Override
	public int getUserId(Serializable externalUserId) {
		Fetcher<Integer> userIds = fetchUserIds();
		int returnUserId = -1;
		try {
			while (userIds.next()) {
				Integer userId = userIds.pick();
				if (userId == null || userId < 0)
					continue;
				
				ExternalRecord record = getUserExternalRecord(userId);
				if (record != null && record.value.equals(externalUserId)) {
					returnUserId = userId;
					break;
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			returnUserId = -1;
		}
		finally {
			try {
				userIds.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		return returnUserId;
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		InterchangeAttributeMap attributeMap = provider.getUserAttributeMap(userId);
		if (attributeMap != null)
			return attributeMap.externalRecord;
		else
			return null;
		
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		
		return provider.getProfileIds(config.getItemUnit());
	}

	
	@Override
	public int getItemId(Serializable externalItemId) {
		Fetcher<Integer> itemIds = fetchItemIds();
		int returnItemId = -1;
		try {
			while (itemIds.next()) {
				Integer itemId = itemIds.pick();
				if (itemId == null || itemId < 0)
					continue;
				
				ExternalRecord record = getItemExternalRecord(itemId);
				if (record != null && record.value.equals(externalItemId)) {
					returnItemId = itemId;
					break;
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			returnItemId = -1;
		}
		finally {
			try {
				itemIds.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		return returnItemId;
	}

	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		
		InterchangeAttributeMap attributeMap = provider.getItemAttributeMap(itemId);
		
		if (attributeMap != null)
			return attributeMap.externalRecord;
		else
			return null;
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		RatingVector vRating = provider.getUserRatingVector(userId);
		if (vRating == null)
			return null;
		else
			return vRating.get(itemId);
	}

	
	@Override
	public RatingVector getUserRating(int userId) {
		
		return provider.getUserRatingVector(userId);
	}

	
	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		Fetcher<Integer> userIds = fetchUserIds();
		
		return new MetaFetcher<Integer, RatingVector>(userIds) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public RatingVector create(Integer u) {
				return getUserRating(u);
			}
			
		};
		
	}

	
	@Override
	public RatingVector getItemRating(int itemId) {
		
		return provider.getItemRatingVector(itemId);
	}

	
	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		Fetcher<Integer> itemIds = fetchItemIds();
		
		return new MetaFetcher<Integer, RatingVector>(itemIds) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public RatingVector create(Integer u) {
				return getItemRating(u);
			}
			
		};

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
	public Profile getUserProfile(int userId) {
		
		return provider.getUserProfile(userId);
	}

	
	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		return provider.getProfiles(config.getUserUnit(), null);
		
	}

	
	@Override
	public AttributeList getUserAttributes() {
		return userAttributes;
	}

	
	@Override
	public Profile getItemProfile(int itemId) {
		
		return provider.getItemProfile(itemId);
	}

	
	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		return provider.getProfiles(config.getItemUnit(), null);
	}

	
	@Override
	public AttributeList getItemAttributes() {
		
		return itemAttributes;
	}

	
	@Override
	public Profile profileOf(Context context) {
		return provider.getCTSManager().profileOf(context);
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		return provider.getCTSManager().profilesOf(ctxTemplateId);
	}


	@Override
	public Fetcher<Profile> fetchSample() {
		return provider.getProfiles(config.getSampleUnit(), null);
	}


	@Override
	public Object clone() {
		ScannerImpl scanner = new ScannerImpl((Provider)this.provider.clone());
		return scanner;
	}
	
	
	@Override
	public Dataset catchup() {
		SnapshotImpl snapshot = new SnapshotImpl();
		
		Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
		Set<Integer> userIds = Util.newSet();
		FetcherUtil.fillCollection(userIds, fetchUserIds(), true);
		for (int userId : userIds) {
			ExternalRecord externalRecord = getUserExternalRecord(userId);
			if (externalRecord != null)
				externalUserRecordMap.put(userId, externalRecord);
		}
		
		Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();
		Set<Integer> itemIds = Util.newSet();
		FetcherUtil.fillCollection(itemIds, fetchItemIds(), true);
		for (int itemId : itemIds) {
			ExternalRecord externalRecord = getItemExternalRecord(itemId);
			if (externalRecord != null)
				externalItemRecordMap.put(itemId, externalRecord);
		}

		List<Profile> sampleProfiles = Util.newList();
		Fetcher<Profile> sampleFetcher = provider.getProfiles(config.getSampleUnit(), null);
		FetcherUtil.fillCollection(sampleProfiles, sampleFetcher, true);
		
		snapshot.assign(
				(DataConfig)config.clone(),
				externalUserRecordMap,
				RatingVector.transfer(fetchUserRatings(), false, true), 
				MemProfiles.create(fetchUserProfiles(), true),
				externalItemRecordMap,
				RatingVector.transfer(fetchItemRatings(), false, true), 
				MemProfiles.create(fetchItemProfiles(), true),
				(ContextTemplateSchema) provider.getCTSManager().getCTSchema().transfer(),
				provider.getCTSManager().createCTSProfiles(),
				sampleProfiles);

		return snapshot;
	}
	

	@Override
	public Dataset selectByContexts(ContextList contexts) {
		
		SemiScanner semiScanner = new SemiScanner(config);
		
		semiScanner.userRatingMap = RatingVector.select(semiScanner.userRatingMap, contexts);
		semiScanner.itemRatingMap = RatingVector.select(semiScanner.itemRatingMap, contexts);
		
		LogUtil.info("ScannerImpl@selectByContexts@557: Because context structure is complex, " +
				"it consumes a lot of redundant operations to select context via database instead of via memory. " +
				"So it isn't a perfect solution but very good in this case");
		
		return semiScanner;
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		return provider.getCTSManager().getCTSchema();
	}


	@Override
	public Provider getProvider() {
		return provider;
	}
	
	
	@Override
	public void clear() {
		super.clear();
		
		if (provider != null) {
			provider.close();
			provider = null;
		}
		
		if (userAttributes != null) {
			userAttributes.clear();
			userAttributes = null;
		}
		
		if (itemAttributes != null) {
			itemAttributes.clear();
			itemAttributes = null;
		}
		
	}


}
