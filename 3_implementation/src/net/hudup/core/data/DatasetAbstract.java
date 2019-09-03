package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;
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
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Dataset.class);

	
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
    protected DatasetRemote stub = null;

    
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
			remoteUnexport();
		}
		catch (Throwable e) {e.printStackTrace();}
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


	/**
	 * Getting stub as remote dataset.
	 * @return stub as remote dataset.
	 */
	public DatasetRemote getStubDataset() {
		return (DatasetRemote)stub;
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


	@Override
	public synchronized DatasetRemote remoteExport(int serverPort) throws RemoteException {
		//Remote wrapper can export itself because this function is useful when the wrapper as remote algorithm can be called remotely by remote evaluator via Evaluator.remoteStart method.
		if (stub == null)
			stub = (DatasetRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return stub;
	}


	@Override
	public synchronized void remoteUnexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (stub != null) {
			NetUtil.RegistryRemote.unexport(this);
			stub = null;
		}
	}


}
