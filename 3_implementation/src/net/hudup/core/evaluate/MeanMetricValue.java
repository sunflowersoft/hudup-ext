/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import net.hudup.core.logistic.MathUtil;

/**
 * This abstract class represents a average number called mean in form (average) as a {@link MetricValue}.
 * The average is referred by the internal variable {@link #value}.
 * The count is referred by the internal variable {@link #count}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class MeanMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The current mean.
	 */
	protected Object value = null;
	
	
	/**
	 * The count for calculating the mean.
	 */
	protected int count = 0;
	
	
	/**
	 * Default constructor.
	 */
	public MeanMetricValue() {
		
	}
	
	
	/**
	 * Constructor with specified value. The count is automatically set to be 1.
	 * @param value specified value.
	 */
	public MeanMetricValue(Object value) {
		initialize(value);
	}

	
	/**
	 * Initialize by specified value. The count is automatically set to be 1.
	 * @param value specified value.
	 */
	public void initialize(Object value) {
		if (value == null)
			reset();
		else {
			this.value = value;
			this.count = 1;
		}
	}
	
	
	@Override
	public Object value() {
		return value;
	}

	
	@Override
	public boolean isUsed() {
		return value != null && count > 0;
	}

	
	@Override
	public void reset() {
		value = null;
		count = 0;
	}


	@Override
	public String toString() {
		if (!isUsed())
			return "";
		else if (value instanceof Number)
			return MathUtil.format(((Number)value).doubleValue());
		else
			return value.toString();
	}


	@Override
	public abstract Object clone();

	
}
