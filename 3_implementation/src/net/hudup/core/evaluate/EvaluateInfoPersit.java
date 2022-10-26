/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;

/**
 * This class contains persistent values for evaluation.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluateInfoPersit implements Cloneable, Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Referred pool result flag. Note, referred pool is managed by other tools different from evaluator.
	 */
	public boolean isRefPoolResult = false;
	
	
	/**
	 * Name of referred pool result.
	 */
	public String refPoolResultName = null;
	
	
	/**
	 * Default constructor.
	 */
	public EvaluateInfoPersit() {

	}
	

}
