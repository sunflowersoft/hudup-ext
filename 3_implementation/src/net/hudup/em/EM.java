package net.hudup.em;

import net.hudup.core.alg.TestingAlg;
import net.hudup.core.data.Dataset;


/**
 * <code>EM</code> is the most abstract interface for all expectation maximization (EM) algorithm.
 * Its main method is {@link #learn()} used to learn parameters.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface EM extends TestingAlg {

	
	/**
	 * Maximum number of iterations.
	 */
	public final static int EM_MAX_ITERATION = 10000;
	
	
	/**
	 * Setting up this EM based on specified dataset.
	 * In this current version, this method initialize the data sample {@link #sample} for learning parameter.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this EM. This parameter is really an array of sub-parameters.
	 * @throws Exception if any error raises.
	 */
	void setup(Dataset dataset, Object... info) throws Exception;

	
	/**
	 * Unset this EM. After this method is called, this EM cannot be used unless the method {@link #setup(Dataset, Object...)} is called again.
	 */
	public void unsetup();
	
	
	/**
	 * Main method to learn parameters.
	 * @return the parameter to be learned.
	 * @exception Exception if any error occurs.
	 */
	public Object learn() throws Exception;
	
	
	/**
	 * Getting current iteration.
	 * @return current iteration. Return 0 if the algorithm does not run yet or run failed. 
	 */
	int getCurrentIteration();
	
	
	/**
	 * Getting current parameter.
	 * @return current parameter. Return null if the algorithm does not run yet or run failed. 
	 */
	Object getCurrentParameter();
	
	
	/**
	 * Getting estimated parameter.
	 * @return estimated parameter. Return null if the algorithm does not run yet or run failed. 
	 */
	Object getEstimatedParameter();
	
	
    /**
     * Convert parameter to shown text.
     * @param parameter specified parameter.
     * @return shown text converted from specified parameter.
     */
    String parameterToShownText(Object parameter);
    
	
}
