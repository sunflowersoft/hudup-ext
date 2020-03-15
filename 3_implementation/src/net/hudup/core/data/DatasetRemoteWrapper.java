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
 * This class is wrapper for remote dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class DatasetRemoteWrapper implements Dataset, DatasetRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Stub as remote dataset.
     */
    protected DatasetRemote exportedStub = null;

	
	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote dataset.
	 */
	protected DatasetRemote remoteDataset = null;
	
	
	/**
	 * Constructor with specified remote dataset.
	 * @param remoteDataset remote dataset.
	 */
	public DatasetRemoteWrapper(DatasetRemote remoteDataset) {
		// TODO Auto-generated constructor stub
		this(remoteDataset, true);
	}

	
	/**
	 * Constructor with specified remote dataset and exclusive mode.
	 * @param remoteDataset remote dataset.
	 * @param exclusive exclusive mode.
	 */
	public DatasetRemoteWrapper(DatasetRemote remoteDataset, boolean exclusive) {
		// TODO Auto-generated constructor stub
		this.remoteDataset = remoteDataset;
		this.exclusive = exclusive;
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetConfig();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return null;
	}

	
	@Override
	public void setConfig(DataConfig config) {
		// TODO Auto-generated method stub
		if (remoteDataset instanceof DatasetAbstract) {
			((DatasetAbstract)remoteDataset).setConfig(config);
		}
		else {
			LogUtil.warn("DatasetRemoteWrapper.setConfig() not supported");
		}
	}

	
	@Override
	public Fetcher<Integer> fetchUserIds() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchUserIds());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public int getUserId(Serializable externalUserId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetUserId(externalUserId);
		}
		catch (Exception e) {e.printStackTrace();}
		
		return -1;
	}

	
	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetUserExternalRecord(userId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Fetcher<Integer> fetchItemIds() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchItemIds());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public int getItemId(Serializable externalItemId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetItemId(externalItemId);
		}
		catch (Exception e) {e.printStackTrace();}

		return -1;
	}

	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetItemExternalRecord(itemId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Rating getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetRating(userId, itemId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public RatingVector getUserRating(int userId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetUserRating(userId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchUserRatings());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public RatingVector getItemRating(int itemId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetItemRating(itemId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchItemRatings());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public RatingMatrix createUserMatrix() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteCreateUserMatrix();
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public RatingMatrix createItemMatrix() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteCreateItemMatrix();
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Profile getUserProfile(int userId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetUserProfile(userId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchUserProfiles());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public AttributeList getUserAttributes() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetUserAttributes();
		}
		catch (Exception e) {e.printStackTrace();}

		return new AttributeList();
	}

	
	@Override
	public Profile getItemProfile(int itemId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetItemProfile(itemId);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchItemProfiles());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public AttributeList getItemAttributes() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetItemAttributes();
		}
		catch (Exception e) {e.printStackTrace();}

		return new AttributeList();
	}

	
	@Override
	public Profile profileOf(Context context) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteProfileOf(context);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteProfilesOf(ctxTemplateId);
		}
		catch (Exception e) {e.printStackTrace();}

		return new MemProfiles();
	}

	
	@Override
	public Fetcher<Profile> fetchSample() {
		// TODO Auto-generated method stub
		try {
			return FetcherUtil.fixFetcherSerialized(remoteDataset.remoteFetchSample());
		}
		catch (Exception e) {e.printStackTrace();}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Object clone() {
		if (remoteDataset instanceof DatasetAbstract) {
			DatasetAbstract newDataset = (DatasetAbstract) ((DatasetAbstract)remoteDataset).clone();
			return new DatasetRemoteWrapper(newDataset, exclusive);
		}
		else {
			LogUtil.warn("DatasetRemoteWrapper.clone() returns itself");
			return this;
		}
	}
	
	
	@Override
	public Dataset catchup() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteCatchup();
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Dataset selectByContexts(ContextList contexts) {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteSelectByContexts(contexts);
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public boolean isExclusive() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteIsExclusive();
		}
		catch (Exception e) {e.printStackTrace();}

		return false;
	}

	
	@Override
	public void setExclusive(boolean exclusive) {
		// TODO Auto-generated method stub
		if (remoteDataset instanceof DatasetAbstract) {
			((DatasetAbstract)remoteDataset).setExclusive(exclusive);
		}
		else {
			LogUtil.warn("DatasetRemoteWrapper.setExclusive(boolean) not supported");
		}
	}

	
	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		try {
			return remoteDataset.remoteGetCTSchema();
		}
		catch (Exception e) {e.printStackTrace();}

		return null;
	}

	
	@Override
	public Provider getProvider() {
		// TODO Auto-generated method stub
		if (remoteDataset instanceof DatasetAbstract) {
			return ((DatasetAbstract)remoteDataset).getProvider();
		}
		else {
			LogUtil.warn("DatasetRemoteWrapper.getProvider() not supported");
			return null;
		}
	}

	
	/**
	 * Getting remote dataset.
	 * @return remote dataset.
	 */
	public DatasetRemote getRemoteDataset() {
		return (DatasetRemote)remoteDataset;
	}

	
	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		//Remote wrapper can export itself because this function is useful when the wrapper as remote algorithm can be called remotely by remote evaluator via Evaluator.remoteStart method.
		if (exportedStub == null)
			exportedStub = (DatasetRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	/**
	 * Exporting internal dataset.
	 * @param serverPort server port. Using port 0 if not concerning registry or naming..
	 * @return successfully stub object. Return null if failed.
	 * @throws RemoteException if any error raises.
	 */
	public synchronized Remote exportInside(int serverPort) throws RemoteException {
		if (remoteDataset == null)
			return null;
		else if (remoteDataset instanceof Exportable) {
			try {
				return ((Exportable)remoteDataset).export(serverPort);
			} catch (Exception e) {e.printStackTrace();}
			
			return null;
		}
		else
			return NetUtil.RegistryRemote.export(remoteDataset, serverPort);
	}

	
	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteDataset != null) {
			try {
				remoteDataset.unexport();
			} catch (Exception e) {e.printStackTrace();}
		}
		remoteDataset = null;
		
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (remoteDataset != null) {
			try {
				remoteDataset.unexport();
			} catch (Exception e) {e.printStackTrace();}
		}
		remoteDataset = null;

		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (remoteDataset != null && (remoteDataset instanceof DatasetAbstract)) {
			((DatasetAbstract)remoteDataset).clear();
		}
		
		try {
			unexport();
		}
		catch (Throwable e) {e.printStackTrace();}
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
			clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
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

	
}
