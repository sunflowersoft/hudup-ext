/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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
		// TODO Auto-generated method stub
		return exclusive;
	}


	@Override
	public void setExclusive(boolean exclusive) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		// TODO Auto-generated method stub
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {DatasetRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
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
		// TODO Auto-generated method stub
		return getConfig();
	}


	@Override
	public Fetcher<Integer> remoteFetchUserIds() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchUserIds();
	}


	@Override
	public int remoteGetUserId(Serializable externalUserId) throws RemoteException {
		// TODO Auto-generated method stub
		return getUserId(externalUserId);
	}


	@Override
	public ExternalRecord remoteGetUserExternalRecord(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		return getUserExternalRecord(userId);
	}


	@Override
	public Fetcher<Integer> remoteFetchItemIds() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchItemIds();
	}


	@Override
	public int remoteGetItemId(Serializable externalItemId) throws RemoteException {
		// TODO Auto-generated method stub
		return getItemId(externalItemId);
	}


	@Override
	public ExternalRecord remoteGetItemExternalRecord(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return getItemExternalRecord(itemId);
	}


	@Override
	public Rating remoteGetRating(int userId, int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return getRating(userId, itemId);
	}


	@Override
	public RatingVector remoteGetUserRating(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		return getUserRating(userId);
	}


	@Override
	public Fetcher<RatingVector> remoteFetchUserRatings() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchUserRatings();
	}


	@Override
	public RatingVector remoteGetItemRating(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return getItemRating(itemId);
	}


	@Override
	public Fetcher<RatingVector> remoteFetchItemRatings() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchItemRatings();
	}


	@Override
	public RatingMatrix remoteCreateUserMatrix() throws RemoteException {
		// TODO Auto-generated method stub
		return createUserMatrix();
	}


	@Override
	public RatingMatrix remoteCreateItemMatrix() throws RemoteException {
		// TODO Auto-generated method stub
		return createItemMatrix();
	}


	@Override
	public Profile remoteGetUserProfile(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		return getUserProfile(userId);
	}


	@Override
	public Fetcher<Profile> remoteFetchUserProfiles() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchUserProfiles();
	}


	@Override
	public AttributeList remoteGetUserAttributes() throws RemoteException {
		// TODO Auto-generated method stub
		return getUserAttributes();
	}


	@Override
	public Profile remoteGetItemProfile(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return getItemProfile(itemId);
	}


	@Override
	public Fetcher<Profile> remoteFetchItemProfiles() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchItemProfiles();
	}


	@Override
	public AttributeList remoteGetItemAttributes() throws RemoteException {
		// TODO Auto-generated method stub
		return getItemAttributes();
	}


	@Override
	public Profile remoteProfileOf(Context context) throws RemoteException {
		// TODO Auto-generated method stub
		return profileOf(context);
	}


	@Override
	public Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException {
		// TODO Auto-generated method stub
		return profilesOf(ctxTemplateId);
	}


	@Override
	public Fetcher<Profile> remoteFetchSample() throws RemoteException {
		// TODO Auto-generated method stub
		return fetchSample();
	}


	@Override
	public Dataset remoteCatchup() throws RemoteException {
		// TODO Auto-generated method stub
		return catchup();
	}


	@Override
	public Dataset remoteSelectByContexts(ContextList contexts) throws RemoteException {
		// TODO Auto-generated method stub
		return selectByContexts(contexts);
	}


	@Override
	public boolean remoteIsExclusive() throws RemoteException {
		// TODO Auto-generated method stub
		return isExclusive();
	}


	@Override
	public ContextTemplateSchema remoteGetCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		return getCTSchema();
	}


	@Override
	public void remoteClear() throws RemoteException {
		// TODO Auto-generated method stub
		clear();
	}


}
