/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cbf;

import net.hudup.core.alg.MemoryBasedRecommender;
import net.hudup.core.alg.Recommender;

/**
 * Note, the interface {@code Recommender} represents all recommendation algorithm.
 * Two typical inherited interfaces of {@link Recommender} are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are interfaces for memory-based recommendation algorithm and model-based recommendation algorithm.
 * There are two common trends: content-base filtering (CBF) and collaborative filtering (CF) in building up a recommendation algorithms as follows:
 * <ul>
 * <li>
 * The CBF recommends an item to user if such item has similarities in contents to other items that he like most in the past (his rating for such item is high). Note that each item has contents which are its properties and so all items will compose a matrix, called item content matrix
 * </li>
 * <li>
 * The CF, on the other hand, recommends an item to user if his neighbors which are other users similar to him are interested in such item. User’s rating on any item expresses his interest on that item. All ratings on items will also compose a matrix, called rating matrix.
 * </li>
 * </ul>
 * Both CBF and CF have their own strong and weak points.
 * <br>
 * This interface represents the memory-based recommendation algorithm with content-based filtering (CBF), called {@code memory-based CBF recommender}.
 * {@code Memory-based CBF recommender} uses rating matrix, user profiles, and item profiles (as parts of the internal dataset) stored in memory for filtering task in recommendation.
 * As mentioned, two main methods of any recommender are {@code estimate(...)} and {@code recommend(...)}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@CBFAnnotation
public interface MemoryBasedCBF extends MemoryBasedRecommender {

}
