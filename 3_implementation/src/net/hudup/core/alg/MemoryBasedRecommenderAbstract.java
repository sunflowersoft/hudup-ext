/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.Util;
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
		double threshold = dataset.getConfig().getAsReal(DataConfig.RELEVANT_RATING_FIELD);
		if (Util.isUsed(threshold)) this.config.put(DataConfig.RELEVANT_RATING_FIELD, threshold);
		
		this.config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		this.config.addReadOnly(DataConfig.MAX_RATING_FIELD);
		if (Util.isUsed(threshold)) this.config.addReadOnly(DataConfig.RELEVANT_RATING_FIELD);
		
		if (this.config.getAsBoolean(MINMAX_RATING_RECONFIG))
			reconfigMinMaxRating(this.config, dataset);
	}
	
	
	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		if (this.config != null) {
			this.config.setMetadata(null);
			this.config.removeReadOnly(DataConfig.MIN_RATING_FIELD);
			this.config.removeReadOnly(DataConfig.MAX_RATING_FIELD);
		}
		
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
