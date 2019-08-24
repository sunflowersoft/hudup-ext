package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.NextUpdate;

/**
 * This interface represents any executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public interface ExecutableAlg extends Alg {
	
	
	/**
	 * Setting up this testing algorithm based on specified dataset.
	 * In this current version, this method initializes the data sample for learning parameter and then calls {@link #learn(Serializable...)} method.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Dataset dataset, Serializable...info) throws RemoteException;
	
	
	/**
	 * Setting up this testing algorithm based on specified sample.
	 * In this current version, this method calls {@link #learn(Serializable...)} method.
	 * @param sample specified sample.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Fetcher<Profile> sample, Serializable...info) throws RemoteException;

	
	/**
	 * Unset up this testing algorithm, which release resources used by the {@link #setup(Dataset, Serializable...)} method.
	 * Exceptionally, for testing algorithm, after this method is called, this algorithm can be used (dependent on specific application).
	 */
	public void unsetup() throws RemoteException ;
	
	
	/**
	 * Main method to learn parameters. As usual, it is called by {@link #setup(Dataset, Serializable...)}.
	 * @param info additional parameter.
	 * @return the parameter to be learned. Return null if learning is failed.
	 * @exception Exception if any error occurs.
	 */
	Serializable learn(Serializable...info) throws Exception;

	
	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter.
	 * @return result of execution. Return null if execution is failed.
	 */
	Serializable execute(Serializable input);
	
	
	/**
	 * Getting parameter of the algorithm.
	 * @return parameter of the algorithm. Return null if the algorithm does not run yet or run failed. 
	 */
	Serializable getParameter();
	
	
    /**
     * Convert parameter to shown text.
     * @param parameter specified parameter.
     * @param info addition information.
     * @return shown text converted from specified parameter.
     */
    String parameterToShownText(Serializable parameter, Serializable...info);
    
    
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
