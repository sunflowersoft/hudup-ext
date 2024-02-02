/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import java.util.EventObject;

/**
 * This class represents an event for application.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class AppEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specific source.
	 * @param source specific source.
	 */
	protected AppEvent(Object source) {
		super(source);
	}

	
}
