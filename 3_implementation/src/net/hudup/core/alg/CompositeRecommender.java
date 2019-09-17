/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import net.hudup.core.logistic.Composite;


/**
 * Recommendation strategy is defined as the co-ordination of recommendation algorithms such as collaborative filtering and content-based filtering in accordance with coherent process so as to achieve the best result of recommendation. In simplest form, strategy is identified with a recommendation algorithm. Recommender service is the most complex service because it implements both algorithms and strategies and applies these strategies in accordance with concrete situation.
 * {@link CompositeRecommender} represents the recommendation strategy, called {@code composite recommender}. {@link CompositeRecommender} is combination of other {@link Recommender} algorithms in order to produce the best list of recommended items.
 * So {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Composite
public interface CompositeRecommender extends Recommender, CompositeAlg {

	
	/**
	 * {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
	 * Such list has a key specified by this constant.
	 */
	final static String INNER_RECOMMENDER = "inner_recommender";
	
	
	/**
	 * {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
	 * This method gets such recommender list from the configuration.
	 * @return list of inner recommender (s).
	 */
	AlgList getInnerRecommenders();
	
	
}
