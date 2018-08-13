package net.hudup.core.alg;

import net.hudup.core.data.Dataset;

/**
 * This interface represents any executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface TestingAlg extends Alg {
	
	
	/**
	 * Setting up this testing algorithm based on specified dataset.
	 * In this current version, this method initializes the data sample for learning parameter and then calls {@link #learn()} method.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws Exception if any error raises.
	 */
	void setup(Dataset dataset, Object... info) throws Exception;

	
	/**
	 * Unset up this testing algorithm, which release resources used by the {@link #setup(Dataset, Object...)} method.
	 * Exceptionally, for testing algorithm, after this method is called, this algorithm can be used (dependent on specific application).
	 */
	public void unsetup();
	
	
	/**
	 * Main method to learn parameters. As usual, it is called by {@link #setup(Dataset, Object...)}.
	 * @return the parameter to be learned.
	 * @exception Exception if any error occurs.
	 */
	public Object learn() throws Exception;

	
	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter.
	 * @return result of execution.
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
	

	/**
	 * Showing a dialog to describe or operate the algorithm.
	 */
	void manifest();
	
	
	/**
	/**
	 * Add the specified setting up listener to the end of listener list.
	 * @param listener specified setting up listener
	 */
	void addSetupListener(SetupAlgListener listener);

	
	/**
	 * Remove the specified setting up listener from the listener list.
	 * @param listener specified setting up listener.
	 */
    void removeSetupListener(SetupAlgListener listener);
    
	
}
