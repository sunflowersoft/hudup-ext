/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper;

import java.io.Serializable;

/**
 * This interface represents information event about an algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface WInfoEvent extends Serializable, Cloneable {

	
	/**
	 * Getting information.
	 * @return information.
	 */
	String getInfo();
	
	
	/**
	 * Setting information.
	 * @param info specified information.
	 */
	void setInfo(String info);
	
	
}
