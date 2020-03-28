/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.UUID;

import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is the exchanged data for dataset pair.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPairExchanged implements Serializable {

	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Remote training dataset.
	 */
	public DatasetRemote training = null;
	
	
	/**
	 * Remote testing dataset.
	 */
	public DatasetRemote testing = null;
	
	
	/**
	 * Remote whole dataset (entire dataset) from which remote training dataset and remote testing dataset are extracted.
	 */
	public DatasetRemote whole = null;

	
	/**
	 * Remote training time stamp;
	 */
	public UUID trainingUUID = null;

	
	/**
	 * Remote testing time stamp;
	 */
	public UUID testingUUID = null;
	
	
	/**
	 * Remote wshole time stamp;
	 */
	public UUID wholeUUID = null;

	
	/**
	 * Default constructor.
	 */
	public DatasetPairExchanged() {
		
	}


	/**
	 * Constructor with specified remote training dataset, testing dataset, and whole (entire) dataset.
	 * @param training specified remote training dataset.
	 * @param testing specified remote testing dataset.
	 * @param whole specified remote whole (entire) dataset from which remote training dataset and testing dataset are extracted.
	 */
	public DatasetPairExchanged(DatasetRemote training, DatasetRemote testing, DatasetRemote whole) {
		this.training = training;
		this.testing = testing;
		this.whole = whole;
	}

	
	/**
	 * Clearing this dataset pair, which means that training dataset, testing dataset, and the whole (entire) dataset are cleared. 
	 * @param forced forced mode.
	 */
	public void clear(boolean forced) {
		if (training != null) {
			try {
				if (training instanceof DatasetRemoteWrapper) {
					if (forced)
						((DatasetRemoteWrapper)training).forceClear();
					else
						((DatasetRemoteWrapper)training).clear();
				}
				else
					training.remoteClear();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		training = null;
		trainingUUID = null;
		
		if (testing != null) {
			try {
				if (testing instanceof DatasetRemoteWrapper) {
					if (forced)
						((DatasetRemoteWrapper)testing).forceClear();
					else
						((DatasetRemoteWrapper)testing).clear();
				}
				else
					testing.remoteClear();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		testing = null;
		testingUUID = null;
		
		if (whole != null) {
			try {
				if (whole instanceof DatasetRemoteWrapper) {
					if (forced)
						((DatasetRemoteWrapper)whole).forceClear();
					else
						((DatasetRemoteWrapper)whole).clear();
				}
				else
					whole.remoteClear();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		whole = null;
		wholeUUID = null;
	}
	
	
	/**
	 * Reloading remote dataset.
	 * @param remoteDataset remote dataset.
	 * @return wrapper of remote dataset.
	 */
	private static DatasetRemoteWrapper reload(DatasetRemote remoteDataset) {
		if (remoteDataset == null) return null;
		
		DataConfig config = null;
		try {
			config = (DataConfig) remoteDataset.remoteGetConfig().clone();
		} catch (Throwable e) {LogUtil.trace(e);}
		if (config == null) return null;
		
		boolean exclusive = remoteDataset instanceof DatasetRemoteWrapper ?
				((DatasetRemoteWrapper)remoteDataset).isExclusive() : true;

		if (config.getAsBoolean(DatasetAbstract.ONLY_MEMORY_FIELD)) {
			if (remoteDataset instanceof DatasetRemoteWrapper)
				return (DatasetRemoteWrapper)remoteDataset;
			else
				return Util.getPluginManager().wrap(remoteDataset, exclusive);
		}
		else if (remoteDataset instanceof NullPointer) 
			return Util.getPluginManager().wrap(remoteDataset, exclusive);
		
		try {
			if (remoteDataset instanceof DatasetRemoteWrapper)
				((DatasetRemoteWrapper)remoteDataset).forceClear();
			else
				remoteDataset.remoteClear();
		} catch (Throwable e) {LogUtil.trace(e);}

		Dataset dataset = DatasetUtil.loadDataset(config);
		if ((dataset == null) || !(dataset instanceof DatasetRemote))
			return null;
		else
			return Util.getPluginManager().wrap((DatasetRemote)dataset, exclusive);
	}
	
	
	/**
	 * Reloading training dataset, testing dataset, and the whole (entire) dataset in this dataset pair.
	 */
	public void reload() {
		training  = reload(training);
		testing  = reload(testing);
		whole  = reload(whole);
	}
	
	
}
