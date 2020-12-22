/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents information of evaluator.
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
	 * List of algorithm names.
	 */
	public List<String> algNames = null;
	
	
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
	public boolean inAlgSetup = false;
	
	
	/**
	 * Elapsed time in miliseconds.
	 */
	public long elapsedTime = 0;
	
	
	/**
	 * Started date.
	 */
	public long startDate = 0;
	
	
	/**
	 * Ended date.
	 */
	public long endDate = 0;
	
	
	/**
	 * Array of statuses.
	 */
	public String[] statuses = null;
	
	
	/**
	 * Resetting this information.
	 */
	public void reset() {
		algName = null;
		algNames = null;
		datasetId = 0;
		progressStep = 0;
		progressTotal = 0;
		vCurrentTotal = 0;
		vCurrentCount = 0;
		inAlgSetup = false;
		elapsedTime = 0;
		startDate = 0;
		endDate = 0;
		statuses = null;
	}
	
	
	/**
	 * Converting this evaluation information into evaluation progress event.
	 * @return evaluation progress event.
	 */
	public EvaluateProgressEvent toEvaluateProgressEvent() {
		EvaluateProgressEvent evt = new EvaluateProgressEvent(null, progressTotal, progressStep);
		evt.setCurrentTotal(vCurrentTotal);
		evt.setCurrentCount(vCurrentCount);
		evt.setDatasetId(datasetId);
		evt.setAlgName(algName);
		
		return evt;
	}
	
	
}
