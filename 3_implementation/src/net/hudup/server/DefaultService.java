/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.client.ServerInfo;
import net.hudup.core.client.Service;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.InterchangeAttributeMap;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Scanner;
import net.hudup.core.data.Snapshot;
import net.hudup.core.data.SnapshotImpl;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;

/**
 * {@link DefaultService} class is default implementation of {@link Service} interface. {@link DefaultService} uses {@link Recommender} and {@link Provider} for processing recommendation request and update request, respectively.
 * Developers are suggested to build up their own services.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DefaultService implements Service, PluginChangedListener, AutoCloseable {


	/**
	 * Server configuration.
	 */
	protected PowerServerConfig serverConfig = null;
	
	
	/**
	 * Evaluator configuration map.
	 */
	protected Map<String, EvaluatorConfig> evaluatorConfigMap = Util.newMap();

	
	/**
	 * Internal transaction.
	 */
	protected Transaction trans = null;
	
	
	/**
	 * Recommender algorithm.
	 */
	protected Recommender recommender = null;
	
	
	/**
	 * Creating recommender algorithm.
	 */
	protected AbstractRunner recommenderCreator = null;
	
	
	/**
	 * Provider. Do not retrieve directly this variable. Using method {@link #getProvider()} instead.
	 */
	private Provider provider = null;
	
	
	/**
	 * Account name.
	 */
	protected String account = null;
	
	
	/**
	 * Account password.
	 */
	protected String password = null;

	
	/**
	 * Constructor with specified transaction.
	 * @param trans specified transaction.
	 */
	public DefaultService(Transaction trans) {
		super();
		this.trans = trans;
		
		this.recommenderCreator = new AbstractRunner() {
			
			@Override
			protected void task() {
				if (serverConfig == null) {
					thread = null;
					paused = false;
					return;
				}
				
				Recommender recommender0 = null;
				try {
					recommender0 = createRecommender(serverConfig);
				} catch (Exception e) {LogUtil.trace(e);}
				
				if (recommender0 != null) {
					synchronized (this) {
						trans.lockWrite();
						
						try {
							if (recommender != null) recommender.unsetup();
						} catch (Throwable e) {LogUtil.trace(e);}
						
						recommender = recommender0;
						
						trans.unlockWrite();
						
						LogUtil.info("Service creates recommender successful");
					}
				}
				
				thread = null;
				paused = false;
			}
			
			@Override
			protected void clear() {}

			@Override
			public synchronized boolean stop() {
				return super.forceStop();
			}
			
		};
	}

	
	/**
	 * Open service with specified configuration and parameters.
	 * @param serverConfig specified configuration.
	 * @param params additional parameters so that recommender algorithm sets up.
	 * @return whether open service successfully.
	 */
	public boolean open(PowerServerConfig serverConfig, Object... params) {
		try {
			close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		boolean opened = true;
		this.serverConfig = serverConfig;
		try {
			updateRecommender(params);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			close();
			opened = false;
			
			LogUtil.error("Service fail to open, caused by " + e.getMessage());
		}
		
		return opened;
	}
	
	
	/**
	 * Updating internal recommender.
	 * @param params additional parameters so that recommender algorithm sets up.
	 */
	protected void updateRecommender(Object...params) {
		try {
			recommenderCreator.stop();
			recommenderCreator.start();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Creating recommender.
	 * @param serverConfig server configuration.
	 * @param params additional parameters so that recommender algorithm sets up.
	 * @return recommender created from server configuration.
	 */
	protected static Recommender createRecommender(PowerServerConfig serverConfig, Object...params) {
		try {
			Dataset dataset = null;
			if (serverConfig.isDatasetEmpty()) {
				dataset = new SnapshotImpl();
				dataset.setConfig((DataConfig)serverConfig.clone());
			}
			else {
				dataset = serverConfig.getParser().parse((DataConfig)serverConfig.clone());
			}
			dataset.setExclusive(true);
	
			Recommender recommender = (Recommender) serverConfig.getRecommender().newInstance();
			recommender.getConfig().putAll(serverConfig.getRecommender().getConfig());
			recommender.setup(dataset, params);
			
			return recommender;
		}
		catch (Exception e) {
			LogUtil.trace(e);
			return null;
		}
	}
	
	
	/**
	 * Testing whether service is opened.
	 * @return whether service is opened.
	 */
	public boolean isOpened() {
		return recommender != null;
	}
	
	
	@Override
	public void close() {
		try {
			recommenderCreator.stop();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		if (recommender != null) {
			try {
				recommender.unsetup();
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		recommender = null;
		
		if (provider != null) {
			try {
				provider.close();
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		provider = null;
	}


	/**
	 * Transfer to target service.
	 * @param target target service.
	 * @return whether transfer successfully.
	 */
	public boolean transferTo(DefaultService target) {
		if (!isOpened())
			return false;
		
		boolean result = true;
		trans.lockWrite();
		try {
			if (this.recommender != null) {
				try {
					if (target.recommender != null) target.recommender.unsetup();
				} catch (Throwable e) {LogUtil.trace(e);}
				target.recommender = this.recommender;
				
				this.recommender = null;
				this.close();
			}
			else
				result = false;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to transfer to target service, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	/**
	 * Getting dataset (scanner).
	 * @return scanner inside recommender algorithm.
	 */
	protected Dataset getDataset() {
		if (isOpened()) {
			try {
				return recommender.getDataset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				return null;
			}
		}
		else
			return null;
	}
	
	
	/**
	 * That provider is null means read only service.
	 * @return provider.
	 */
	protected Provider getProvider() {
		Dataset dataset = getDataset();
		if (dataset == null) {
			if (provider != null) {
				try {
					provider.close();
				}
				catch (Exception e) {
					LogUtil.trace(e);
				}
				provider = null;
			}
			return null;
		}
		else if (dataset instanceof Scanner) {
			try {
				if (provider != null) provider.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			provider = null;
			
			return ((Scanner)dataset).getProvider(); //Only scanner owns provider.
		}
		else {
			if (provider == null) {
				DataConfig config = (DataConfig)dataset.getConfig().clone();
				provider = new ProviderImpl(config);
			}
			return provider;
		}
	}

	
	@Override
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {

	}


	@Override
	public boolean isIdle() throws RemoteException {
		return !isOpened();
	}


	@Override
	public int getPort() throws RemoteException {
		return serverConfig.getServerPort();
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = recommender.estimate(param, queryIds);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = null;
			
			LogUtil.error("Service fail to estimate, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = recommender.recommend(param, maxRecommend);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = null;
			
			LogUtil.error("Service fail to recommend, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}

	
	@Override
	public RatingVector recommend(int userId, int maxRecommend)
			throws RemoteException {
		RatingVector result = null;
		
		trans.lockRead();
		try {
			RecommendParam param = new RecommendParam(userId);
			result = recommender.recommend(param, maxRecommend);
			param.clear();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = null;
			
			LogUtil.error("Service fail to recommend first time, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		if (result == null) {
			
			trans.lockRead();
			try {
				RecommendParam param = getProvider().getRecommendParam(userId);
				if (param != null) {
					result = recommender.recommend(param, maxRecommend);
					param.clear();
				}
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				result = null;
				
				LogUtil.error("Service fail to recommend second time, caused by " + e.getMessage());
			}
			finally {
				trans.unlockRead();
			}
		}
		
		return result;
	}


	@Override
	@Deprecated
	public net.hudup.core.client.Recommendlet recommend(
			String listenerHost, 
			int listenerPort,
			String regName, 
			Serializable externalUserId, 
			Serializable externalItemId,
			int maxRecommend, 
			Rating rating) throws RemoteException {
		
		if (externalItemId != null && rating != null && rating.isRated()) {
			trans.lockWrite();
			try {
				int userId = getDataset().getUserId(externalUserId);
				int itemId = getDataset().getItemId(externalItemId);
				
				if (userId >= 0 && itemId >= 0)
					getProvider().updateRating(userId, itemId, rating);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				
				LogUtil.error("Service fail to update rating when recommending net.hudup.core.client.Recommendlet, caused by " + e.getMessage());
			}
			finally {
				trans.unlockWrite();
			}
		}
		
		net.hudup.core.client.Recommendlet result = null;
		
		trans.lockRead();
		try {
			DataConfig cfg = getDataset().getConfig();
			
			DatasetMetadata metadata = cfg.getMetadata();
			ServerInfo listenerInfo = new ServerInfo();
			listenerInfo.setHost(listenerHost);
			listenerInfo.setPort(listenerPort);
			
			int userId = getDataset().getUserId(externalUserId);
			userId = userId < 0 ? Integer.parseInt(externalUserId.toString()) : userId; // for testing, should be removed
			if (userId >= 0) {
			
				RatingVector recommendedItems = recommend(userId, maxRecommend);
				Set<Integer> itemIds = Util.newSet();
				if (recommendedItems != null)
					itemIds.addAll(recommendedItems.fieldIds());
				
				ClientStubImpl clientStub = ClientStubImpl.create(regName, itemIds, null);
				result = new net.hudup.core.client.Recommendlet(
						externalUserId.toString(),
						externalItemId != null ? externalItemId.toString() : null, 
						recommendedItems, 
						metadata,
						listenerInfo,
						clientStub);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = null;
			
			LogUtil.error("Service fail to recommend net.hudup.core.client.Recommendlet, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}


	@Override
	public boolean updateRating(RatingVector vRating) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateRating(vRating);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean updateRating(int userId, int itemId, Rating rating) 
			throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateRating(userId, itemId, rating);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public boolean deleteRating(RatingVector vRating) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteRating(vRating);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public Fetcher<Integer> getUserIds() throws RemoteException {
		Fetcher<Integer> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			
			fetcher = getDataset().fetchUserIds();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			fetcher = MemFetcher.createEmpty();
			
			LogUtil.error("Service fail to get user id (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

	
	@Override
	public RatingVector getUserRating(int userId) throws RemoteException {
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = getDataset().getUserRating(userId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = null;
			
			LogUtil.error("Service fail to get user rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}
	
	
	@Override
	public Fetcher<RatingVector> getUserRatings() throws RemoteException {
		Fetcher<RatingVector> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchUserRatings();
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			fetcher = MemFetcher.createEmpty();
			
			LogUtil.error("Service fail to get user rating (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}
	
	
	@Override
	public boolean deleteUserRating(int userId) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteUserRating(userId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete user rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public Profile getUserProfile(int userId) throws RemoteException {
		
		Profile user = null;
		
		trans.lockRead();
		try {
			user = getDataset().getUserProfile(userId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			user = null;
			
			LogUtil.error("Service fail to get user profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return user;
	}

	
	@Override
	public Profile getUserProfileByExternal(Serializable externalUserId)
			throws RemoteException {

		Profile user = null;
		
		trans.lockRead();
		try {
			int userId = getDataset().getUserId(externalUserId);
			if (userId >= 0)
				user = getDataset().getUserProfile(userId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			user = null;
			
			LogUtil.error("Service fail to get user profile by external, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return user;
	}


	@Override
	public Fetcher<Profile> getUserProfiles() throws RemoteException {
		Fetcher<Profile> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchUserProfiles();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			fetcher = MemFetcher.createEmpty();
			
			LogUtil.error("Service fail to get user profiles, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

		
	@Override
	public boolean updateUserProfile(Profile user) throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateUserProfile(user);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update user profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean deleteUserProfile(int userId) throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteUserProfile(userId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete user profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public AttributeList getUserAttributeList()
			throws RemoteException {
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getDataset().getUserAttributes();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			attributeList = null;
			
			LogUtil.error("Service fail to get user attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}

	
	@Override
	public ExternalRecord getUserExternalRecord(int userId)
			throws RemoteException {
		
		ExternalRecord externalRecord = null;
		
		trans.lockRead();
		try {
			externalRecord = getDataset().getUserExternalRecord(userId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			externalRecord = null;
			
			LogUtil.error("Service fail to get user external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return externalRecord;
	}


	@Override
	public Fetcher<Integer> getItemIds() throws RemoteException {
		Fetcher<Integer> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchItemIds();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			fetcher = MemFetcher.createEmpty();
			
			LogUtil.error("Service fail to get item id (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

	
	@Override
	public RatingVector getItemRating(int itemId) throws RemoteException {
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = getDataset().getItemRating(itemId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = null;
			
			LogUtil.error("Service fail to get item rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}
	
	
	@Override
	public Fetcher<RatingVector> getItemRatings() 
			throws RemoteException {
		
		Fetcher<RatingVector> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchItemRatings();
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			fetcher = MemFetcher.createEmpty();
			
			LogUtil.error("Service fail to get item rating (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

		
	@Override
	public boolean deleteItemRating(int itemId) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteItemRating(itemId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete item rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public Profile getItemProfile(int itemId) throws RemoteException {
		
		Profile item = null;
		
		trans.lockRead();
		try {
			item = getDataset().getItemProfile(itemId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			item = null;
			
			LogUtil.error("Service fail to get item profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return item;
	}

	
	@Override
	public Profile getItemProfileByExternal(Serializable externalItemId)
			throws RemoteException {

		Profile item = null;
		
		trans.lockRead();
		try {
			int itemId = getDataset().getItemId(externalItemId);
			if (itemId >= 0)
				item = getDataset().getItemProfile(itemId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			item = null;
			
			LogUtil.error("Service fail to get item profile by external, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return item;
	}


	@Override
	public Fetcher<Profile> getItemProfiles() throws RemoteException {
		Fetcher<Profile> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchItemProfiles();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			fetcher = MemFetcher.createEmpty();
			
			LogUtil.error("Service fail to get item profiles, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}
	
	
	@Override
	public boolean updateItemProfile(Profile item) throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateItemProfile(item);
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update item profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean deleteItemProfile(int itemId) throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteItemProfile(itemId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete item profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public AttributeList getItemAttributeList()
			throws RemoteException {
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getDataset().getItemAttributes(); 
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			attributeList = null;
			
			LogUtil.error("Service fail to get item attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}
	
	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId)
			throws RemoteException {
		
		ExternalRecord externalRecord = null;
		
		trans.lockRead();
		try {
			externalRecord = getDataset().getItemExternalRecord(itemId);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			externalRecord = null;
			
			LogUtil.error("Service fail to item external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return externalRecord;
	}

	
	@Override
	public NominalList getNominal(String unitName, String attribute) {
		NominalList nominalList = null;
		
		trans.lockRead();
		try {
			nominalList = getProvider().getNominalList(unitName, attribute);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			nominalList = null;
			
			LogUtil.error("Service fail to get nominal, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return nominalList;
	}


	@Override
	public boolean updateNominal(String unitName, String attribute, Nominal nominal)
			throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateNominal(unitName, attribute, nominal);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update nominal, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean deleteNominal(String unitName, String attribute) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteNominal(unitName, attribute);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete nominal, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public ExternalRecord getExternalRecord(InternalRecord internalRecord) throws RemoteException {
		
		ExternalRecord externalRecord = null;
		
		trans.lockRead();
		try {
			InterchangeAttributeMap attributeMap = getProvider().getAttributeMap(internalRecord);
			if (attributeMap != null && attributeMap.isValid())
				externalRecord = attributeMap.externalRecord;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			externalRecord = null;
			
			LogUtil.error("Service fail to get external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return externalRecord;
	}


	@Override
	public boolean updateExternalRecord(InternalRecord internalRecord,
			ExternalRecord externalRecord) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateAttributeMap(
					new InterchangeAttributeMap(internalRecord, externalRecord));
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}


	@Override
	public boolean deleteExternalRecord(InternalRecord internalRecord) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteAttributeMap(internalRecord);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean validateAccount(String account, String password, int privileges) 
			throws RemoteException {
		Provider provider = getProvider();
		if (provider == null) return true;

		boolean validated = provider.validateAccount(account, password, privileges);
		if (validated)
			return true;
		else if (account.equals("admin")) {
			String pwd = Util.getHudupProperty("admin");
			if (pwd == null)
				return false;
			else
				return password.equals(pwd);
		}
		else
			return false;
	}

	
	@Override
	public Profile getSampleProfile(Profile condition) throws RemoteException {
		Profile profile = null;
		
		trans.lockRead();
		try {
			profile = getProvider().getProfile(getDataset().getConfig().getSampleUnit(), condition);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			profile = null;
			
			LogUtil.error("Service fail to get profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return profile;
	}
	
	
	@Override
	public AttributeList getSampleProfileAttributeList()
			throws RemoteException {
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getProvider().getProfileAttributes(getDataset().getConfig().getSampleUnit());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			attributeList = null;
			
			LogUtil.error("Service fail to get profile attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}

	
	@Override
	public boolean updateSampleProfile(Profile profile)  
			throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateProfile(getDataset().getConfig().getSampleUnit(), profile);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
		
	}
	
	
	@Override
	public boolean deleteSampleProfile(Profile condition) 
			throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteProfile(getDataset().getConfig().getSampleUnit(), condition);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public Profile getProfile(String profileUnit, Profile condition) throws RemoteException {
		Profile profile = null;
		if (profileUnit.equals(getDataset().getConfig().getAccountUnit()))
			return profile;
		
		trans.lockRead();
		try {
			profile = getProvider().getProfile(profileUnit, condition);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			profile = null;
			
			LogUtil.error("Service fail to get profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return profile;
	}
	
	
	@Override
	public AttributeList getProfileAttributeList(String unitName)
			throws RemoteException {
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getProvider().getProfileAttributes(unitName);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			attributeList = null;
			
			LogUtil.error("Service fail to get profile attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}

	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile)  
			throws RemoteException {
		
		boolean result = false;
		if (profileUnit.equals(getDataset().getConfig().getAccountUnit()))
			return result;
		
		trans.lockWrite();
		try {
			result = getProvider().updateProfile(profileUnit, profile);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to update profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
		
	}
	
	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) 
			throws RemoteException {
		
		boolean result = false;
		if (profileUnit.equals(getDataset().getConfig().getAccountUnit()))
			return result;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteProfile(profileUnit, condition);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			result = false;
			
			LogUtil.error("Service fail to delete profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public DataConfig getServerConfig() throws RemoteException {
		
		DataConfig serverConfig = null;
		
		trans.lockRead();
		try {
			serverConfig = new DataConfig();
			serverConfig.setMetadata(getDataset().getConfig().getMetadata());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			serverConfig = null;
			
			LogUtil.error("Service fail to get server configuration, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return serverConfig;
	}

	
	@Override
	public Snapshot getSnapshot() throws RemoteException {
		Snapshot snapshot = null;
		
		trans.lockRead();
		try {
			DataConfig config = new DataConfig();
			Snapshot temp = (Snapshot) getDataset().catchup();
			config.setMetadata(temp.getConfig().getMetadata());
			
			snapshot = new SnapshotImpl();
			snapshot.assign(temp);
			snapshot.setConfig(config);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			snapshot = null;
			
			LogUtil.error("Service fail to get snapshot, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return snapshot;
	}


	@Override
	public Evaluator getEvaluator(String evaluatorName, String account, String password) throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return null;
		else
			return getEvaluator(evaluatorName);
	}


	/**
	 * Getting evaluator with specified evaluator name.
	 * @param evaluatorName specified evaluator name.
	 * @return evaluator with specified evaluator name.
	 * @throws RemoteException if any error raises.
	 */
	public Evaluator getEvaluator(String evaluatorName) throws RemoteException {
		Evaluator evaluator = null;
		trans.lockRead();
		try {
			List<Evaluator> evList = Util.getPluginManager().loadInstances(Evaluator.class);
			for (Evaluator ev : evList) {
				if (ev.getName().equals(evaluatorName)) {
					evaluator = ev;
					break;
				}
			}
			
			for (Evaluator ev : evList) {
				if (ev != evaluator) {
					try {
						ev.close();
					} catch (Exception e) {LogUtil.trace(e);}
				}
			}
			
			if (evaluator != null) {
				evaluator.export(serverConfig.getServerPort());
				evaluator.stimulate();
				
				synchronized (evaluatorConfigMap) {
					evaluatorConfigMap.put(evaluator.getName(), evaluator.getConfig());
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			evaluator = null;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return evaluator;
	}
	
	
	@Override
	public String[] getEvaluatorNames() throws RemoteException {
		List<String> evaluatorNames = Util.newList();
		
		trans.lockRead();
		try {
			List<Evaluator> evList = Util.getPluginManager().loadInstances(Evaluator.class);
			for (Evaluator ev : evList) {
				evaluatorNames.add(ev.getName());
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		Collections.sort(evaluatorNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
			
		});
		return evaluatorNames.toArray(new String[0]);
	}


	/*
	 * In current implementation, only normal algorithms are concerned.
	 * @see net.hudup.core.client.Service#getAlg(java.lang.String)
	 */
	@NextUpdate
	@Override
	public Alg getAlg(String algName) throws RemoteException {
		Alg alg = null;
		
		trans.lockRead();
		try {
			alg = PluginStorage.getNormalAlgReg().query(algName);
			if (alg instanceof AlgRemote) {
				AlgRemote remoteAlg = (AlgRemote)alg;
				
//				boolean singleton = remoteAlg instanceof SingletonExport;
//				if (!singleton)
//					remoteAlg = (AlgRemote) alg.newInstance();
				
				synchronized (remoteAlg) {
					remoteAlg.export(serverConfig.getServerPort());
				}
				alg = Util.getPluginManager().wrap(remoteAlg, false);
				
//				if (!singleton)
//					ExtraStorage.addUnmanagedExportedObject(remoteAlg);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			alg = null;
			
			LogUtil.error("Service fails to get algorithm, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return alg;
	}


	/*
	 * In current implementation, only normal algorithms are concerned.
	 * @see net.hudup.core.client.Service#getAlgNames()
	 */
	@Override
	public String[] getAlgNames() throws RemoteException {
		List<String> algNames = Util.newList();
		
		trans.lockRead();
		try {
			algNames = PluginStorage.getNormalAlgReg().getAlgNames();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Service fail to get algorithm names, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		Collections.sort(algNames);
		return algNames.toArray(new String[0]);
	}

	
	/*
	 * In current implementation, only normal algorithms are concerned.
	 * @see net.hudup.core.client.Service#getAlgDescs()
	 */
	@Override
	public AlgDesc2List getAlgDescs() throws RemoteException {
		AlgDesc2List algDescs = new AlgDesc2List();
		
		trans.lockRead();
		try {
			algDescs.addAll2(PluginStorage.getNormalAlgReg().getAlgList());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Service fail to get list of extended algorithm descriptions, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		algDescs.sort(new Comparator<AlgDesc2>() {

			@Override
			public int compare(AlgDesc2 o1, AlgDesc2 o2) {
				return o1.algName.compareTo(o2.algName);
			}
			
		});
		return algDescs;
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
}
