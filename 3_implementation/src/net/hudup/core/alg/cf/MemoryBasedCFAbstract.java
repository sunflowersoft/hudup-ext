/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf;

import net.hudup.core.alg.MemoryBasedRecommenderAbstract;



/**
 * This abstract class implements basically a memory-based collaborative filtering represented by the interface {@link MemoryBasedCF}.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@CFAnnotation
public abstract class MemoryBasedCFAbstract extends MemoryBasedRecommenderAbstract implements MemoryBasedCF {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public MemoryBasedCFAbstract() {
		
	}
	
	
}
