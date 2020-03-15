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


}
