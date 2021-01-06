/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;

/**
 * This is the wrapper of any object. The variable {@link #object} is such internal object.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Wrapper implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal object wrapped by this wrapper.
	 */
	protected Serializable object = null;
	
	
	/**
	 * Constructor with specified object.
	 * @param object specified object.
	 */
	public Wrapper(Serializable object) {
		this.object = object;
	}
	
	
	/**
	 * Getting the internal object wrapped by this wrapper.
	 * @return internal object wrapped by this wrapper.
	 */
	public Serializable getObject() {
		return object;
	}
	
	
	/**
	 * Setting the internal object by specified object.
	 * @param object specified object that is wrapped by this wrapper.
	 */
	public void setObject(Serializable object) {
		this.object = object;
	}
	
	
}
