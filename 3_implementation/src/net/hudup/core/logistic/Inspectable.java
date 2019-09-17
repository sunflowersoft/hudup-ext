/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This interface represents an inspectable object which can be inspected via its {@link #getInspector()} method.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Inspectable {

	
	/**
	 * Getting the inspector to inspect the object. This is not remote method.
	 * @return inspector to inpsect the inspectable object.
	 */
	Inspector getInspector();

	
}
