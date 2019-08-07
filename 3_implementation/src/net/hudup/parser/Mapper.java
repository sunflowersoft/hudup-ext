package net.hudup.parser;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public interface Mapper {
	
	/**
	 * 
	 * @param value
	 * @return value
	 */
	double map(double value);
	
	
	/**
	 * 
	 * @param value
	 * @return inverse value
	 */
	double imap(double value);
}
