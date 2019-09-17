/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.logistic.MathUtil;

/**
 * This interface represents value of a metric, called {@code metric value}. It can be anything but in current implementation, it only reflects real number (double number) via the method {@link #value()}.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface MetricValue extends Serializable, Cloneable {

	
	/**
	 * Getting value of {@code MetricValue}. For example, such value is often mean number (average number).
	 * The returned object is often serializable. It can be geometric object.
	 * @return value of {@code MetricValue}.
	 */
	Object value();
	
	
	/**
	 * Testing whether or not this {@code metric value} is used.
	 * @return whether or not this {@code metric value} is used.
	 */
	boolean isUsed();
	
	
	/**
	 * Taking the accumulative operator. For example, the accumulative addition operator will add this {@code MetricValue} to the specified {@code MetricValue} and then,
	 * the result is set back to this {@code MetricValue}. 
	 * @param metricValue specified {@code MetricValue}.
	 * @throws RemoteException if any error raises.
	 */
	void accum(MetricValue metricValue) throws RemoteException;
	
	
	/**
	 * Resetting this {@code metric value}. For example, its real number returned by ({@link #value()}) is re-set to be 0.
	 */
	void reset();
	
	
	/**
	 * Utility method to convert a metric value to text.
	 * @param metricValue specified metric value.
	 * @return text converted from specified metric value.
	 */
	static String valueToText(MetricValue metricValue) {
		String textValue = "";
		if (metricValue != null && metricValue.isUsed()) {
			if (metricValue.value() instanceof Number)
				textValue = MathUtil.format(((Number)metricValue.value()).doubleValue());
			else
				textValue = metricValue.toString();
		}
		return textValue;
	}
	
	
	/**
	 * Extracting real value from metric value.
	 * @param metricValue specified metric value.
	 * @return real value extracted from metric value.
	 */
	static double extractRealValue(MetricValue metricValue) {
		if (metricValue == null || !metricValue.isUsed())
			return Constants.UNUSED;
		Object value = metricValue.value();
		if (value instanceof Number)
			return ((Number)value).doubleValue();
		else
			return Constants.UNUSED;
	}
	
	
}
