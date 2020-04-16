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
 * As a convention, this interface is called model-based recommender.
 * <br>
 * Model-based recommender applies knowledge database represented by {@link KBase} interface into performing recommendation task.
 * In other words, {@code KBase} provides both necessary information and inference mechanism to model-based recommender.
 * Ability of inference is the unique feature of {@code KBase}. Model-based recommender is responsible for creating {@code KBase} by
 * calling its {@link #newKB()} method and so, every model-based recommender owns distinguished {@code KBase}.
 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, its {@code KBase} contains such pattern.
 * model-based recommender always takes advantages of {@code KBase} whereas memory-based recommender uses dataset in execution.
 * <br>
 * In general, methods of model-based recommender always using {@code KBase} are {@code ModelBasedRecommender.setup()}, {@code ModelBasedRecommender.createKB()}, {@code ModelBasedRecommender.estimate(...)} and {@code ModelBasedRecommender.recommend(...)}.
 * Especially, it is mandatory that {@code setup()} method of model-based recommender calls method {@code KBase.learn(...)} or {@code KBase.load()}.
 * The association between memory-based recommender represented by {@code MemoryBasedRecommender} interface and dataset indicates that all memory-based algorithms use dataset for recommendation task.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ModelBasedRecommender extends Recommender, ModelBasedAlg {

	
}
