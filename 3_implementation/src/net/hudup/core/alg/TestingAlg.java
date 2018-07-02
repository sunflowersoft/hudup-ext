package net.hudup.core.alg;

/**
 * This interface represents any executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface TestingAlg extends Alg {
	
	
	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter
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
	 * Translate this algorithm into text.
	 * @return text form of this model.
	 */
	String getDescription();
	

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
