/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

/**
 * This abstract class implements basically the recommendation service represented by service recommender interface. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ServiceRecommenderAbstract extends RecommenderAbstract implements ServiceRecommender, ServiceAlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public ServiceRecommenderAbstract() {
		// TODO Auto-generated constructor stub
	}

}
