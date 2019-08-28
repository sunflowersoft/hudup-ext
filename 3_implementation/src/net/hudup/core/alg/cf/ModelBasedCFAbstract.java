/**
 * 
 */
package net.hudup.core.alg.cf;

import net.hudup.core.alg.ModelBasedRecommenderAbstract;


/**
 * This abstract class implements basically a model-based collaborative filtering algorithm represented by the interface {@link ModelBasedCF}
 * @author Loc Nguyen
 * @version 10.0
 *
 */
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
