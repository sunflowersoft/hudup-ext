package net.hudup.parser;


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
