/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;

/**
 * This interface declares methods for remote knowledge base.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface KBaseRemoteTask extends AutoCloseable {
	
	
	/**
	 * This method is used to read {@code KBase} from storage system. Storage system can be files, database, etc.
	 * @throws RemoteException if any error raises.
	 */
	void load() throws RemoteException;
	

	/**
	 * This method is responsible for creating {@code KBase} or learning from specified dataset.
	 * Because every model-based recommender owns distinguished {@code KBase}, the second parameter is such algorithm.
	 * The association between model-based recommender and {@code KBase} is tight.
	 * This method tells us that {@code KBase} can be learned by any approaches: machine learning, data mining, artificial intelligence, statistics, etc.
	 * @param dataset specified dataset.
	 * @param alg the algorithm that owns this KBase.
	 * @throws RemoteException if any error raises.
	 */
	void learn(Dataset dataset, Alg alg) throws RemoteException;
	
	
	/**
	 * This method is used to write {@code KBase} to storage system according to internal configuration. Storage system can be files, database, etc.
	 * This method in turn calls {@link #save(DataConfig)} method.
	 * @throws RemoteException if any error raises.
	 */
	void save() throws RemoteException;
	
	
	/**
	 * This method is used to write {@code KBase} to storage system according to the specified configuration.
	 * It is possible to store knowledge base anywhere according to the specified configuration.
	 * This method is called by {@link #save()} method.
	 * @param storeConfig specified configuration where to store knowledge base.
	 * @throws RemoteException if any error raises.
	 */
	void save(DataConfig storeConfig) throws RemoteException;
	
	
	/*
	 * Close this KBase. After this method is called, KBase becomes empty, which means that method {@link #isEmpty()} returns {@code true} but KBase can be re-learned by calling the method {@link #learn(Dataset, Alg)} again.
	 * This close method does not call unexport method as usual, which is a special case.
	 * @see net.hudup.core.data.AutoCloseable#close()
	 */
	@Override
	void close() throws Exception;

	
	/**
	 * Cleaning out {@code KBase}. This method is stronger than method {@link #close()} because it firstly deletes all storage and then calls {@link #close()}.
	 * Note, after method {@link #close()} was called, KBase in memory becomes empty , which means that method {@link #isEmpty()} returns {@code true} but KBase is still stored in persistent storage.
	 * However, after this method was called, KBase in both memory and storage is empty but it can be re-learned.
	 * Using this method should be careful.
	 * @throws RemoteException if any error raises.
	 */
	void clear() throws RemoteException;
	
	
	/**
	 * Checking whether {@code KBase} is empty or not. If a KBase which is empty, it needs to re-learned by calling the method {@link #learn(Dataset, Alg)} again.
	 * @return whether knowledge base is empty or not.
	 * @throws RemoteException if any error raises.
	 */
	boolean isEmpty() throws RemoteException;


}
