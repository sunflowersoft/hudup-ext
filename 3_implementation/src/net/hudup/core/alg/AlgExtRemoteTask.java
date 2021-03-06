/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Collection;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;

/**
 * This interface declares methods for remote extensive algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface AlgExtRemoteTask extends AlgRemoteTask {


	/**
	 * Setting up this algorithm based on specified dataset.
	 * In this current version, this method initializes the data sample for learning parameter and then calls learning method.
	 * @param dataset specified dataset.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Dataset dataset, Object...info) throws RemoteException;
	
	
	/**
	 * Setting up this algorithm based on specified sample.
	 * In this current version, this method calls learning method.
	 * @param sample specified sample.
	 * @param info additional parameters to set up this algorithm. This parameter is really an array of sub-parameters.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Fetcher<Profile> sample, Object...info) throws RemoteException;

	
	/**
	 * Setting up this algorithm based on specified sample.
	 * In this current version, this method calls learning method.
	 * @param sample specified sample.
	 * @param info additional parameters to set up this algorithm.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Collection<Profile> sample, Object...info) throws RemoteException;

	
	/**
	 * Unset up this algorithm, which release resources used by the {@link #setup(Dataset, Object...)} method.
	 * Exceptionally, for some algorithm, after this method is called, this algorithm can be used (dependent on specific application).
	 * So this unsetup method of extensive algorithm should not clear parameter.
	 * @throws RemoteException if any error raises.
	 */
	public void unsetup() throws RemoteException;
	
	
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
