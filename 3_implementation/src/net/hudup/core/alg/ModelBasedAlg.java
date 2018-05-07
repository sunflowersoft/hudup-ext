package net.hudup.core.alg;

/**
 * This interface indicates an algorithm that learns and uses knowledge base to execute.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ModelBasedAlg extends Alg {

	
	/**
	 * Getting the internal knowledge base represented by {@link KBase} interface.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the internal {@code KBase} contains such pattern.
	 * @return knowledge base {@link KBase}
	 */
	KBase getKBase();
	
	
	/**
	 * Creating a new knowledge base represented by {@link KBase} interface.
	 * Normally, {@code KBase} is created from dataset.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the new {@code KBase} contains such pattern.
	 * @return knowledge base {@link KBase}
	 */
	public abstract KBase createKB();
	
	
}
