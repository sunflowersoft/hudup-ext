/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf;

import net.hudup.core.alg.ModelBasedRecommenderAbstract;


/**
 * This abstract class implements basically a model-based collaborative filtering algorithm represented by the interface {@link ModelBasedCF}
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@CFAnnotation
public abstract class ModelBasedCFAbstract extends ModelBasedRecommenderAbstract implements ModelBasedCF {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public ModelBasedCFAbstract() {
		super();
		// TODO Auto-generated constructor stub
	}


}
