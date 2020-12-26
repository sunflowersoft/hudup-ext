/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This interface indicates the object which can simplify itself.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Simplify {

	
	/**
	 * The object simplifies itself.
	 * @return simplified object.
	 * @throws Exception if any error raises.
	 */
	Object simplify() throws Exception;
	
	
}