/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

/**
 * This class represents a bidirectional mapping.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public interface Mapper {
	
	
	/**
	 * Mapping specified value.
	 * @param value specified value.
	 * @return mapped value of specified value.
	 */
	double map(double value);
	
	
	/**
	 * Inverse mapping specified value.
	 * @param value specified value.
	 * @return inverse mapped value of specified value.
	 */
	double imap(double value);
	
	
}
