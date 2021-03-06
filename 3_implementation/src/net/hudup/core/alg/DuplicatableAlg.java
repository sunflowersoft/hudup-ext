/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

/**
 * This interface represents the algorithm that are able to be duplicated.
 * Especially, the name of an algorithm that can be duplicated is retrieved by looking up the field {@value #DUPLICATED_ALG_NAME_FIELD} in its configuration.
 * If such field does not exist, users define particular name via the returned value of {@link #getName()} method.  
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface DuplicatableAlg extends Alg {

	
	/**
	 * Field in configuration indicates the algorithm name.
	 */
	final static String DUPLICATED_ALG_NAME_FIELD = "duplicated_alg_name";
		
	
	/**
	 * Setting the name Declaring the name of algorithm, the name is used to query algorithm in register table. 
	 * Especially, the name of an algorithm that can be duplicated is retrieved by looking up the field {@value #DUPLICATED_ALG_NAME_FIELD} in its configuration.
	 * If such field does not exist, users define particular name via the returned value of {@link #getName()} method.  
	 * @param name new name algorithm name.
	 */
	void setName(String name);
	
	
	/**
	 * Create the new instance of algorithm, concrete algorithm is determined run-time.
	 * Especially, the name of an algorithm that can be duplicated is retrieved by looking up the field {@value #DUPLICATED_ALG_NAME_FIELD} in its configuration.
	 * If such field does not exist, users define particular name via the returned value of {@link #getName()} method.  
	 * @return new instance of this algorithm.
	 */
	@Override
	Alg newInstance();
	
	
}
