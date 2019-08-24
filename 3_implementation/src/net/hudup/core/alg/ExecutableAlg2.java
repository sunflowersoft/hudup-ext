package net.hudup.core.alg;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.Inspectable;

/**
 * This interface represents extension of an executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExecutableAlg2 extends ExecutableAlg, Inspectable {
	
	
	/**
	 * Setting up this testing algorithm based on specified dataset.
	 * In this current version, this method initializes the data sample for learning parameter and then calls {@link #learn(Object...)} method.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws Exception if any error raises.
	 */
	void setup(Dataset dataset, Object...info) throws Exception;
	
	
	/**
	 * Setting up this testing algorithm based on specified sample.
	 * In this current version, this method calls {@link #learn(Object...)} method.
	 * @param sample specified sample.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws Exception if any error raises.
	 */
	void setup(Fetcher<Profile> sample, Object...info) throws Exception;

	
	/**
	 * Unset up this testing algorithm, which release resources used by the {@link #remoteSetup(Dataset, Object...)} method.
	 * Exceptionally, for testing algorithm, after this method is called, this algorithm can be used (dependent on specific application).
	 */
	public void unsetup();
	
	
	/**
	 * Main method to learn parameters. As usual, it is called by {@link #remoteSetup(Dataset, Object...)}.
	 * @param info additional parameter.
	 * @return the parameter to be learned. Return null if learning is failed.
	 * @exception Exception if any error occurs.
	 */
	public Object learn(Object...info) throws Exception;

	
	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter.
	 * @return result of execution. Return null if execution is failed.
	 */
	Object execute(Object input);
	
	
	/**
	 * Getting parameter of the algorithm.
	 * @return parameter of the algorithm. Return null if the algorithm does not run yet or run failed. 
	 */
	Object getParameter();
	
	
    /**
     * Convert parameter to shown text.
     * @param parameter specified parameter.
     * @param info addition information.
     * @return shown text converted from specified parameter.
     */
    String parameterToShownText(Object parameter, Object...info);
    
    
	/**
	 * Getting description of this algorithm.
	 * @return text form of this model.
	 */
	String getDescription();
	

}
