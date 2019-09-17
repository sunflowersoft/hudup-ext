/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

/**
 * This abstract class represents optional fraction (a/b).
 * The method {@link #operator(double)} is applied to the method {@link #value()}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class OperatorFractionMetricValue extends FractionMetricValue {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with the numerator and denominator.
	 * @param a specified numerator.
	 * @param b specified denominator.
	 */
	public OperatorFractionMetricValue(double a, double b) {
		super(a, b);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with other fraction.
	 * @param fraction other fraction.
	 */
	public OperatorFractionMetricValue(FractionMetricValue fraction) {
		super(fraction.a, fraction.b);
	}
	
	
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return operator(a / b);
	}


	/**
	 * Operator which is applied into method {@link #value()}.
	 * @param value specified value.
	 * @return value after taking the specific operator.
	 */
	protected abstract double operator(double value);
	
	
}
