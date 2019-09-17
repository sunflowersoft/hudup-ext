/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This class represents exception occurred in Hudup framework. It inherits directly from {@link Exception} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class HudupException extends Exception {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor with error message.
	 * 
	 * @param message specified error message.
	 */
	public HudupException(String message) {
		super(message);
	}

	
}
