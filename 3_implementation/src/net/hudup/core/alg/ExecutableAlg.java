/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import net.hudup.core.logistic.Inspectable;

/**
 * This interface represents extension of an executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExecutableAlg extends Alg, ExecutableAlgRemote, Inspectable {
	
	
}
