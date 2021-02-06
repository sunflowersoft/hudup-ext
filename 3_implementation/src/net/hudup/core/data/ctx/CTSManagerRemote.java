/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemote;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;

/**
 * This interface represents a remote context template schema (CTS) manager.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface CTSManagerRemote extends CTSManagerRemoteTask, AlgRemote {


	@Override
	void remoteSetup(DataConfig config) throws RemoteException;

	
	@Override
	ContextTemplateSchema remoteGetCTSchema() throws RemoteException;
	
	
	@Override
	boolean remoteCreateContextTemplateUnit() throws RemoteException;

	
	@Override
	public void remoteReload() throws RemoteException;
	
	
	@Override
	Context remoteCreateContext(
			int ctxTemplateId, 
			Serializable assignedValue) throws RemoteException;
	
	
	@Override
	ContextList remoteGetContexts(int userId, int itemId) throws RemoteException;

	
	@Override
	ContextList remoteGetContexts(int userId, int itemId, long ratedDate) throws RemoteException;

	
	@Override
	Profile remoteProfileOf(Context context) throws RemoteException;

	
	@Override
	Profile remoteProfileOf(int ctxTemplateId, ContextValue ctxValue) throws RemoteException;

	
	@Override
	Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException;
	
	
	@Override
	CTSMultiProfiles remoteCreateCTSProfiles() throws RemoteException;
	
	
	@Override
	boolean remoteCommitCTSchema() throws RemoteException;
	
	
	@Override
	boolean remoteImportCTSchema(CTSManager ctsm) throws RemoteException;
	
	
	@Override
	boolean remoteImportCTSchema(Dataset dataset) throws RemoteException;

	
	@Override
	void remoteDefaultCTSchema() throws RemoteException;


}
