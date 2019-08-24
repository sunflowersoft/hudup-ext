package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;

/**
 * This interface represents any executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExecutableAlg extends Alg {
	
	
	/**
	 * Setting up this testing algorithm based on specified dataset. This is remote method.
	 * In this current version, this method initializes the data sample for learning parameter and then calls {@link #learn(Object...)} method.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void remoteSetup(Dataset dataset, Serializable...info) throws RemoteException;

	
	/**
	 * Setting up this testing algorithm based on specified sample. This is remote method.
	 * In this current version, this method calls {@link #learn(Object...)} method.
	 * @param sample specified sample.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void remoteSetup(Fetcher<Profile> sample, Serializable...info) throws RemoteException;

	
	/**
	 * Unset up this testing algorithm, which release resources used by the {@link #remoteSetup(Dataset, Object...)} method.
	 * Exceptionally, for testing algorithm, after this method is called, this algorithm can be used (dependent on specific application).
	 * This is remote method.
	 * @throws RemoteException if any error raises.
	 */
	public void remoteUnsetup() throws RemoteException;

	
	/**
	 * Executing this algorithm by input parameter.
	 * @param input specified input parameter.
	 * This is remote method.
	 * @return result of execution. Return null if execution is failed.
	 * @throws RemoteException if any error raises.
	 */
	Serializable remoteExecute(Serializable input) throws RemoteException;
	
	
	/**
	 * Getting description of this algorithm.
	 * This is remote method.
	 * @return text form of this model.
	 * @throws RemoteException if any error raises.
	 */
	String remoteGetDescription() throws RemoteException;

	
	/**
	 * Add the specified setting up listener to the end of listener list.
	 * This is remote method.
	 * @param listener specified setting up listener
	 * @throws RemoteException if any error raises.
	 */
	void addSetupListener(SetupAlgListener listener) throws RemoteException;

	
	/**
	 * Remove the specified setting up listener from the listener list.
	 * This is remote method.
	 * @param listener specified setting up listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeSetupListener(SetupAlgListener listener) throws RemoteException;
    
	
}
