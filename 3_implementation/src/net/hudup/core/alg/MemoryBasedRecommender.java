/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

/**
 * Two typical inherited interfaces of {@link Recommender} are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are interfaces for memory-based recommendation algorithm and model-based recommendation algorithm.
 * As a convention, this interface is called memory-based recommender.
 * This memory-based recommender makes recommendation tasks directly on dataset represented by {@code Dataset} class.
 * It often stores rating matrix in memory. Recommendation tasks are executed in memory.
 * Memory-based recommender is often simpler than model-based recommender.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface MemoryBasedRecommender extends Recommender, MemoryBasedAlg {

	
}
