package net.hudup.core.logistic;

/**
 * This interface represents an inspectable object which can be inspected via its {@link #getInspector()} method.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Inspectable {

	
	/**
	 * Getting the inspector to inspect the object. This is not remote method.
	 * @return inspector to inpsect the inspectable object.
	 */
	Inspector getInspector();

	
}
