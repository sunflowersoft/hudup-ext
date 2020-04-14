/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui.toolkit;

/**
 * This interface indicates the component that implements it needs to be closed when closing.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Dispose {
	
	
	/**
	 * Disposing object.
	 */
	void dispose();
	
	
	/**
	 * Testing whether running.
	 * @return whether running.
	 */
	boolean isRunning();
	
	
}
