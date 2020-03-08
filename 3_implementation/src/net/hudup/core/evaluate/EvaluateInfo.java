/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;

/**
 * This class represents information of evaluation.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class EvaluateInfo implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Algorithm name.
	 */
	public String algName = null;
	
	
	/**
	 * Dataset identifier.
	 */
	public int datasetId = 0;
	
	
	/**
	 * Progress step which is the current record over all datasets.
	 */
	public int progressStep = 0;
	
	
	/**
	 * Progress total which is the number of records over all datasets.
	 */
	public int progressTotal = 0;
	
	
	/**
	 * The current record of current dataset.
	 */
	public int vCurrentTotal = 0;
	
	
	/**
	 * The number of records of current dataset.
	 */
	public int vCurrentCount = 0;
	
	
	/**
	 * Flag to indicate whether in setting up progress.
	 */
	public boolean inSetup = false;
	
	
	/**
	 * Time elapsed in miliseconds.
	 */
	public long timeElapse = 0;
	
	
	/**
	 * Default constructor.
	 */
	public EvaluateInfo() {
		reset();
	}
	
	
	/**
	 * Resetting this information.
	 */
	public void reset() {
		algName = null;
		datasetId = 0;
		progressStep = 0;
		progressTotal = 0;
		vCurrentTotal = 0;
		vCurrentCount = 0;
		inSetup = false;
		timeElapse = 0;
	}
	
	
}
