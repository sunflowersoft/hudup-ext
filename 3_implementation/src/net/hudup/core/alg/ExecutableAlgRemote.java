package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;

/**
 * This interface represents a executable remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExecutableAlgRemote extends AlgRemote {

	/**
	 * Setting up this testing algorithm based on specified dataset.
	 * In this current version, this method initializes the data sample for learning parameter and then calls {@link #learn(Object...)} method.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Dataset dataset, Object...info) throws RemoteException;
	
	
	/**
	 * Setting up this testing algorithm based on specified sample.
	 * In this current version, this method calls {@link #learn(Object...)} method.
	 * @param sample specified sample.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Fetcher<Profile> sample, Object...info) throws RemoteException;

	
	/**
	 * Unset up this testing algorithm, which release resources used by the {@link #setup(Dataset, Object...)} method.
	 * Exceptionally, for some algorithm, after this method is called, this algorithm can be used (dependent on specific application).
	 * So this unsetup method of executable algorithm should not clear parameter.
	 * @throws RemoteException if any error raises.
	 */
	public void unsetup() throws RemoteException;
	
	
	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter.
	 * @return result of execution. Return null if execution is failed.
	 * @throws RemoteException if any error raises.
	 */
	Object execute(Object input) throws RemoteException;
	
	
	/**
	 * Getting parameter of the algorithm.
	 * @return parameter of the algorithm. Return null if the algorithm does not run yet or run failed. 
	 * @throws RemoteException if any error raises.
	 */
	Object getParameter() throws RemoteException;
	
	
    /**
     * Convert parameter to shown text.
     * @param parameter specified parameter.
     * @param info addition information.
     * @return shown text converted from specified parameter.
	 * @throws RemoteException if any error raises.
     */
    String parameterToShownText(Object parameter, Object...info) throws RemoteException;
    
    
}
