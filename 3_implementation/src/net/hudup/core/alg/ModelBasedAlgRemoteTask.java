/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;

/**
 * This interface declares methods for remote memory-based algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ModelBasedAlgRemoteTask {

	
	/**
	 * Getting the internal knowledge base represented by {@link KBase} interface.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the internal {@code KBase} contains such pattern.
	 * @return knowledge base {@link KBase}
	 * @throws RemoteException if any error raises.
	 */
	KBase getKBase() throws RemoteException;
	
	
	/**
	 * Creating a new knowledge base represented by {@link KBase} interface.
	 * Normally, {@code KBase} is created from dataset.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the new {@code KBase} contains such pattern.
	 * @return knowledge base {@link KBase}
	 * @throws RemoteException if any error raises.
	 */
	KBase newKB() throws RemoteException;
	
	
	/**
	 * Creating a new knowledge base represented by {@link KBase} interface from specified dataset.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the new {@code KBase} contains such pattern.
	 * @param dataset specified dataset.
	 * @return new instance of {@link KBase}.
	 * @throws RemoteException if any error raises.
	 */
	KBase createKBase(Dataset dataset) throws RemoteException;


}
