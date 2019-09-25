/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

/**
 * This interface declares the utility to process algorithms.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface AlgUtil {

	
	/**
	 * Wrapping a remote algorithm.
	 * @param remoteAlg remote algorithm.
	 * @param exclusive exclusive mode.
	 * @return wrapper of a remote algorithm.
	 */
	AlgRemoteWrapper wrap(AlgRemote remoteAlg, boolean exclusive);
	
	
}
