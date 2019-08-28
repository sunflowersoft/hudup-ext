package net.hudup.core.logistic;

import java.io.Serializable;

/**
 * This interface represents an inspectable object which can be inspected via its {@link #getInspector()} method.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Inspectable extends Serializable {

	
	/**
	 * Getting the inspector to inspect the object. This is not remote method.
	 */
	Inspector getInspector();

	
}
