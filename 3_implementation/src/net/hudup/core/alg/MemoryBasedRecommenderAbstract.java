/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;

/**
 * This class implements basically the memory-based recommendation algorithm represented by the interface {@code MemoryBasedRecomender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class MemoryBasedRecommenderAbstract extends RecommenderAbstract implements MemoryBasedRecommender, MemoryBasedAlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal dataset.
	 */
	protected Dataset dataset = null;

	
	/**
	 * Default constructor.
	 */
	public MemoryBasedRecommenderAbstract() {
		super();
	}

	
	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		unsetup();

		this.dataset = dataset;
		
		this.config.setMetadata(dataset.getConfig().getMetadata());
		this.config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		this.config.addReadOnly(DataConfig.MAX_RATING_FIELD);
	}
	
	
	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		this.config.setMetadata(null);
		this.config.removeReadOnly(DataConfig.MIN_RATING_FIELD);
		this.config.removeReadOnly(DataConfig.MAX_RATING_FIELD);
		
		this.dataset = null;
	}

	
	@Override
	public Dataset getDataset() throws RemoteException {
		return dataset;
	}
	

	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {RecommenderRemote.class.getName(), MemoryBasedAlgRemote.class.getName()};
	}


}
