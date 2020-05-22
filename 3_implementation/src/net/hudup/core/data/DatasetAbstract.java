/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

/**
 * This abstract class implements partially {@link Dataset} interface.
 * It add two variables such as {@link #config} which is the configuration of dataset and {@link #exclusive} indicating whether this dataset is exclusive.
 * Note, if this dataset is in exclusive mode, it is clear (method {@link #clear()} is called) when method Recommender#unsetup() is called (Recommender algorithm is stopped).
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class DatasetAbstract implements Dataset, DatasetRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * In-memory field.
	 */
	public static final String ONLY_MEMORY_FIELD = "only_memory";
	
	
	/**
	 * In-memory default value.
	 */
	public static final boolean ONLY_MEMORY_DEFAULT = false;

	
	/**
	 * Hardware address MAC field.
	 */
	public static final String HARDWARE_ADDR_FIELD = "hardware_addr";

	
	/**
	 * Host address field field.
	 */
	public static final String HOST_ADDR_FIELD = "host_addr";

	
	/**
	 * Dataset identifier. This identifier is not the URI identifier. It is a number which is often assigned in evaluation process.
	 */
	public static final String DATASETID_FIELD = "$datasetid";

	
	/**
	 * The configuration of dataset.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Indicator of whether this dataset is exclusive.
	 */
	protected boolean exclusive = false;
	
	
    /**
     * Stub as remote dataset.
     */
    protected DatasetRemote exportedStub = null;

    
    @Override
	public DataConfig getConfig() {
		return config;
	}

	
	@Override
	public void setConfig(DataConfig config) {
		this.config = config;
	}

	
	@Override
	public void clear() {
		config = null;
		exclusive = false;
		
		try {
			unexport();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public abstract Object clone();


	@Override
	public boolean isExclusive() {
		return exclusive;
	}


	@Override
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null)
			exportedStub = (DatasetRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {DatasetRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
//			clear(); //Clear method can close provider with scanner.
			unexport();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


	@Override
	public DataConfig remoteGetConfig() throws RemoteException {
		return getConfig();
	}


	@Override
	public Fetcher<Integer> remoteFetchUserIds() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchUserIds());
	}


	@Override
	public int remoteGetUserId(Serializable externalUserId) throws RemoteException {
		return getUserId(externalUserId);
	}


	@Override
	public ExternalRecord remoteGetUserExternalRecord(int userId) throws RemoteException {
		return getUserExternalRecord(userId);
	}


	@Override
	public Fetcher<Integer> remoteFetchItemIds() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchItemIds());
	}


	@Override
	public int remoteGetItemId(Serializable externalItemId) throws RemoteException {
		return getItemId(externalItemId);
	}


	@Override
	public ExternalRecord remoteGetItemExternalRecord(int itemId) throws RemoteException {
		return getItemExternalRecord(itemId);
	}


	@Override
	public Rating remoteGetRating(int userId, int itemId) throws RemoteException {
		return getRating(userId, itemId);
	}


	@Override
	public RatingVector remoteGetUserRating(int userId) throws RemoteException {
		return getUserRating(userId);
	}


	@Override
	public Fetcher<RatingVector> remoteFetchUserRatings() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchUserRatings());
	}


	@Override
	public RatingVector remoteGetItemRating(int itemId) throws RemoteException {
		return getItemRating(itemId);
	}


	@Override
	public Fetcher<RatingVector> remoteFetchItemRatings() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchItemRatings());
	}


	@Override
	public RatingMatrix remoteCreateUserMatrix() throws RemoteException {
		return createUserMatrix();
	}


	@Override
	public RatingMatrix remoteCreateItemMatrix() throws RemoteException {
		return createItemMatrix();
	}


	@Override
	public Profile remoteGetUserProfile(int userId) throws RemoteException {
		return getUserProfile(userId);
	}


	@Override
	public Fetcher<Profile> remoteFetchUserProfiles() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchUserProfiles());
	}


	@Override
	public AttributeList remoteGetUserAttributes() throws RemoteException {
		return getUserAttributes();
	}


	@Override
	public Profile remoteGetItemProfile(int itemId) throws RemoteException {
		return getItemProfile(itemId);
	}


	@Override
	public Fetcher<Profile> remoteFetchItemProfiles() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchItemProfiles());
	}


	@Override
	public AttributeList remoteGetItemAttributes() throws RemoteException {
		return getItemAttributes();
	}


	@Override
	public Profile remoteProfileOf(Context context) throws RemoteException {
		return profileOf(context);
	}


	@Override
	public Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException {
		return profilesOf(ctxTemplateId);
	}


	@Override
	public Fetcher<Profile> remoteFetchSample() throws RemoteException {
		return FetcherUtil.fixFetcherSerialized(fetchSample());
	}


	@Override
	public Dataset remoteCatchup() throws RemoteException {
		return catchup();
	}


	@Override
	public Dataset remoteSelectByContexts(ContextList contexts) throws RemoteException {
		return selectByContexts(contexts);
	}


	@Override
	public boolean remoteIsExclusive() throws RemoteException {
		return isExclusive();
	}


	@Override
	public ContextTemplateSchema remoteGetCTSchema() throws RemoteException {
		return getCTSchema();
	}


	@Override
	public void remoteClear() throws RemoteException {
		clear();
	}


}
