/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.EventListenerList2;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.xURI;

/**
 * This abstract implements partially {@link KBase} interface by adding a configuration {@link #config} to configure it and
 * a data source {@link #datasource} to point to where to locate the dataset from which this knowledge base is learned (trained).
 * Moreover, this class also implements methods {@link #load()} and {@link #save()} to load (read) and save (write) knowledge base.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class KBaseAbstract implements KBase, KBaseRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The configuration used to customize this knowledge base. The configuration is shared the same with algorithm.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * The data source points to where to locate the dataset from which this knowledge base is learned (trained).
	 * Please see {@link Datasource} for more details.
	 */
	protected Datasource datasource = new Datasource();
	
	
    /**
     * Exported knowledge base must be serializable.
     */
    protected KBaseRemote exportedStub = null;

    
	/**
	 * Holding a list of listeners.
	 * 
	 */
    protected EventListenerList2 listenerList = new EventListenerList2();

    
    /**
     * Flag to indicate whether algorithm learning process was started.
     */
    protected volatile boolean learnStarted = false;
    
    
    /**
     * Flag to indicate whether algorithm learning process was paused.
     */
    protected volatile boolean learnPaused = false;

    
    /**
	 * Default constructor.
	 */
	protected KBaseAbstract() {
		
	}
	
	
	@Override
	public synchronized void load() throws RemoteException {
		xURI store = config.getStoreUri();
		xURI cfgUri = store.concat(KBASE_CONFIG);
		config.load(cfgUri);
		
		datasource.close();
		String datasourceUriText = config.getAsString(DATASOURCE_URI);
		if (datasourceUriText == null || datasourceUriText.isEmpty())
			return;
		xURI datasourceUri = xURI.create(datasourceUriText);
		if (datasourceUri != null)
			datasource.connect(datasourceUri);
		
	}


	@Override
	public synchronized void learn(Dataset dataset, Alg alg) throws RemoteException {
		config.setMetadata(dataset.getConfig().getMetadata());
		double threshold = dataset.getConfig().getAsReal(DataConfig.RELEVANT_RATING_FIELD);
		if (Util.isUsed(threshold)) config.put(DataConfig.RELEVANT_RATING_FIELD, threshold);
		config.put(KBASE_NAME, getName());
		
		config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		config.addReadOnly(DataConfig.MAX_RATING_FIELD);
		if (Util.isUsed(threshold)) config.addReadOnly(DataConfig.RELEVANT_RATING_FIELD);
		config.addReadOnly(KBASE_NAME);
		
		datasource.close();
		if (dataset instanceof Pointer)
			return;
		
		datasource.connect(dataset);
		if (datasource.isValid())
			config.put(DATASOURCE_URI, datasource.getUri().toString());
		else
			datasource.close();
			
		if (config.getAsBoolean(RecommenderAbstract.MINMAX_RATING_RECONFIG))
			RecommenderAbstract.reconfigMinMaxRating(config, dataset);
	}


	@Override
	public void save() throws RemoteException {
		save(config);
	}
	
	
	@Override
	public synchronized void save(DataConfig storeConfig) throws RemoteException {
		UriAdapter adapter = new UriAdapter(storeConfig);

		xURI store = storeConfig.getStoreUri();
		adapter.clearContent(store, new UriFilter() {
			
			@Override
			public boolean accept(xURI uri) {
				return uri.getLastName().startsWith(getName());
			}

			@Override
			public String getDescription() {
				return "No description";
			}
			
			
		});
		adapter.create(store, true);
		adapter.close();
		
		xURI cfgUri = store.concat(KBASE_CONFIG);
		config.save(cfgUri);
		
	}


	@Override
	public synchronized void clear() throws RemoteException {
		xURI store = config.getStoreUri();
		if (store != null) {
			UriAdapter adapter = new UriAdapter(config);
			adapter.clearContent(store, new UriFilter() {
				
				@Override
				public boolean accept(xURI uri) {
					return uri.getLastName().startsWith(getName());
				}
				
				@Override
				public String getDescription() {
					return "No description";
				}
				
			});
			adapter.close();
		}
		
		try {
			close();
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	/*
	 * This close method does not call unexport method as usual. This is a special case.
	 * @see net.hudup.core.alg.KBaseRemoteTask#close()
	 */
	@Override
	public synchronized void close() throws Exception {
		datasource.close();
		
		config.setMetadata(null);
		config.remove(KBASE_NAME);
		config.remove(DATASOURCE_URI);
		
//		config.removeReadOnly(DataConfig.MIN_RATING_FIELD);
//		config.removeReadOnly(DataConfig.MAX_RATING_FIELD);
//		config.removeReadOnly(KBASE_NAME);
	}


	@Override
	public String queryName() throws RemoteException {
		return getName();
	}


	@Override
	public String getDescription() throws RemoteException {
		return getName();
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {KBase.class.getName()};
	}

	
	@Override
	public DataConfig getConfig() {
		return config;
	}
	
	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		return getConfig();
	}


	@Override
	public void putConfig(DataConfig config) throws RemoteException {
		this.config.putAll(config);
	}

	
	@Override
	public synchronized void setConfig(DataConfig config) {
		this.config = config;
	}


	@Override
	public Datasource getDatasource() {
		return datasource;
	}


	@Override
	public Inspector getInspector() {
		return new Inspector.NullInspector();
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
	}


	/**
	 * Getting an array of listeners for this EM.
	 * @return array of listeners for this EM.
	 */
	protected SetupAlgListener[] getSetupListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
	}


	@Override
	public void fireSetupEvent(SetupAlgEvent evt) throws RemoteException {
		SetupAlgListener[] listeners = getSetupListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		fireSetupEvent(evt);
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
		if (exportedStub == null)
			exportedStub = (KBaseRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	/*
	 * This unexport method is not called by close method as usual. This is a special case.
	 * @see net.hudup.core.data.Exportable#unexport()
	 */
	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
//	@Override
//	public boolean isAgent() throws RemoteException {
//		// TODO Auto-generated method stub
//		return config.getAsBoolean(DataConfig.AGENT_FIELD);
//	}
//
//
//	@Override
//	public void setAgent(boolean isAgent) throws RemoteException {
//		// TODO Auto-generated method stub
//		config.put(DataConfig.AGENT_FIELD, isAgent); //Should use variable instead.
//	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
		} catch (Throwable e) {}
		
		try {
			if (!isEmpty()) close();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
		
		try {
			unexport();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}


	@Override
	public Object learnStart(Object... info) throws RemoteException {
		if (isLearnStarted()) return null;
		
		learnStarted = true;
		
		while (learnStarted) {
			
			//Do something here.
			
			synchronized (this) {
				while (learnPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {LogUtil.trace(e);}
				}
			}
			
			learnStarted = false; //Pseudo-code to stop learning process.
		}
		
		synchronized (this) {
			learnStarted = true;
			learnPaused = false;
			
			notifyAll();
		}
		
		return null;
	}


	@Override
	public synchronized boolean learnPause() throws RemoteException {
		if (!isLearnRunning()) return false;
		
		learnPaused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return true;
	}


	@Override
	public synchronized boolean learnResume() throws RemoteException {
		if (!isLearnPaused()) return false;
		
		learnPaused = false;
		notifyAll();
		
		return true;
	}


	@Override
	public synchronized boolean learnStop() throws RemoteException {
		if (!isLearnStarted()) return false;
		
		learnStarted = false;
		
		if (learnPaused) {
			learnPaused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return true;
	}


	@Override
	public synchronized boolean learnForceStop() throws RemoteException {
		return learnStop();
	}


	@Override
	public boolean isLearnStarted() throws RemoteException {
		return learnStarted;
	}


	@Override
	public boolean isLearnPaused() throws RemoteException {
		return learnStarted && learnPaused;
	}


	@Override
	public boolean isLearnRunning() throws RemoteException {
		return learnStarted && !learnPaused;
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	/**
	 * Create empty knowledge base.
	 * @param kbaseName knowledge base name.
	 * @return empty knowledge base.
	 */
	public static KBase emptyKBase(String kbaseName) {
		return new KBaseAbstract() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEmpty() throws RemoteException {
				return true;
			}
			
			@Override
			public String getName() {
				return (kbaseName != null && !kbaseName.isEmpty()) ? kbaseName : "empty_kbase";
			}
			
		};
	}

	
}
