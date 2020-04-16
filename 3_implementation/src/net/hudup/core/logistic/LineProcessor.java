/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * Any class implementing this interface must define the main method {@link #process(String)} to specify how to process a line string.
 * This interface is often used as an input for a method that needs to process a file line by line.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface LineProcessor {
	
	/**
	 * Any class implementing this interface must define the main method {@link #process(String)} to specify how to process a line string.
	 * @param line specified string line.
	 */
	void process(String line);
	
}
