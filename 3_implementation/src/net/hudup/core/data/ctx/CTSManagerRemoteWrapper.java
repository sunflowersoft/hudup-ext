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

import net.hudup.core.Constants;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.MemProfiles;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is wrapper of remote context template manager.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@BaseClass //This class is not base class but the base class annotation prevents the wrapper to be registered in plug-in storage.
public class CTSManagerRemoteWrapper extends AlgRemoteWrapper implements CTSManager, CTSManagerRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified remote context template manager.
	 * @param remoteCTSManager specified remote context template manager.
	 */
	public CTSManagerRemoteWrapper(CTSManagerRemote remoteCTSManager) {
		super(remoteCTSManager);
	}

	
	/**
	 * Constructor with specified remote context template manager and exclusive mode.
	 * @param remoteCTSManager specified remote context template manager.
	 * @param exclusive exclusive mode.
	 */
	public CTSManagerRemoteWrapper(CTSManagerRemote remoteCTSManager, boolean exclusive) {
		super(remoteCTSManager, exclusive);
	}

	
	@Override
	public void setup(DataConfig config) {
		try {
			((CTSManagerRemote)remoteAlg).remoteSetup(config);
		} catch (Exception e) {LogUtil.trace(e);}
	}

	
	@Override
	public ContextTemplateSchema getCTSchema() {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteGetCTSchema();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	@Override
	public boolean createContextTemplateUnit() {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCreateContextTemplateUnit();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}

	
	@Override
	public void reload() {
		try {
			((CTSManagerRemote)remoteAlg).remoteReload();
		} catch (Exception e) {LogUtil.trace(e);}
	}

	
	@Override
	public Context createContext(int ctxTemplateId, Serializable assignedValue) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCreateContext(ctxTemplateId, assignedValue);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	@Override
	public ContextList getContexts(int userId, int itemId) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteGetContexts(userId, itemId);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new ContextList();
	}

	
	@Override
	public ContextList getContexts(int userId, int itemId, long ratedDate) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteGetContexts(userId, itemId, ratedDate);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new ContextList();
	}

	
	@Override
	public Profile profileOf(Context context) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteProfileOf(context);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	@Override
	public Profile profileOf(int ctxTemplateId, ContextValue ctxValue) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteProfileOf(ctxTemplateId, ctxValue);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteProfilesOf(ctxTemplateId);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return MemProfiles.createEmpty();
	}

	
	@Override
	public CTSMultiProfiles createCTSProfiles() {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCreateCTSProfiles();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return CTSMemMultiProfiles.create();
	}

	
	@Override
	public boolean commitCTSchema() {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCommitCTSchema();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}

	
	@Override
	public boolean importCTSchema(CTSManager ctsm) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(ctsm);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}

	
	@Override
	public boolean importCTSchema(Dataset dataset) {
		try {
			return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(dataset);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}

	
	@Override
	public void defaultCTSchema() {
		try {
			((CTSManagerRemote)remoteAlg).remoteDefaultCTSchema();
		} catch (Exception e) {LogUtil.trace(e);}
	}
	
	
	@Override
	public Inspector getInspector() {
		return new Inspector.NullInspector();
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		if (remoteAlg instanceof CTSManagerAbstract)
			return ((CTSManagerAbstract)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote CTS manager does not support createDefaultConfig()");
			return null;
		}
	}

	
	@Override
	public void close() throws Exception {
		if (exclusive && (remoteAlg != null)) {
			try {
				((CTSManagerRemote)remoteAlg).close();
			} catch (Exception e) {LogUtil.trace(e);}
			remoteAlg = null;
		}
		
		try {
			unexport();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {CTSManagerRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	/**
	 * Getting exported context template schema manager.
	 * @return exported context template schema manager.
	 */
	public CTSManagerRemote getExportedCTSManager() {
		return (CTSManagerRemote)exportedStub;
	}

	
	@Override
	public void remoteSetup(DataConfig config) throws RemoteException {
		((CTSManagerRemote)remoteAlg).remoteSetup(config);
	}


	@Override
	public ContextTemplateSchema remoteGetCTSchema() throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteGetCTSchema();
	}

	
	@Override
	public boolean remoteCreateContextTemplateUnit() throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteCreateContextTemplateUnit();
	}


	@Override
	public void remoteReload() throws RemoteException {
		((CTSManagerRemote)remoteAlg).remoteReload();
	}

	
	@Override
	public Context remoteCreateContext(int ctxTemplateId, Serializable assignedValue) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteCreateContext(ctxTemplateId, assignedValue);
	}

	
	@Override
	public ContextList remoteGetContexts(int userId, int itemId) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteGetContexts(userId, itemId);
	}

	
	@Override
	public ContextList remoteGetContexts(int userId, int itemId, long ratedDate) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteGetContexts(userId, itemId, ratedDate);
	}

	
	@Override
	public Profile remoteProfileOf(Context context) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteProfileOf(context);
	}

	
	@Override
	public Profile remoteProfileOf(int ctxTemplateId, ContextValue ctxValue) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteProfileOf(ctxTemplateId, ctxValue);
	}

	
	@Override
	public Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteProfilesOf(ctxTemplateId);
	}

	
	@Override
	public CTSMultiProfiles remoteCreateCTSProfiles() throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteCreateCTSProfiles();
	}


	@Override
	public boolean remoteCommitCTSchema() throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteCommitCTSchema();
	}


	@Override
	public boolean remoteImportCTSchema(CTSManager ctsm) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(ctsm);
	}


	@Override
	public boolean remoteImportCTSchema(Dataset dataset) throws RemoteException {
		return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(dataset);
	}


	@Override
	public void remoteDefaultCTSchema() throws RemoteException {
		((CTSManagerRemote)remoteAlg).remoteDefaultCTSchema();
	}

	
}
