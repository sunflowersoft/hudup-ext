/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * This interface represents a console.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Console extends Cloneable, Serializable, Remote {

	
	/**
	 * Starting console.
	 * @param params additional parameters.
	 * @return true if starting is successfully.
	 * @throws Exception if any error raises.
	 */
	boolean start(Object...params) throws Exception;
	

	/**
	 * Stopping console.
	 * @param params additional parameters.
	 * @return true if starting is successfully.
	 * @throws Exception if any error raises.
	 */
	boolean stop(Object...params) throws Exception;


}
