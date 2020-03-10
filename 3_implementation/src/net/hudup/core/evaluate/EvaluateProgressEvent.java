/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.util.EventObject;

import net.hudup.core.logistic.NextUpdate;

/**
 * This class represents an event for monitoring the progress of evaluation task in evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EvaluateProgressEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Referred evaluator.
	 */
	protected Evaluator evaluator = null;

	
	/**
	 * Total number of steps in progress.
	 */
	protected int progressTotal = 0;
	
	
	/**
	 * Current step in progress.
	 */
	protected int progressStep = 0;
	
	
	/**
	 * Algorithm name.
	 */
	protected String algName = "";
	
	
	/**
	 * Dataset identifier.
	 */
	protected int datasetId = -1;
	
	
	/**
	 * Total number of rating vectors.
	 */
	protected int vCurrentTotal = 0;
	
	
	/**
	 * Current count of rating vectors.
	 */
	protected int vCurrentCount = 0;
	
	
	/**
	 * Constructor with specified evaluator, total number of steps in progress, and current step.
	 * @param evaluator specified evaluator.
	 * @param progressTotal total number of steps in progress.
	 * @param progressStep current step.
	 */
	@NextUpdate
	public EvaluateProgressEvent(Evaluator evaluator, int progressTotal, int progressStep) {
//		super(evaluator); //Test different hosts for RMI, evaluator wrapper can solve.
		super(new Integer(1)); //Not use evaluator because of improving network speed.
		
		this.progressTotal = progressTotal;
		this.progressStep = progressStep;
	}
	
	
	/**
	 * Getting evaluator.
	 * @return evaluator.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Evaluator getEvaluator() {
		Object source = getSource();
		if (source instanceof Evaluator)
			return (Evaluator)source;
		else
			return null;
	}
	
	
	/**
	 * Getting total number of steps in progress.
	 * @return total number of steps in progress.
	 */
	public int getProgressTotal() {
		return progressTotal;
	}
	

	/**
	 * Getting current step in progress.
	 * @return progress current step.
	 */
	public int getProgressStep() {
		return progressStep;
	}
	
	
	/**
	 * Getting algorithm name.
	 * @return algorithm name.
	 */
	public String getAlgName() {
		return algName;
	}
	
	
	/**
	 * Setting algorithm name.
	 * @param algName algorithm name.
	 */
	public void setAlgName(String algName) {
		this.algName = algName;
	}
	
	
	/**
	 * Getting dataset identifier.
	 * @return dataset identifier.
	 */
	public int getDatasetId() {
		return datasetId;
	}
	
	
	/**
	 * Setting dataset identifier.
	 * @param datasetId dataset identifier.
	 */
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}
	
	
	/**
	 * Getting total number of rating vectors.
	 * @return total number of rating vectors.
	 */
	public int getCurrentTotal() {
		return vCurrentTotal;
	}
	
	
	/**
	 * Setting total number of rating vectors.
	 * @param vCurrentTotal total number of rating vectors.
	 */
	public void setCurrentTotal(int vCurrentTotal) {
		this.vCurrentTotal = vCurrentTotal;
	}
	
	
	/**
	 * Getting current number of rating vectors.
	 * @return current count of rating vectors.
	 */
	public int getCurrentCount() {
		return vCurrentCount;
	}
	
	
	/**
	 * Setting current number of rating vectors.
	 * @param vRatingCount current number of rating vectors.
	 */
	public void setCurrentCount(int vRatingCount) {
		this.vCurrentCount = vRatingCount;
	}
	
	
}
