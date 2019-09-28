/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.PropList;
import net.hudup.core.data.RatingMatrix;

/**
 * This abstract class models a so-called integrated KBase, which provides advances methods on knowledge base.
 * For example, method {@link #estimate(int, int)} estimates a rating value of specified user giving on specified item.
 * Integrated KBase is often integrated with complex recommendation algorithms.
 * Especially, any integrated KBase has internal data structure dependent on concrete application.
 * The method {@link #destroyDataStructure()} destroys such internal data structure when integrated KBase is closed. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class KBaseRecommendIntegrated extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public KBaseRecommendIntegrated() {
		super();
	}
	
	
	/**
	 * This method estimates a rating value of specified user giving on specified item.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 * 
	 * @param userId specified user identifier (user ID).
	 * @param itemId specified item identifier (item ID).
	 * @return estimated rating value.
	 */
	public abstract double estimate(int userId, int itemId);
	
	
	@Override
	public void learn(Dataset dataset, Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		learn0(dataset, alg);
	}


	/**
	 * This method is responsible for creating KBase or learning from specified dataset according to specified algorithm.
	 * This method is called by {@link #learn(Dataset, Alg)}.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 * 
	 * @param dataset specified dataset.
	 * @param alg specified algorithm which is often a recommendation algorithm (called recommender).
	 */
	public abstract void learn0(Dataset dataset, Alg alg);
	
	
	/**
	 * This method is responsible for creating KBase or learning from specified user rating matrix.
	 * This method is called by {@link #learn(Dataset, Alg)}.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 * 
	 * @param userRatingMatrix specified user rating matrix, represented by {@link RatingMatrix}.
	 * In user rating matrix, each row is a vector of rating values that a user gives on many items.
	 */
	public abstract void learn0(RatingMatrix userRatingMatrix);
	
	
	@Override
	public void load() throws RemoteException {
		// TODO Auto-generated method stub
		super.load();
		
		load0();
	}

	
	/**
	 * This method is responsible for creating KBase or learning from dataset specified in the configuration of {@link KBaseRecommendIntegrated}.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 */
	public abstract void load0();
	
	
	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		// TODO Auto-generated method stub
		super.save(storeConfig);
		
		export0(storeConfig);
	}

	
	/**
	 * This method copies (exports) the internal configuration to the specified configuration.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 * @param storeConfig the configuration object to receive entries of the configuration of this knowledge base.
	 */
	public abstract void export0(DataConfig storeConfig);
	
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
		
		destroyDataStructure();
	}


	/**
	 * Especially, any integrated KBase has internal data structure dependent on concrete application.
	 * So this method destroys such internal data structure when integrated KBase is closed.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 */
	protected abstract void destroyDataStructure();
	
	
	/**
	 * Retrieving default parameters of {@link KBaseRecommendIntegrated} from its configuration.
	 * Because this method is abstract, any class extends {@link KBaseRecommendIntegrated} must complete it.
	 * @return default parameters stored in {@link PropList}.
	 */
	public abstract PropList getDefaultParameters();
	
	
}
