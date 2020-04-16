/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;

import net.hudup.core.Constants;

/**
 * This utility class represents a pair of values represented by its two variables {@link #one} and {@link #other}.
 * Such two variables are dual. 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Dual implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This variable represents the first value. Note that two variables {@link #one} and {@link #other} are dual.
	 */
	protected double one = Constants.UNUSED;
	
	
	/**
	 * This variable represents the second value. Note that two variables {@link #one} and {@link #other} are dual.
	 */
	protected double other = Constants.UNUSED;
	
	
	/**
	 * Constructor with the first value and the second value.
	 * @param one The first value.
	 * @param other The second value.
	 */
	public Dual(double one, double other) {
		this.one = one;
		this.other = other;
	}
	
	
	/**
	 * Getting the first value.
	 * @return the first value.
	 */
	public double one() {
		return one;
	}
	
	
	/**
	 * Getting the second value.
	 * @return the second value
	 */
	public double other() {
		return other;
	}
	
	
	/**
	 * Setting the first value.
	 * @param one The first value.
	 */
	public void setOne(double one) {
		this.one = one;
	}
	

	/**
	 * Setting the second value.
	 * @param other The second value.
	 */
	public void setOther(double other) {
		this.other = other;
	}
	
	
}
