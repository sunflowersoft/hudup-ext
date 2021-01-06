/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.io.Serializable;

import net.hudup.core.data.Wrapper;

/**
 * This class represents an important property.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ImportantProperty extends Wrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified value.
	 * @param value specified value.
	 */
	public ImportantProperty(Serializable value) {
		super(value);
	}


	@Override
	public String toString() {
		if (object != null)
			return object.toString();
		else
			return super.toString();
	}

	
}
