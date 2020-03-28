/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.cf.NeighborCFItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class implements basically the recommendation strategy (composite recommender) represented by the interface @link CompositeRecommender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Composite
@NextUpdate
public abstract class CompositeRecommenderAbstract extends RecommenderAbstract implements CompositeRecommender, CompositeAlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This constant specifies the class of default internal recommender.
	 */
	final static Class<? extends Recommender> DEFAULT_RECOMMENDER_CLASS = NeighborCFItemBased.class;

	
	/**
	 * Default constructor.
	 */
	public CompositeRecommenderAbstract() {
		super();
		
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
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
		
		try {
			Recommender defaultRecommender = DEFAULT_RECOMMENDER_CLASS.newInstance();
			defaultRecommender.getConfig().setStoreUri(config.getStoreUri().concat(defaultRecommender.getName()));
			
			config.put(INNER_RECOMMENDER, new AlgList(defaultRecommender));
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return config;
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		unsetup();
		
		AlgList recommenders = getInnerRecommenders();
		for (int i = 0; i < recommenders.size(); i++) {
			Recommender recommender = (Recommender) recommenders.get(i);
			recommender.setup(dataset, params);
		}
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		
		AlgList recommenders = getInnerRecommenders();
		for (int i = 0; i < recommenders.size(); i++) {
			Recommender recommender = (Recommender) recommenders.get(i);
			recommender.unsetup();
		}
	}

	
	/**
	 * {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
	 * This method sets such recommender list into the configuration of {@link CompositeRecommender}. 
	 * @param recommenders specified list of recommender (s).
	 */
	protected void setInnerRecommenders(AlgList recommenders) {
		try {
			unsetup();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		for (int i = 0; i < recommenders.size(); i++) {
			Recommender recommender = (Recommender) recommenders.get(i);
			recommender.getConfig().setStoreUri(getConfig().getStoreUri().concat(recommender.getName()));
		}
		getConfig().put(INNER_RECOMMENDER, recommenders);
	}
	
	
	@Override
	public AlgList getInnerRecommenders() {
		AlgList recommenderList = new AlgList();
		AlgList algList = (AlgList) getConfig().get(INNER_RECOMMENDER);
		if (algList == null || algList.size() == 0)
			return recommenderList;
		
		for (int i = 0; i < algList.size(); i++) {
			Alg alg = algList.get(i);
			if (alg instanceof Recommender)
				recommenderList.add((Recommender)alg);
		}
		
		return recommenderList;
	}
	
	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {RecommenderRemote.class.getName(), CompositeAlgRemote.class.getName()};
	}
	
	
}
