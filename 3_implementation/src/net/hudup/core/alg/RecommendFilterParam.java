package net.hudup.core.alg;

import java.io.Serializable;

import net.hudup.core.logistic.NextUpdate;


/**
 * Before recommendation task is performed (for example, producing a list of recommended items), the filtering tasks are performed.
 * Filtering task is specified in {@code Filter} class.
 * Concretely, filtering task is coded in the method {@code filter(...)} of {@code Filter} class.
 * As a convention, this is called {@code filter parameter}. Filter parameter can contain any information.
 * In current implementation, filter parameter is very simple, which only contains item identifier (item ID).
 * Filter parameter need to be improved in the future.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class RecommendFilterParam implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Item identifiers (item IDs).
	 */
	public Integer itemId = null;

	
	/**
	 * Default constructor. This is private constructor and so a filter parameter is created by {@link #create(int)} method.
	 */
	private RecommendFilterParam() {
		
	}
	
	
	/**
	 * Creating a filter parameter.
	 * Because the only default constructor is private, this method is used to create a filter parameter.
	 * @param itemId specified item identifier (item ID).
	 * @return new filter parameter.
	 */
	public static RecommendFilterParam create(int itemId) {
		RecommendFilterParam param = new RecommendFilterParam();
		param.itemId = itemId;
		
		return param;
	}
	
	
}
