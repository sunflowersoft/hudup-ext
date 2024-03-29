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

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;

/**
 * This interface represents remote dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface DatasetRemote extends DatasetRemoteTask, Remote {


	@Override
	DataConfig remoteGetConfig() throws RemoteException;
	
	
	@Override
	Fetcher<Integer> remoteFetchUserIds() throws RemoteException;


	@Override
	Collection<Integer> remoteFetchUserIds2() throws RemoteException;


	@Override
	int remoteGetUserId(Serializable externalUserId) throws RemoteException;
	
	
	@Override
	ExternalRecord remoteGetUserExternalRecord(int userId) throws RemoteException;
	
	
	@Override
	Fetcher<Integer> remoteFetchItemIds() throws RemoteException;

	
	@Override
	Collection<Integer> remoteFetchItemIds2() throws RemoteException;

	
	@Override
	int remoteGetItemId(Serializable externalItemId) throws RemoteException;
	
	
	@Override
	ExternalRecord remoteGetItemExternalRecord(int itemId) throws RemoteException;
	
	
	@Override
	Rating remoteGetRating(int userId, int itemId) throws RemoteException;
	
	
	@Override
	RatingVector remoteGetUserRating(int userId) throws RemoteException;
	
	
	@Override
	Fetcher<RatingVector> remoteFetchUserRatings() throws RemoteException;
	
	
	@Override
	Collection<RatingVector> remoteFetchUserRatings2() throws RemoteException;

	
	@Override
	RatingVector remoteGetItemRating(int itemId) throws RemoteException;
	
	
	@Override
	Fetcher<RatingVector> remoteFetchItemRatings() throws RemoteException;
	
	
	@Override
	Collection<RatingVector> remoteFetchItemRatings2() throws RemoteException;
	
	
	@Override
	RatingMatrix remoteCreateUserMatrix() throws RemoteException;
	
	
	@Override
	RatingMatrix remoteCreateItemMatrix() throws RemoteException;
	
	
	@Override
	Profile remoteGetUserProfile(int userId) throws RemoteException;

	
	@Override
	Fetcher<Profile> remoteFetchUserProfiles() throws RemoteException;
	
	
	@Override
	Collection<Profile> remoteFetchUserProfiles2() throws RemoteException;
	
	
	@Override
	AttributeList remoteGetUserAttributes() throws RemoteException;

	
	@Override
	Profile remoteGetItemProfile(int itemId) throws RemoteException;
	
	
	@Override
	Fetcher<Profile> remoteFetchItemProfiles() throws RemoteException;

	
	@Override
	Collection<Profile> remoteFetchItemProfiles2() throws RemoteException;

	
	@Override
	AttributeList remoteGetItemAttributes() throws RemoteException;

	
	@Override
	Profile remoteProfileOf(Context context) throws RemoteException;
	
	
	@Override
	Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException;

	
	@Override
	Fetcher<Profile> remoteFetchSample() throws RemoteException;
	
	
	@Override
	Collection<Profile> remoteFetchSample2() throws RemoteException;
	
	
	@Override
	Dataset remoteCatchup() throws RemoteException;
	
	
	@Override
	Dataset remoteSelectByContexts(ContextList contexts) throws RemoteException;
	
	
	@Override
	boolean remoteIsExclusive() throws RemoteException;
	
	
	@Override
	ContextTemplateSchema remoteGetCTSchema() throws RemoteException;
	
	
	@Override
    void remoteClear() throws RemoteException;


	@Override
    Remote export(int serverPort) throws RemoteException;
    
    
	@Override
    void unexport() throws RemoteException;


	@Override
    void forceUnexport() throws RemoteException;

    
	@Override
	Remote getExportedStub() throws RemoteException;


	/**
	 * Getting base remote interface names.
	 * @return base remote interface names.
	 * @throws RemoteException if any error raises.
	 */
	String[] getBaseRemoteInterfaceNames() throws RemoteException;


}
