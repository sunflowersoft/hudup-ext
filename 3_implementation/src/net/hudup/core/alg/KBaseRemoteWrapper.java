package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

/**
 * This class is wrapper of remote knowledge base.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class KBaseRemoteWrapper implements KBase, KBaseRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote knowledge base. It must be serializable.
	 */
	protected KBaseRemote remoteKBase = null;
	
	
    /**
     * Exported knowledge base must be serializable.
     */
    protected KBaseRemote exportedStub = null;

    
    /**
     * Default constructor.
     */
    public KBaseRemoteWrapper() {

    }

    
    /**
	 * Constructor with specified remote knowledge base.
	 * @param remoteKBase remote knowledge base.
	 */
	public KBaseRemoteWrapper(KBaseRemote remoteKBase) {
		this(remoteKBase, true);
	}

	
	/**
	 * Constructor with specified remote knowledge base and exclusive mode.
	 * @param remoteKBase remote knowledge base.
	 * @param exclusive exclusive mode.
	 */
	public KBaseRemoteWrapper(KBaseRemote remoteKBase, boolean exclusive) {
		this.remoteKBase = remoteKBase;
		this.exclusive = exclusive;
	}

	
	@Override
	public void load() throws RemoteException {
		remoteKBase.load();
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) throws RemoteException {
		remoteKBase.learn(dataset, alg);
	}

	
	@Override
	public void save() throws RemoteException {
		remoteKBase.save();
	}

	
	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		remoteKBase.save(storeConfig);
	}

	
	@Override
	public void clear() throws RemoteException {
		remoteKBase.clear();
	}

	
	/*
	 * This close method does not call unexport method as usual. This is a special case because
	 * after close method is called, KBase becomes empty, which means that method {@link #isEmpty()} returns {@code true} but KBase can be re-learned by calling the method {@link #learn(Dataset, Alg)} again.
	 * @see net.hudup.core.alg.KBaseRemoteTask#close()
	 */
	@Override
	public void close() throws Exception {
		remoteKBase.close();
	}

	
	@Override
	public boolean isEmpty() throws RemoteException {
		return remoteKBase.isEmpty();
	}

	
	@Override
	public DataConfig getConfig() {
		try {
			return remoteKBase.queryConfig();
		} catch (Exception e) { LogUtil.trace(e); }
		
		return null;
	}

	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		return remoteKBase.queryConfig();
	}


	@Override
	public void setConfig(DataConfig config) {
		if (remoteKBase instanceof KBase)
			((KBase)remoteKBase).setConfig(config);
		else {
			LogUtil.info("KBase wrapper does not support method setConfig(DataConfig)");
		}
	}

	
	@Override
	public Datasource getDatasource() {
		if (remoteKBase instanceof KBase)
			return ((KBase)remoteKBase).getDatasource();
		else {
			LogUtil.info("KBase wrapper does not support method getDatasource()");
			return null;
		}
	}

	
	@Override
	public Inspector getInspector() {
		if (remoteKBase instanceof KBase)
			return ((KBase)remoteKBase).getInspector();
		else {
			return new Inspector.NullInspector();
		}
	}


	@Override
	public String getName() {
		try {
			return remoteKBase.queryName();
		} catch (Exception e) { LogUtil.trace(e); }
		
		return null;
	}

	
	@Override
	public String queryName() throws RemoteException {
		return remoteKBase.queryName();
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {KBase.class.getName()};
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return remoteKBase.getDescription();
	}


	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		remoteKBase.addSetupListener(listener);
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		remoteKBase.removeSetupListener(listener);
	}


	@Override
	public void fireSetupEvent(SetupAlgEvent evt) throws RemoteException {
		remoteKBase.fireSetupEvent(evt);
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		remoteKBase.receivedSetup(evt);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null)
			exportedStub = (KBaseRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	@Override
	public boolean classPathContains(String className) throws RemoteException {
    	try {
    		Util.getPluginManager().loadClass(className, false);
    		return true;
    	} catch (Exception e) {}
    	
		return false;
	}


	/*
	 * This unexport method is not called by close method as usual. This is a special case because
	 * after close method is called, KBase becomes empty, which means that method {@link #isEmpty()} returns {@code true} but KBase can be re-learned by calling the method {@link #learn(Dataset, Alg)} again.
	 * @see net.hudup.core.data.Exportable#unexport()
	 */
	@Override
	public synchronized void unexport() throws RemoteException {
		if (exclusive && remoteKBase != null) {
			try {
				//if (!remoteKBase.isAgent())
					remoteKBase.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteKBase = null;

		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		if (remoteKBase != null) {
			try {
				//if (!remoteKBase.isAgent())
					remoteKBase.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteKBase = null;

		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	/**
	 * Getting remote knowledge base.
	 * @return remote knowledge base.
	 */
	public KBaseRemote getRemoteKBase() {
		return remoteKBase;
	}

	
//	@Override
//	public boolean isAgent() throws RemoteException {
//		return remoteKBase.isAgent();
//	}
//
//
//	@Override
//	public void setAgent(boolean isAgent) throws RemoteException {
//		remoteKBase.setAgent(isAgent);
//	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
		} catch (Throwable e) {}
		
		try {
			if (!isEmpty()) close();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			unexport();;
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public Object learnStart(Object... info) throws RemoteException {
		return remoteKBase.learnStart(info);
	}


	@Override
	public boolean learnPause() throws RemoteException {
		return remoteKBase.learnPause();
	}


	@Override
	public boolean learnResume() throws RemoteException {
		return remoteKBase.learnResume();
	}


	@Override
	public boolean learnStop() throws RemoteException {
		return remoteKBase.learnStop();
	}


	@Override
	public boolean learnForceStop() throws RemoteException {
		return remoteKBase.learnForceStop();
	}


	@Override
	public boolean isLearnStarted() throws RemoteException {
		return remoteKBase.isLearnStarted();
	}


	@Override
	public boolean isLearnPaused() throws RemoteException {
		return remoteKBase.isLearnPaused();
	}


	@Override
	public boolean isLearnRunning() throws RemoteException {
		return remoteKBase.isLearnRunning();
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


}
