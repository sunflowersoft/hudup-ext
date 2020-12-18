/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;


/**
 * This class implements basically the model-based recommendation algorithm represented by the interface {@code ModelBasedRecomender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ModelBasedRecommenderAbstract extends RecommenderAbstract implements ModelBasedRecommender, ModelBasedAlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal {@code KBase} of this model-based recommender.
	 * For example, if model-based recommender uses frequent pattern to make recommendation, the internal {@code KBase} contains such pattern.
	 */
	protected KBase kb = null;
	

	/**
	 * Default constructor, which always call {@link #newKB()} to create knowledge base.
	 */
	public ModelBasedRecommenderAbstract() {
		super();
		
		try {
			if (kb == null)
				kb = newKB();
			kb.setConfig(config); //This code line is important.
			
			//Loc Nguyen added: 2020.03.30
			if (kb instanceof KBaseRemote)
				((KBaseRemote)kb).addSetupListener(this);
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		unsetup();
		
		if (dataset instanceof KBasePointer) {
			xURI pointerStoreUri = dataset.getConfig().getStoreUri();
			if (pointerStoreUri != null && !pointerStoreUri.equals(kb.getConfig().getStoreUri())) {
				kb.getConfig().setStoreUri(pointerStoreUri); //Also affect store URI of algorithm.
			}
			kb.load();
			
			//This line is important because algorithm configuration is KBase configuration which in turn is loaded from dataset (pointed by KBasePointer).
			//Therefore, configuring dataset (pointed by KBasePointer) here is to configure algorithm.
			dataset.getConfig().putAll(kb.getConfig());
		}
		else if (!(dataset instanceof Pointer)) {
			kb.learn(dataset, this);
			kb.save();
		}
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		if (kb != null) {
			try {
				kb.close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
	}

	
	@Override
	public KBase getKBase() throws RemoteException {
		return kb;
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
		if (kb == null) return null;
		
		Datasource datasource = kb.getDatasource();
		if (datasource != null)
			return datasource.getDataset();
		else
			return null;
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {RecommenderRemote.class.getName(), ModelBasedAlgRemote.class.getName()};
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		boolean fixedStore = false;
		try {
			String fixedStoreText = Util.getHudupProperty("kb_fixedstore");
			if (fixedStoreText != null && !fixedStoreText.isEmpty())
				fixedStore = Boolean.parseBoolean(fixedStoreText);
		}
		catch (Exception e) {
			fixedStore= false;
		}
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		if (!fixedStore)
			store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName()).concat("" + new java.util.Date().getTime());
		config.setStoreUri(store);
		
		return config;
	}


	@Override
	public Object learnStart(Object... info) throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).learnStart(info);
		else
			return super.learnStart(info);
	}


	@Override
	public synchronized boolean learnPause() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).learnPause();
		else
			return super.learnPause();
	}


	@Override
	public synchronized boolean learnResume() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).learnResume();
		else
			return super.learnResume();
	}


	@Override
	public synchronized boolean learnStop() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).learnStop();
		else
			return super.learnStop();
	}


	@Override
	public boolean learnForceStop() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).learnForceStop();
		else
			return super.learnForceStop();
	}


	@Override
	public boolean isLearnStarted() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).isLearnStarted();
		else
			return super.isLearnStarted();
	}


	@Override
	public boolean isLearnPaused() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).isLearnPaused();
		else
			return super.isLearnPaused();
	}


	@Override
	public boolean isLearnRunning() throws RemoteException {
		if ((kb != null) && (kb instanceof KBaseRemote))
			return ((KBaseRemote)kb).isLearnRunning();
		else
			return super.isLearnRunning();
	}
	

}
