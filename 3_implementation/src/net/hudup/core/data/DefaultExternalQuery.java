/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.RemoteException;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ExternalItemInfo;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ExternalQueryAbstract;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.ExternalUserInfo;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.InterchangeAttributeMap;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.data.ProviderAssoc.CsvWriter;
import net.hudup.core.data.Rating;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * This class is default implementation of {@link ExternalQuery} interface.
 * Currently, the default implementation only maps units such as user, item, and rating.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DefaultExternalQuery extends ExternalQueryAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal provider.
	 */
	protected Provider internalProvider = null;

	
	/**
	 * External provider.
	 */
	protected Provider externalProvider = null;

	
	/**
	 * Default constructor.
	 */
	public DefaultExternalQuery() {
		super();
	}

	
	@Override
	public boolean setup(
			DataConfig internalConfig, 
			ExternalConfig externalConfig) throws RemoteException {
		
		try {
			close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		try {
			internalProvider = new ProviderImpl(internalConfig);
			externalProvider = new ProviderImpl(externalConfig);
			
//			config = internalProvider.getConfig(); //Added date: 2019.09.25 by Loc Nguyen
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			try {
				close();
			} 
			catch (Throwable e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		}
		
		return false;
	}
	
	
	/**
	 * This method is used for preparation of importing data.
	 */
	protected void preImportData() {
		DataConfig internalConfig = internalProvider.getConfig();

		if (internalConfig.getRatingUnit() == null)
			internalConfig.setRatingUnit(DataConfig.RATING_UNIT);
		
		if (internalConfig.getNominalUnit() == null)
			internalConfig.setNominalUnit(DataConfig.NOMINAL_UNIT);

		if (internalConfig.getUserUnit() == null)
			internalConfig.setUserUnit(DataConfig.USER_UNIT);

		if (internalConfig.getItemUnit() == null)
			internalConfig.setItemUnit(DataConfig.ITEM_UNIT);

		if (internalConfig.getAttributeMapUnit() == null)
			internalConfig.setAttributeMapUnit(DataConfig.ATTRIBUTE_MAP_UNIT);

		// 1. Setting up internal data
		UnitList internalUnitList = internalProvider.getUnitList();
		
		if (internalUnitList.contains(internalConfig.getRatingUnit()))
			internalProvider.deleteUnitData(internalConfig.getRatingUnit());
		else
			internalProvider.createUnit(internalConfig.getRatingUnit(), AttributeList.defaultRatingAttributeList());
			
		if (internalUnitList.contains(internalConfig.getNominalUnit())) {
			internalProvider.deleteNominal(internalConfig.getUserUnit());
			internalProvider.deleteNominal(internalConfig.getItemUnit());
		}
		else
			internalProvider.createUnit(internalConfig.getNominalUnit(), AttributeList.defaultNominalAttributeList());

		if (internalUnitList.contains(internalConfig.getUserUnit()))
			internalProvider.dropUnit(internalConfig.getUserUnit());
		internalProvider.createUnit(internalConfig.getUserUnit(), AttributeList.defaultUserAttributeList());

		if (internalUnitList.contains(internalConfig.getItemUnit()))
			internalProvider.dropUnit(internalConfig.getItemUnit());
		internalProvider.createUnit(internalConfig.getItemUnit(), AttributeList.defaultItemAttributeList());
		
		if (internalUnitList.contains(internalConfig.getAttributeMapUnit())) {
			internalProvider.deleteAttributeMap( internalConfig.getUserUnit() );
			internalProvider.deleteAttributeMap( internalConfig.getItemUnit() );
		}
		else
			internalProvider.createUnit(internalConfig.getAttributeMapUnit(), AttributeList.defaultAttributeMapAttributeList());
	}
	
	
	@Override
	public void importData(ProgressListener registeredListener) throws RemoteException {
		// TODO Auto-generated method stub
		DataConfig internalConfig = internalProvider.getConfig();
		ExternalConfig externalConfig = (ExternalConfig) externalProvider.getConfig();

		DataDriver ddriver = DataDriver.create(internalConfig.getStoreUri());
		if (ddriver != null && ddriver.getType() == DataType.file) {
			fileImportData(registeredListener);
			return;
		}

		preImportData();
		
		int progressTotal = 3;
		int progressStep = 0;

		AttributeList userAttributes = internalProvider.getUserAttributes();
		AttributeList itemAttributes = internalProvider.getItemAttributes();
		
		// 2. Inserting users
		Fetcher<Profile> userFetcher = null;
		if (externalConfig.getUserSql() != null)
			userFetcher = externalProvider.getProfiles(new ParamSql(externalConfig.getUserSql()), null);
		else
			userFetcher = externalProvider.getProfiles(externalConfig.getUserUnit(), null);
		try {
			
			int userType = 0;
			int userId = 0;
			while (userFetcher.next()) {
				Profile user = userFetcher.pick();
				if (user == null)
					continue;
				
				String externalUserId = user.getValueAsString(externalConfig.getUserIdField());
				String externalUserType = user.getValueAsString(externalConfig.getUserTypeField());
				if (externalUserId == null || externalUserType == null) //Fixing bug date: 2019.08.09 by Loc Nguyen
					continue;
				
				ExternalRecord externalRecord = new ExternalRecord(
						externalConfig.getUserUnit(), 
						externalConfig.getUserIdField(), 
						externalUserId);

				userId++;
				userType++;
				Profile userProfile = new Profile(userAttributes);
				userProfile.setIdValue(userId);
				userProfile.setValue(DataConfig.USER_TYPE_FIELD, userType);
				internalProvider.insertUserProfile(userProfile, externalRecord);
				
				InterchangeAttributeMap userMap = new InterchangeAttributeMap(
						new InternalRecord(internalConfig.getUserUnit(), DataConfig.USER_TYPE_FIELD, userType), 
						new ExternalRecord(externalConfig.getUserUnit(), externalConfig.getUserTypeField(), externalUserType));
				internalProvider.insertAttributeMap(userMap);
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (userFetcher != null)
					userFetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null)
			registeredListener.receiveProgress(
				new ProgressEvent(this, progressTotal, ++progressStep, "Users are inserted"));
		
		
		// 3. Inserting items
		Fetcher<Profile> itemFetcher = null;
		if (externalConfig.getItemSql() != null)
			itemFetcher = externalProvider.getProfiles(new ParamSql(externalConfig.getItemSql()), null);
		else
			itemFetcher = externalProvider.getProfiles(externalConfig.getItemUnit(), null);
		try {
			
			int itemType = 0;
			int itemId = 0;
			while (itemFetcher.next()) {
				Profile item = itemFetcher.pick();
				if (item == null)
					continue;
				
				String externalItemId = item.getValueAsString(externalConfig.getItemIdField());
				String externalItemType = item.getValueAsString(externalConfig.getItemTypeField());
				if (externalItemId == null || externalItemType == null) //Fixing bug date: 2019.08.09 by Loc Nguyen
					continue;
				
				ExternalRecord externalRecord = new ExternalRecord(
						externalConfig.getItemUnit(), 
						externalConfig.getItemIdField(), 
						externalItemId);
				
				itemId++;
				itemType++;
				Profile itemProfile = new Profile(itemAttributes);
				itemProfile.setIdValue(itemId);
				itemProfile.setValue(DataConfig.ITEM_TYPE_FIELD, itemType);
				internalProvider.insertItemProfile(itemProfile, externalRecord);
				
				InterchangeAttributeMap itemMap = new InterchangeAttributeMap(
						new InternalRecord(internalConfig.getItemUnit(), DataConfig.ITEM_TYPE_FIELD, itemType), 
						new ExternalRecord(externalConfig.getItemUnit(), externalConfig.getItemTypeField(), externalItemType));
				internalProvider.insertAttributeMap(itemMap);
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (itemFetcher != null)
					itemFetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null)
			registeredListener.receiveProgress(
				new ProgressEvent(this, progressTotal, ++progressStep, "Item are inserted"));
		

		// 4. Inserting ratings
		Fetcher<Profile> ratingFetcher = null;
		if (externalConfig.getRatingSql() != null)
			ratingFetcher = externalProvider.getProfiles(new ParamSql(externalConfig.getRatingSql()), null);
		else
			ratingFetcher = externalProvider.getProfiles(externalConfig.getRatingUnit(), null);
		try {
			
			while (ratingFetcher.next()) {
				Profile rating = ratingFetcher.pick();
				if (rating == null)
					continue;
				
				String externalRatingUserId = rating.getValueAsString(externalConfig.getRatingUserIdField());
				String externalRatingItemId = rating.getValueAsString(externalConfig.getRatingItemIdField());
				double externalRating = rating.getValueAsReal(externalConfig.getRatingField());
				if (externalRatingUserId == null || externalRatingItemId == null || !Util.isUsed(externalRating))
					continue;
				
				InterchangeAttributeMap userMap = internalProvider.getAttributeMapByExternal(
					new ExternalRecord(
						externalConfig.getUserUnit(), 
						externalConfig.getUserIdField(), 
						externalRatingUserId));
				
				InterchangeAttributeMap itemMap = internalProvider.getAttributeMapByExternal(
					new ExternalRecord(
						externalConfig.getItemUnit(), 
						externalConfig.getItemIdField(),
						externalRatingItemId));
				
				if (userMap == null || itemMap == null)
					continue;
					
				int internalUserId = -1;
				int internalItemId = -1;
				try {
					internalUserId = Integer.parseInt(userMap.internalRecord.value.toString());
					internalItemId = Integer.parseInt(itemMap.internalRecord.value.toString());
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					continue;
				}
				if (internalUserId < 0 || internalItemId < 0)
					continue;
				
				internalProvider.insertRating(internalUserId, internalItemId, new Rating(externalRating));
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (ratingFetcher != null)
					ratingFetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null)
			registeredListener.receiveProgress(
				new ProgressEvent(this, progressTotal, ++progressStep, "Ratings are inserted"));

	
	}

	
	/**
	 * Fill in Hudup database by importing data from outside (external) database with note that Hudup database is a set of files.
	 * @param registeredListener the registered listener to observe the importing process.
	 * This listener can do some particular tasks such as showing the progress bar so that users know the progress of importing process.
	 * @throws RemoteException if any error raises.
	 */
	private void fileImportData(ProgressListener registeredListener) throws RemoteException {
		// TODO Auto-generated method stub
		preImportData();
		
		DataConfig internalConfig = internalProvider.getConfig();
		ExternalConfig externalConfig = (ExternalConfig) externalProvider.getConfig();
		int progressTotal = 3;
		int progressStep = 0;

		AttributeList userAttributes = internalProvider.getUserAttributes();
		AttributeList itemAttributes = internalProvider.getItemAttributes();
		AttributeList attMapAttributes = internalProvider.getProfileAttributes(internalConfig.getAttributeMapUnit());
		AttributeList ratingAttributes = internalProvider.getProfileAttributes(internalConfig.getRatingUnit());
		
		ProviderAssoc assoc = Util.getFactory().createProviderAssoc(internalConfig);
		CsvWriter csvUserWriter = assoc.getWriter(internalConfig.getUserUnit(), true);
		CsvWriter csvItemWriter = assoc.getWriter(internalConfig.getItemUnit(), true);
		CsvWriter csvAttMapWriter = assoc.getWriter(internalConfig.getAttributeMapUnit(), true);
		CsvWriter csvRatingWriter = assoc.getWriter(internalConfig.getRatingUnit(), true);

		// 2. Inserting users
		Fetcher<Profile> userFetcher = null;
		if (externalConfig.getUserSql() != null)
			userFetcher = externalProvider.getProfiles(new ParamSql(externalConfig.getUserSql()), null);
		else
			userFetcher = externalProvider.getProfiles(externalConfig.getUserUnit(), null);
		try {

			int userId = 0;
			int userType = 0;
			while (userFetcher.next()) {
				Profile user = userFetcher.pick();
				if (user == null)
					continue;
				
				String externalUserId = user.getValueAsString(externalConfig.getUserIdField());
				String externalUserType = user.getValueAsString(externalConfig.getUserTypeField());
				if (externalUserId == null || externalUserType == null) //Fixing bug date: 2019.08.09 by Loc Nguyen
					continue;
				
				ExternalRecord externalRecord = new ExternalRecord(
						externalConfig.getUserUnit(), 
						externalConfig.getUserIdField(), 
						externalUserId);

				userId++;
				userType++;
				Profile userProfile = new Profile(userAttributes);
				userProfile.setIdValue(userId);
				userProfile.setValue(DataConfig.USER_TYPE_FIELD, userType);
				insertUserProfile(internalConfig, csvUserWriter, csvAttMapWriter, attMapAttributes, userProfile, externalRecord);
				
				InterchangeAttributeMap userMap = new InterchangeAttributeMap(
						new InternalRecord(internalConfig.getUserUnit(), DataConfig.USER_TYPE_FIELD, userType), 
						new ExternalRecord(externalConfig.getUserUnit(), externalConfig.getUserTypeField(), externalUserType));
				insertAttributeMap(csvAttMapWriter, attMapAttributes, userMap);
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (userFetcher != null)
					userFetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null)
			registeredListener.receiveProgress(
				new ProgressEvent(this, progressTotal, ++progressStep, "Users are inserted"));
		
		
		// 3. Inserting items
		Fetcher<Profile> itemFetcher = null;
		if (externalConfig.getItemSql() != null)
			itemFetcher = externalProvider.getProfiles(new ParamSql(externalConfig.getItemSql()), null);
		else
			itemFetcher = externalProvider.getProfiles(externalConfig.getItemUnit(), null);
		try {
			
			int itemId = 0;
			int itemType = 0;
			while (itemFetcher.next()) {
				Profile item = itemFetcher.pick();
				if (item == null)
					continue;
				
				String externalItemId = item.getValueAsString(externalConfig.getItemIdField());
				String externalItemType = item.getValueAsString(externalConfig.getItemTypeField());
				if (externalItemId == null || externalItemType == null) //Fixing bug date: 2019.08.09 by Loc Nguyen
					continue;
				
				ExternalRecord externalRecord = new ExternalRecord(
						externalConfig.getItemUnit(), 
						externalConfig.getItemIdField(), 
						externalItemId);
				
				itemId++;
				itemType++;
				Profile itemProfile = new Profile(itemAttributes);
				itemProfile.setIdValue(itemId);
				itemProfile.setValue(DataConfig.ITEM_TYPE_FIELD, itemType);
				insertItemProfile(internalConfig, csvItemWriter, csvAttMapWriter, attMapAttributes, itemProfile, externalRecord);
				
				InterchangeAttributeMap itemMap = new InterchangeAttributeMap(
						new InternalRecord(internalConfig.getItemUnit(), DataConfig.ITEM_TYPE_FIELD, itemType), 
						new ExternalRecord(externalConfig.getItemUnit(), externalConfig.getItemTypeField(), externalItemType));
				insertAttributeMap(csvAttMapWriter, attMapAttributes, itemMap);
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (itemFetcher != null)
					itemFetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null)
			registeredListener.receiveProgress(
				new ProgressEvent(this, progressTotal, ++progressStep, "Item are inserted"));
		

		// 4. Inserting ratings
		Fetcher<Profile> ratingFetcher = null;
		if (externalConfig.getRatingSql() != null)
			ratingFetcher = externalProvider.getProfiles(new ParamSql(externalConfig.getRatingSql()), null);
		else
			ratingFetcher = externalProvider.getProfiles(externalConfig.getRatingUnit(), null);
		try {
			
			while (ratingFetcher.next()) {
				Profile rating = ratingFetcher.pick();
				if (rating == null)
					continue;
				
				String externalRatingUserId = rating.getValueAsString(externalConfig.getRatingUserIdField());
				String externalRatingItemId = rating.getValueAsString(externalConfig.getRatingItemIdField());
				double externalRating = rating.getValueAsReal(externalConfig.getRatingField());
				if (externalRatingUserId == null || externalRatingItemId == null || !Util.isUsed(externalRating))
					continue;
				
				InterchangeAttributeMap userMap = internalProvider.getAttributeMapByExternal(
					new ExternalRecord(
						externalConfig.getUserUnit(), 
						externalConfig.getUserIdField(), 
						externalRatingUserId));
				
				InterchangeAttributeMap itemMap = internalProvider.getAttributeMapByExternal(
					new ExternalRecord(
						externalConfig.getItemUnit(), 
						externalConfig.getItemIdField(),
						externalRatingItemId));
				
				if (userMap == null || itemMap == null)
					continue;
					
				int internalUserId = -1;
				int internalItemId = -1;
				try {
					internalUserId = Integer.parseInt(userMap.internalRecord.value.toString());
					internalItemId = Integer.parseInt(itemMap.internalRecord.value.toString());
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					continue;
				}
				if (internalUserId < 0 || internalItemId < 0)
					continue;
				
				insertRating(csvRatingWriter, ratingAttributes, internalUserId, internalItemId, new Rating(externalRating));
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (ratingFetcher != null)
					ratingFetcher.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		if (registeredListener != null)
			registeredListener.receiveProgress(
				new ProgressEvent(this, progressTotal, ++progressStep, "Ratings are inserted"));

	
		try {
			csvUserWriter.close();
			csvItemWriter.close();
			csvAttMapWriter.close();
			csvRatingWriter.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "default_external_query";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Default external query";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new DefaultExternalQuery();
	}


	@Override
	public ExternalUserInfo getUserInfo(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		DataConfig internalConfig = internalProvider.getConfig();
		
		InterchangeAttributeMap inter = internalProvider.getAttributeMap(
				new InternalRecord(
						internalConfig.getUserUnit(), 
						internalConfig.getAsString(DataConfig.USERID_FIELD), 
						userId));
		if (inter == null)
			return null;
		
		String externalUserId = inter.externalRecord.value.toString();
		ExternalConfig externalConfig = (ExternalConfig) externalProvider.getConfig();
		
		Profile condition = new Profile(externalProvider.getProfileAttributes(externalConfig.getUserUnit()));
		condition.setValue(externalConfig.getUserIdField(), externalUserId);
		Profile profile = externalProvider.getProfile(externalConfig.getUserUnit(), condition);
		if (profile == null)
			return null;

		ExternalUserInfo userInfo = new ExternalUserInfo();
		userInfo.externalId = externalUserId;
		
		return userInfo;
	}

	
	@Override
	public ExternalItemInfo getItemInfo(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		DataConfig internalConfig = internalProvider.getConfig();
		
		InterchangeAttributeMap inter = internalProvider.getAttributeMap(
				new InternalRecord(
						internalConfig.getItemUnit(), 
						internalConfig.getAsString(DataConfig.ITEMID_FIELD), 
						itemId));
		if (inter == null)
			return null;
		
		String externalItemId = inter.externalRecord.value.toString();
		ExternalConfig externalConfig = (ExternalConfig) externalProvider.getConfig();
		
		Profile condition = new Profile(externalProvider.getProfileAttributes(externalConfig.getItemUnit()));
		condition.setValue(externalConfig.getItemIdField(), externalItemId);
		Profile profile = externalProvider.getProfile(externalConfig.getItemUnit(), condition);
		if (profile == null)
			return null;
		
		ExternalItemInfo itemInfo = new ExternalItemInfo();
		itemInfo.externalId = externalItemId;
		return itemInfo;
		
	}


	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		if (externalProvider == null)
			return null;
		else
			return externalProvider.getConfig();
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
		
		try {
			if (internalProvider != null)
				internalProvider.close();
			internalProvider = null;
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			if (externalProvider != null)
				externalProvider.close();
			externalProvider = null;
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
	}

	
	/**
	 * Inserting user profile.
	 * @param internalConfig specified internal configuration.
	 * @param userWriter CSV writer for user profile.
	 * @param csvAttMapWriter CSV writer for attribute map.
	 * @param attMapAttributes attribute list.
	 * @param user user profile.
	 * @param externalRecord external record.
	 * @return whether insert profile successfully
	 */
	private static boolean insertUserProfile(DataConfig internalConfig, CsvWriter userWriter, CsvWriter csvAttMapWriter, AttributeList attMapAttributes, Profile user, ExternalRecord externalRecord) {
		// TODO Auto-generated method stub
		
		boolean result = insertProfile(userWriter, user);
		
		if (externalRecord != null && externalRecord.isValid()) {

			InternalRecord internalRecord = new InternalRecord(
					internalConfig.getUserUnit(), 
					DataConfig.USERID_FIELD, 
					user.getIdValueAsInt());
			
			InterchangeAttributeMap attributeMap = new InterchangeAttributeMap(internalRecord, externalRecord);
			result &= insertAttributeMap(csvAttMapWriter, attMapAttributes, attributeMap);
		}

		return result;
	}

	
	/**
	 * Inserting item profile.
	 * @param internalConfig specified internal configuration.
	 * @param itemWriter CSV writer for item profile.
	 * @param csvAttMapWriter CSV writer for attribute map.
	 * @param attMapAttributes attribute list.
	 * @param item item profile.
	 * @param externalRecord external record.
	 * @return whether insert profile successfully
	 */
	private static boolean insertItemProfile(DataConfig internalConfig, CsvWriter itemWriter, CsvWriter csvAttMapWriter, AttributeList attMapAttributes, Profile item, ExternalRecord externalRecord) {
		// TODO Auto-generated method stub
		
		boolean result = insertProfile(itemWriter, item);
		
		if (externalRecord != null && externalRecord.isValid()) {

			InternalRecord internalRecord = new InternalRecord(
					internalConfig.getItemUnit(), 
					DataConfig.ITEMID_FIELD, 
					item.getIdValueAsInt());
			
			InterchangeAttributeMap attributeMap = new InterchangeAttributeMap(internalRecord, externalRecord);
			result &= insertAttributeMap(csvAttMapWriter, attMapAttributes, attributeMap);
		}

		return result;
	}

	
	/**
	 * Inserting rating.
	 * @param ratingWriter rating writer.
	 * @param ratingAttributes rating attributes.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param rating specified rating.
	 * @return whether insert profile successfully
	 */
	private boolean insertRating(CsvWriter ratingWriter, AttributeList ratingAttributes, int userId, int itemId, Rating rating) {
		
		Profile profile = new Profile(ratingAttributes);
		profile.setValue(DataConfig.USERID_FIELD, userId);
		profile.setValue(DataConfig.ITEMID_FIELD, itemId);
		profile.setValue(DataConfig.RATING_FIELD, rating.value);
		profile.setValue(DataConfig.RATING_DATE_FIELD, rating.ratedDate);
		
		return insertProfile(ratingWriter, profile);
	}
	
	
	/**
	 * Inserting attributes.
	 * @param attMapWriter writer for attribute.
	 * @param attMapAttributes attribute list.
	 * @param attributeMap interchanged attribute map.
	 * @return whether insert profile successfully.
	 */
	private static boolean insertAttributeMap(CsvWriter attMapWriter, AttributeList attMapAttributes, InterchangeAttributeMap attributeMap) {
		if (!attributeMap.isValid())
			return false;
		
		Profile profile = new Profile(attMapAttributes);
		
		profile.setValue(DataConfig.INTERNAL_UNIT_FIELD, attributeMap.internalRecord.unit);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, attributeMap.internalRecord.attribute);
		profile.setValue(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, attributeMap.internalRecord.value);
		
		profile.setValue(DataConfig.EXTERNAL_UNIT_FIELD, attributeMap.externalRecord.unit);
		profile.setValue(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD, attributeMap.externalRecord.attribute);
		profile.setValue(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD, attributeMap.externalRecord.value);
		
		return insertProfile(attMapWriter, profile);
	}
	
	
	/**
	 * Inserting profile to CSV file.
	 * @param writer CSV writer.
	 * @param profile specified profile.
	 * @return whether insert profile successfully.
	 */
	private static boolean insertProfile(CsvWriter writer, Profile profile) {
		if (profile == null || profile.getAttCount() == 0)
			return false;
		
		try {
			writer.writeRecord(profile.toStringArray());
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
}
