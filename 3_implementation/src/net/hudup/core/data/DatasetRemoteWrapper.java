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
import java.util.Collection;

import net.hudup.core.Constants;
import net.hudup.core.Util;
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
     * Default constructor.
     */
    protected DatasetRemoteWrapper() {

    }

    
	/**
	 * Constructor with specified remote dataset.
	 * @param remoteDataset remote dataset.
	 */
	public DatasetRemoteWrapper(DatasetRemote remoteDataset) {
		this(remoteDataset, true);
	}

	
	/**
	 * Constructor with specified remote dataset and exclusive mode.
	 * @param remoteDataset remote dataset.
	 * @param exclusive exclusive mode.
	 */
	public DatasetRemoteWrapper(DatasetRemote remoteDataset, boolean exclusive) {
		this.remoteDataset = remoteDataset;
		this.exclusive = exclusive;
	}

	
	@Override
	public DataConfig getConfig() {
		try {
			return remoteDataset.remoteGetConfig();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	@Override
	public void setConfig(DataConfig config) {
		if (remoteDataset instanceof Dataset) {
			((Dataset)remoteDataset).setConfig(config);
		}
		else {
			LogUtil.warn("DatasetRemoteWrapper.setConfig() not supported");
		}
	}

	
	@Override
	public Fetcher<Integer> fetchUserIds() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchUserIds();
			else
				return remoteDataset.remoteFetchUserIds();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<Integer> fetchUserIds2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchUserIds2();
			else
				return remoteDataset.remoteFetchUserIds2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public int getUserId(Serializable externalUserId) {
		try {
			return remoteDataset.remoteGetUserId(externalUserId);
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return -1;
	}

	
	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		try {
			return remoteDataset.remoteGetUserExternalRecord(userId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Fetcher<Integer> fetchItemIds() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchItemIds();
			else
				return remoteDataset.remoteFetchItemIds();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<Integer> fetchItemIds2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchItemIds2();
			else
				return remoteDataset.remoteFetchItemIds2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public int getItemId(Serializable externalItemId) {
		try {
			return remoteDataset.remoteGetItemId(externalItemId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return -1;
	}

	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		try {
			return remoteDataset.remoteGetItemExternalRecord(itemId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Rating getRating(int userId, int itemId) {
		try {
			return remoteDataset.remoteGetRating(userId, itemId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public RatingVector getUserRating(int userId) {
		try {
			return remoteDataset.remoteGetUserRating(userId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchUserRatings();
			else
				return remoteDataset.remoteFetchUserRatings();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<RatingVector> fetchUserRatings2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchUserRatings2();
			else
				return remoteDataset.remoteFetchUserRatings2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public RatingVector getItemRating(int itemId) {
		try {
			return remoteDataset.remoteGetItemRating(itemId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchItemRatings();
			else
				return remoteDataset.remoteFetchItemRatings();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<RatingVector> fetchItemRatings2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchItemRatings2();
			else
				return remoteDataset.remoteFetchItemRatings2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public RatingMatrix createUserMatrix() {
		try {
			return remoteDataset.remoteCreateUserMatrix();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public RatingMatrix createItemMatrix() {
		try {
			return remoteDataset.remoteCreateItemMatrix();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Profile getUserProfile(int userId) {
		try {
			return remoteDataset.remoteGetUserProfile(userId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchUserProfiles();
			else
				return remoteDataset.remoteFetchUserProfiles();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<Profile> fetchUserProfiles2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchUserProfiles2();
			else
				return remoteDataset.remoteFetchUserProfiles2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public AttributeList getUserAttributes() {
		try {
			return remoteDataset.remoteGetUserAttributes();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return new AttributeList();
	}

	
	@Override
	public Profile getItemProfile(int itemId) {
		try {
			return remoteDataset.remoteGetItemProfile(itemId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchItemProfiles();
			else
				return remoteDataset.remoteFetchItemProfiles();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<Profile> fetchItemProfiles2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchItemProfiles2();
			else
				return remoteDataset.remoteFetchItemProfiles2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public AttributeList getItemAttributes() {
		try {
			return remoteDataset.remoteGetItemAttributes();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return new AttributeList();
	}

	
	@Override
	public Profile profileOf(Context context) {
		try {
			return remoteDataset.remoteProfileOf(context);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		try {
			return remoteDataset.remoteProfilesOf(ctxTemplateId);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return new MemProfiles();
	}

	
	@Override
	public Fetcher<Profile> fetchSample() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchSample();
			else
				return remoteDataset.remoteFetchSample();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return new MemFetcher<>();
	}

	
	@Override
	public Collection<Profile> fetchSample2() {
		try {
			if (remoteDataset instanceof Dataset)
				return ((Dataset)remoteDataset).fetchSample2();
			else
				return remoteDataset.remoteFetchSample2();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return Util.newList();
	}

	
	@Override
	public Object clone() {
		if (remoteDataset instanceof Dataset) {
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
		try {
			return remoteDataset.remoteCatchup();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Dataset selectByContexts(ContextList contexts) {
		try {
			return remoteDataset.remoteSelectByContexts(contexts);
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public boolean isExclusive() {
		try {
			return remoteDataset.remoteIsExclusive();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return false;
	}

	
	@Override
	public void setExclusive(boolean exclusive) {
		if (remoteDataset instanceof Dataset) {
			((Dataset)remoteDataset).setExclusive(exclusive);
		}
		else {
			LogUtil.warn("DatasetRemoteWrapper.setExclusive(boolean) not supported");
		}
	}

	
	@Override
	public ContextTemplateSchema getCTSchema() {
		try {
			return remoteDataset.remoteGetCTSchema();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return null;
	}

	
	@Override
	public Provider getProvider() {
		if (remoteDataset instanceof Dataset) {
			return ((Dataset)remoteDataset).getProvider();
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

	
	/**
	 * Testing whether this wrapper wraps a stub remote dataset.
	 * @return whether this wrapper wraps a stub remote dataset.
	 */
	protected boolean isRemote() {
		return ((remoteDataset != null) && !(remoteDataset instanceof Dataset));
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
			} catch (Exception e) {LogUtil.trace(e);}
			
			return null;
		}
		else
			return NetUtil.RegistryRemote.export(remoteDataset, serverPort);
	}

	
	@Override
	public synchronized void unexport() throws RemoteException {
		if (exclusive && remoteDataset != null) {
			try {
				remoteDataset.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteDataset = null;
		
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		if (remoteDataset != null) {
			try {
				remoteDataset.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
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
		if (exclusive && remoteDataset != null) {
			try {
				remoteDataset.remoteClear();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		try {
			unexport();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}

	
	/**
	 * Forcing clear.
	 */
	public void forceClear() {
		if (remoteDataset != null) {
			try {
				remoteDataset.remoteClear();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		try {
			forceUnexport();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {DatasetRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
//			clear(); //Clear method can close provider with scanner.
			unexport();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}


	@Override
	public DataConfig remoteGetConfig() throws RemoteException {
		return getConfig();
	}


	@Override
	public Fetcher<Integer> remoteFetchUserIds() throws RemoteException {
		return fetchUserIds();
	}


	@Override
	public Collection<Integer> remoteFetchUserIds2() throws RemoteException {
		return fetchUserIds2();
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
		return fetchItemIds();
	}


	@Override
	public Collection<Integer> remoteFetchItemIds2() throws RemoteException {
		return fetchItemIds2();
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
		return fetchUserRatings();
	}


	@Override
	public Collection<RatingVector> remoteFetchUserRatings2() throws RemoteException {
		return fetchUserRatings2();
	}


	@Override
	public RatingVector remoteGetItemRating(int itemId) throws RemoteException {
		return getItemRating(itemId);
	}


	@Override
	public Fetcher<RatingVector> remoteFetchItemRatings() throws RemoteException {
		return fetchItemRatings();
	}


	@Override
	public Collection<RatingVector> remoteFetchItemRatings2() throws RemoteException {
		return fetchItemRatings2();
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
		return fetchUserProfiles();
	}


	@Override
	public Collection<Profile> remoteFetchUserProfiles2() throws RemoteException {
		return fetchUserProfiles2();
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
		return fetchItemProfiles();
	}


	@Override
	public Collection<Profile> remoteFetchItemProfiles2() throws RemoteException {
		return fetchItemProfiles2();
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
		return fetchSample();
	}


	@Override
	public Collection<Profile> remoteFetchSample2() throws RemoteException {
		return fetchSample2();
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
