/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		a = 0;
		b = 0;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new FractionMetricValue(a, b);
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		double value = (b == 0 ? Constants.UNUSED : ((Number)value()).doubleValue());
		return MathUtil.format(value);
	}


//	@Override
//	public int compareTo(FractionMetricValue o) {
//		// TODO Auto-generated method stub
//		return new Double((Double)this.value()).compareTo((Double)o.value());
//	}
	
	
}
