/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
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
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified remote context template manager and exclusive mode.
	 * @param remoteCTSManager specified remote context template manager.
	 * @param exclusive exclusive mode.
	 */
	public CTSManagerRemoteWrapper(CTSManagerRemote remoteCTSManager, boolean exclusive) {
		super(remoteCTSManager, exclusive);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void setup(DataConfig config) {
		// TODO Auto-generated method stub
		try {
			((CTSManagerRemote)remoteAlg).remoteSetup(config);
		} catch (Exception e) {e.printStackTrace();}
	}

	
	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteGetCTSchema();
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
	}

	
	@Override
	public boolean createContextTemplateUnit() {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCreateContextTemplateUnit();
		} catch (Exception e) {e.printStackTrace();}
		
		return false;
	}

	
	@Override
	public void reload() {
		// TODO Auto-generated method stub
		try {
			((CTSManagerRemote)remoteAlg).remoteReload();
		} catch (Exception e) {e.printStackTrace();}
	}

	
	@Override
	public Context createContext(int ctxTemplateId, Serializable assignedValue) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCreateContext(ctxTemplateId, assignedValue);
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
	}

	
	@Override
	public ContextList getContexts(int userId, int itemId) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteGetContexts(userId, itemId);
		} catch (Exception e) {e.printStackTrace();}
		
		return new ContextList();
	}

	
	@Override
	public Profile profileOf(Context context) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteProfileOf(context);
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
	}

	
	@Override
	public Profile profileOf(int ctxTemplateId, ContextValue ctxValue) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteProfileOf(ctxTemplateId, ctxValue);
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
	}

	
	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteProfilesOf(ctxTemplateId);
		} catch (Exception e) {e.printStackTrace();}
		
		return MemProfiles.createEmpty();
	}

	
	@Override
	public CTSMultiProfiles createCTSProfiles() {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCreateCTSProfiles();
		} catch (Exception e) {e.printStackTrace();}
		
		return CTSMemMultiProfiles.create();
	}

	
	@Override
	public boolean commitCTSchema() {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteCommitCTSchema();
		} catch (Exception e) {e.printStackTrace();}
		
		return false;
	}

	
	@Override
	public boolean importCTSchema(CTSManager ctsm) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(ctsm);
		} catch (Exception e) {e.printStackTrace();}
		
		return false;
	}

	
	@Override
	public boolean importCTSchema(Dataset dataset) {
		// TODO Auto-generated method stub
		try {
			return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(dataset);
		} catch (Exception e) {e.printStackTrace();}
		
		return false;
	}

	
	@Override
	public void defaultCTSchema() {
		// TODO Auto-generated method stub
		try {
			((CTSManagerRemote)remoteAlg).remoteDefaultCTSchema();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		return new Inspector.NullInspector();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof CTSManagerAbstract) {
			CTSManagerAbstract newCTSManager = (CTSManagerAbstract) ((CTSManagerAbstract)remoteAlg).newInstance();
			return new CTSManagerRemoteWrapper(newCTSManager, exclusive);
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof CTSManagerAbstract)
			return ((CTSManagerAbstract)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote CTS manager does not support createDefaultConfig()");
			return null;
		}
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (exclusive && (remoteAlg != null)) {
			try {
				((CTSManagerRemote)remoteAlg).close();
			} catch (Exception e) {e.printStackTrace();}
			remoteAlg = null;
		}
		
		try {
			unexport();
		}
		catch (Throwable e) {e.printStackTrace();}
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		try {
			close();
		}
		catch (Throwable e) {e.printStackTrace();}
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
		// TODO Auto-generated method stub
		((CTSManagerRemote)remoteAlg).remoteSetup(config);
	}


	@Override
	public ContextTemplateSchema remoteGetCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteGetCTSchema();
	}

	
	@Override
	public boolean remoteCreateContextTemplateUnit() throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteCreateContextTemplateUnit();
	}


	@Override
	public void remoteReload() throws RemoteException {
		// TODO Auto-generated method stub
		((CTSManagerRemote)remoteAlg).remoteReload();
	}

	
	@Override
	public Context remoteCreateContext(int ctxTemplateId, Serializable assignedValue) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteCreateContext(ctxTemplateId, assignedValue);
	}

	
	@Override
	public ContextList remoteGetContexts(int userId, int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteGetContexts(userId, itemId);
	}

	
	@Override
	public Profile remoteProfileOf(Context context) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteProfileOf(context);
	}

	
	@Override
	public Profile remoteProfileOf(int ctxTemplateId, ContextValue ctxValue) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteProfileOf(ctxTemplateId, ctxValue);
	}

	
	@Override
	public Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteProfilesOf(ctxTemplateId);
	}

	
	@Override
	public CTSMultiProfiles remoteCreateCTSProfiles() throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteCreateCTSProfiles();
	}


	@Override
	public boolean remoteCommitCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteCommitCTSchema();
	}


	@Override
	public boolean remoteImportCTSchema(CTSManager ctsm) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(ctsm);
	}


	@Override
	public boolean remoteImportCTSchema(Dataset dataset) throws RemoteException {
		// TODO Auto-generated method stub
		return ((CTSManagerRemote)remoteAlg).remoteImportCTSchema(dataset);
	}


	@Override
	public void remoteDefaultCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		((CTSManagerRemote)remoteAlg).remoteDefaultCTSchema();
	}

	
}
