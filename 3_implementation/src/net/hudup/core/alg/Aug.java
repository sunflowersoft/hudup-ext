/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.AlgDesc.MethodType;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.Inspectable;

/**
 * This interface declares the most powerful algorithm and such algorithm is atomic.
 * For example, some algorithms require recommendation results from recommendation algorithms.
 * However, this interface is not categorized in {@link MethodType}.
 * As a convention, any algorithm that implements this interface is called AUgorithm. 
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Aug extends AugRemoteTask, ExecutableAlg, SupportCacheAlg, NoteAlg, Inspectable, Serializable {

	
	/**
	 * Getting dataset.
	 * @return reference to dataset.
	 * @throws RemoteException if any error raises.
	 */
	Dataset getDataset() throws RemoteException;


	/**
	 * Creating a new knowledge base from specified dataset.
	 * @param dataset specified dataset.
	 * @return new instance of knowledge base.
	 * @throws RemoteException if any error raises.
	 */
	KBase createKBase(Dataset dataset) throws RemoteException;


}
