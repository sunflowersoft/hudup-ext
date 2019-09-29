/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.event.EventListenerList;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.Inspector;
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
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
	 * Default constructor.
	 */
	protected KBaseAbstract() {
		
	}
	
	
	@Override
	public void load() throws RemoteException {
		// TODO Auto-generated method stub
		
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
	public void learn(Dataset dataset, Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		config.setMetadata(dataset.getConfig().getMetadata());
		config.put(KBASE_NAME, getName());
		
		config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		config.addReadOnly(DataConfig.MAX_RATING_FIELD);
		config.addReadOnly(KBASE_NAME);
		
		datasource.close();
		if (dataset instanceof Pointer)
			return;
		
		datasource.connect(dataset);
		if (datasource.isValid())
			config.put(DATASOURCE_URI, datasource.getUri().toString());
		else
			datasource.close();
			
	}


	@Override
	public void save() throws RemoteException {
		save(config);
	}
	
	
	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		// TODO Auto-generated method stub

		UriAdapter adapter = new UriAdapter(storeConfig);

		xURI store = storeConfig.getStoreUri();
		adapter.clearContent(store, new UriFilter() {
			
			@Override
			public boolean accept(xURI uri) {
				// TODO Auto-generated method stub
				return uri.getLastName().startsWith(getName());
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "No description";
			}
			
			
		});
		adapter.create(store, true);
		adapter.close();
		
		xURI cfgUri = store.concat(KBASE_CONFIG);
		config.save(cfgUri);
		
	}


	@Override
	public void clear() throws RemoteException {
		// TODO Auto-generated method stub
		
		xURI store = config.getStoreUri();
		if (store != null) {
			UriAdapter adapter = new UriAdapter(config);
			adapter.clearContent(store, new UriFilter() {
				
				@Override
				public boolean accept(xURI uri) {
					// TODO Auto-generated method stub
					return uri.getLastName().startsWith(getName());
				}
				
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "No description";
				}
				
			});
			adapter.close();
		}
		
		try {
			close();
		} catch (Throwable e) {e.printStackTrace();}
	}


	/*
	 * This close method does not call unexport method as usual. This is a special case.
	 * @see net.hudup.core.alg.KBaseRemoteTask#close()
	 */
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return getName();
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return getName();
	}


	@Override
	public DataConfig getConfig() {
		
		return config;
	}
	
	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return getConfig();
	}


	@Override
	public void setConfig(DataConfig config) {
		this.config = config;
	}


	@Override
	public Datasource getDatasource() {
		// TODO Auto-generated method stub
		return datasource;
	}


	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		return new Inspector.NullInspector();
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
	}


	/**
	 * Getting an array of listeners for this EM.
	 * @return array of listeners for this EM.
	 */
	protected SetupAlgListener[] getSetupListeners() {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
	}


	@Override
	public void fireSetupEvent(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		SetupAlgListener[] listeners = getSetupListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		fireSetupEvent(evt);
	}

	
	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			if (!isEmpty()) close();
		} catch (Throwable e) {e.printStackTrace();}
		
		try {
			unexport();;
		} catch (Throwable e) {e.printStackTrace();}
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
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return (kbaseName != null && !kbaseName.isEmpty()) ? kbaseName : "empty_kbase";
			}
			
		};
	}

	
}
