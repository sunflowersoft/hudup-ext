/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.EventListenerList2;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

/**
 * The class is a wrapper of remote algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote algorithm) and REM wrapper (normal algorithm).
 * The server creates REM stub (remote algorithm) and the client creates and uses the REM wrapper as normal algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class AlgRemoteWrapper implements Alg, AlgRemote, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


    /**
     * Exported algorithm must be serializable.
     */
    protected AlgRemote exportedStub = null;

	
	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote algorithm. It must be serializable.
	 */
	protected AlgRemote remoteAlg = null;
	
	
	/**
	 * Holding a list of listeners.
	 * 
	 */
    protected EventListenerList2 listenerList = new EventListenerList2();
    

    /**
     * Algorithm name.
     */
    private String name = null;
    
    
    /**
     * Configuration.
     */
    private transient DataConfig config = null;
    
    
	/**
	 * Constructor with specified remote algorithm.
	 * @param remoteAlg remote algorithm.
	 */
	public AlgRemoteWrapper(AlgRemote remoteAlg) {
		this(remoteAlg, true);
	}

	
	/**
	 * Constructor with specified remote algorithm and exclusive mode.
	 * @param remoteAlg remote algorithm.
	 * @param exclusive exclusive mode.
	 */
	public AlgRemoteWrapper(AlgRemote remoteAlg, boolean exclusive) {
		this.remoteAlg = remoteAlg;
		this.exclusive = exclusive;
		
		try {
			this.name = queryName();
		} catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	public Object learnStart(Object... info) throws RemoteException {
		return remoteAlg.learnStart(info);
	}


	@Override
	public boolean learnPause() throws RemoteException {
		return remoteAlg.learnPause();
	}


	@Override
	public boolean learnResume() throws RemoteException {
		return remoteAlg.learnResume();
	}


	@Override
	public boolean learnStop() throws RemoteException {
		return remoteAlg.learnStop();
	}


	@Override
	public boolean learnForceStop() throws RemoteException {
		return remoteAlg.learnForceStop();
	}


	@Override
	public boolean isLearnStarted() throws RemoteException {
		return remoteAlg.isLearnStarted();
	}


	@Override
	public boolean isLearnPaused() throws RemoteException {
		return remoteAlg.isLearnPaused();
	}


	@Override
	public boolean isLearnRunning() throws RemoteException {
		return remoteAlg.isLearnRunning();
	}


	/**
	 * Getting exclusive mode.
	 * @return exclusive mode.
	 */
	public boolean isExclusive() {
		return exclusive;
	}
	
	
	/**
	 * Setting exclusive mode.
	 * @param exclusive exclusive mode.
	 */
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
	
	
	@Override
	public String queryName() throws RemoteException {
		return name = remoteAlg.queryName();
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {AlgRemote.class.getName()};
	}
	
	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		try {
			config = remoteAlg.queryConfig();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return config;
	}

	
	@Override
	public void putConfig(DataConfig config) throws RemoteException {
		try {
			remoteAlg.putConfig(config);
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public DataConfig getConfig() {
		try {
			if (remoteAlg instanceof Alg)
				config = ((Alg)remoteAlg).getConfig();
			else if (config == null)
				config = remoteAlg.queryConfig();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return config;
	}

	
	@Override
	public void resetConfig() {
		if (remoteAlg instanceof Alg)
			((Alg)remoteAlg).resetConfig();
		else
			LogUtil.error("resetConfig() not supported");
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		if (remoteAlg instanceof Alg)
			return ((Alg)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("createDefaultConfig() not supported");
			return null;
		}
	}

	
	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return remoteAlg.getDescription();
	}

	
	@Override
	public Alg newInstance() {
		if (remoteAlg instanceof AlgAbstract) {
			try {
				AlgAbstract newAlg = (AlgAbstract) ((AlgAbstract)remoteAlg).newInstance();
				Constructor<?>[] constructors = getClass().getDeclaredConstructors();
				for (Constructor<?> constructor : constructors) {
					Class<?>[] types = constructor.getParameterTypes();
					if (types.length == 2 &&
							AlgRemote.class.isAssignableFrom(types[0]) &&
							(boolean.class.isAssignableFrom(types[1]) || Boolean.class.isAssignableFrom(types[1]))) {
						return (Alg)constructor.newInstance(newAlg, exclusive);
					}
				}
				
			} catch (Exception e) {LogUtil.trace(e);}
			
			return null;
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		remoteAlg.addSetupListener(listener);
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		remoteAlg.removeSetupListener(listener);
	}


	@Override
	public void fireSetupEvent(SetupAlgEvent evt) throws RemoteException {
		remoteAlg.fireSetupEvent(evt);
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		remoteAlg.receivedSetup(evt);
	}


	@Override
	public boolean classPathContains(String className) throws RemoteException {
    	try {
    		Util.getPluginManager().loadClass(className, false);
    		return true;
    	} catch (Exception e) {}
    	
		return false;
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		//Remote wrapper can export itself because this function is useful when the wrapper as remote algorithm can be called remotely by remote evaluator via Evaluator.remoteStart method.
		if (exportedStub == null)
			exportedStub = (AlgRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exclusive && remoteAlg != null) {
			try {
				//if (!remoteAlg.isAgent())
					remoteAlg.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteAlg = null;
		
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		if (remoteAlg != null) {
			try {
				//if (!remoteAlg.isAgent())
					remoteAlg.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteAlg = null;
		
		unexport();
	}


	/**
	 * Getting remote algorithm.
	 * @return remote algorithm.
	 */
	public AlgRemote getRemoteAlg() {
		return remoteAlg;
	}

	
	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
//	@Override
//	public boolean isAgent() throws RemoteException {
//		return remoteAlg.isAgent();
//	}
//
//
//	@Override
//	public void setAgent(boolean isAgent) throws RemoteException {
//		remoteAlg.setAgent(isAgent);
//	}


	@Override
	public String toString() {
		return DSUtil.shortenVerbalName(getName());
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			unexport();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}


}
