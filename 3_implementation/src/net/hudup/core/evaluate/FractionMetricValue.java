/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.MathUtil;

/**
 * This abstract class represents fraction (a/b) as a {@link MetricValue}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FractionMetricValue implements MetricValue /*, Comparable<FractionMetricValue>*/ {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Numerator of the fraction.
	 */
	protected double a = 0;
	
	
	/**
	 * Denominator of the fraction.
	 */
	protected double b = 0;
	
	
	/**
	 * Constructor with the numerator and denominator.
	 * @param a specified numerator.
	 * @param b specified denominator.
	 */
	public FractionMetricValue(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	
	/**
	 * Getting the numerator of this fraction.
	 * @return numerator of this fraction.
	 */
	public double a() {
		return a;
	}
	
	
	/**
	 * Getting the denominator of this fraction.
	 * @return denominator of this fraction.
	 */
	public double b() {
		return b;
	}
	
	
	@Override
	public Object value() {
		return a / b;
	}
	
	
	@Override
	public void accum(MetricValue metricValue) throws RemoteException {
		if (metricValue == null || !metricValue.isUsed() || 
				!(metricValue instanceof FractionMetricValue))
			return;
		
		FractionMetricValue fraction = (FractionMetricValue)metricValue;
		if (!isUsed()) {
			this.a = fraction.a();
			this.b = fraction.b();
		}
		else {
			this.a += fraction.a();
			this.b += fraction.b();
		}
	}


	@Override
	public boolean isUsed() {
		return Util.isUsed(a) && Util.isUsed(b) && b != 0 ;
	}


	@Override
	public void reset() {
		a = 0;
		b = 0;
	}


	@Override
	public Object clone() {
		return new FractionMetricValue(a, b);
	}
	
	
	@Override
	public String toString() {
		double value = (b == 0 ? Constants.UNUSED : ((Number)value()).doubleValue());
		return MathUtil.format(value);
	}


//	@Override
//	public int compareTo(FractionMetricValue o) {
//		return ((Double)this.value()).compareTo((Double)o.value());
//	}
	
	
}
