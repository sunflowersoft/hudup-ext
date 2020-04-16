/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.ui;

import net.hudup.core.alg.Alg;

/**
 * This interface specifies user interface (UI) component showing algorithms for selection and configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface AlgListUI {

	
	/**
	 * Returning selected algorithm.
	 * @return selected algorithm.
	 */
	Alg getSelectedAlg();
	
	
	/**
	 * Testing whether this component is enabled.
	 * @return whether this component is enabled.
	 */
	boolean isEnabled();
	
}
