/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Map;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.Pointer;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;

/**
 * This class implements partially the AUgorithm {@link Aug}.
 * 
 * @author Loc Nguyen
 * @version 12
 *
 */
@NextUpdate
public abstract class AugAbstract extends ExecutableAlgAbstract implements Aug, AugRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal {@code KBase} of this model-based recommender.
	 */
	protected KBase kb = null;
	

	/**
	 * Default constructor.
	 */
	public AugAbstract() {
		// TODO Auto-generated constructor stub
		try {
			if (kb == null)
				kb = newKB();
			kb.setConfig(config); //This code line is important.
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	@SuppressWarnings("unchecked")
	@Override
	public synchronized void setup(Dataset dataset, Object...info) throws RemoteException {
		// TODO Auto-generated method stub
		unsetup();
		this.dataset = dataset;
		
		if (dataset != null) {
			if (dataset instanceof KBasePointer) {
				xURI pointerStoreUri = dataset.getConfig().getStoreUri();
				if (pointerStoreUri != null && !pointerStoreUri.equals(kb.getConfig().getStoreUri())) {
					kb.getConfig().setStoreUri(pointerStoreUri); //Also affect store URI of algorithm.
				}
				kb.load();
				
				dataset.getConfig().putAll(kb.getConfig());
				
				if (kb.getDatasource() != null) {
					this.dataset = kb.getDatasource().getDataset();
					this.dataset = this.dataset == null ? dataset : this.dataset;
					dataset = this.dataset;
				}
			}
			else if (!(dataset instanceof Pointer)) {
				kb.learn(dataset, this);
				kb.save();
			}
		}
		
		if (info != null && info.length > 0 && (info[0] instanceof Fetcher<?>))
			this.sample = (Fetcher<Profile>)info[0];
		else
			this.sample = dataset.fetchSample();
		
		learn();
		
		SetupAlgEvent evt = new SetupAlgEvent(
				this,
				Type.done,
				this.getName(),
				dataset,
				"Learned models: " + this.getDescription());
		fireSetupEvent(evt);
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		
		if (kb != null) {
			try {
				kb.close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
	}


	@Override
	public KBase createKBase(Dataset dataset) throws RemoteException {
		KBase kb = newKB();
		kb.setConfig((DataConfig)config.clone());
		
		if (dataset instanceof KBasePointer) {
			xURI pointerStoreUri = dataset.getConfig().getStoreUri();
			if (pointerStoreUri != null && !pointerStoreUri.equals(kb.getConfig().getStoreUri())) {
				kb.getConfig().setStoreUri(pointerStoreUri); //Also affect store URI of algorithm.
			}

			kb.load();
		}
		else if (!(dataset instanceof Pointer))
			kb.learn(dataset, this);
		
		return kb;
	}
	
	
	@Override
	public Dataset getDataset() throws RemoteException {
		// TODO Auto-generated method stub
		return dataset;
	}


	@Override
	public Object cacheTask(int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		return SupportCacheAlg.cacheTask(this, id1, id2, cache, task, params);
	}

	
	@Override
	public Object cacheTask(int id, Map<Integer, Object> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		return SupportCacheAlg.cacheTask(this, id, cache, task, params);
	}


	@Override
	public boolean isCached() {
		// TODO Auto-generated method stub
		return getConfig().getAsBoolean(SupportCacheAlg.SUPPORT_CACHE_FIELD);
	}

	
	@Override
	public void setCached(boolean cached) {
		// TODO Auto-generated method stub
		getConfig().put(SupportCacheAlg.SUPPORT_CACHE_FIELD, cached);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {AugRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		boolean fixedStore = false;
		try {
			String fixedText = Util.getHudupProperty("kb_fixedstore");
			if (fixedText != null && !fixedText.isEmpty())
				fixedStore = Boolean.parseBoolean(fixedText);
		}
		catch (Exception e) {
			fixedStore= false;
		}
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		if (!fixedStore)
			store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName()).concat("" + new java.util.Date().getTime());
		config.setStoreUri(store);
		
		config.put(SUPPORT_CACHE_FIELD, SUPPORT_CACHE_DEFAULT);

		return config;
	}


}
