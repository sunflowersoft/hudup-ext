/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.Util;

/**
 * This abstract class represents a real average number.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RealMeanMetricValue extends MeanMetricValue /*implements Comparable<RealMeanMetricValue>*/ {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public RealMeanMetricValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified value. The count is automatically set to be 1.
	 * @param value specified value.
	 */
	public RealMeanMetricValue(double value) {
		// TODO Auto-generated constructor stub
		initialize(value);
	}


	@Override
	public void initialize(Object value) {
		// TODO Auto-generated method stub
		reset();
		if (value != null && value instanceof Number) {
			this.value = ((Number)value).doubleValue();
			this.count = 1;
		}
	}
	
	
	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		if (!super.isUsed())
			return false;
		else
			return Util.isUsed((Double)value);
	}

	
	@Override
	public void accum(MetricValue metricValue) throws RemoteException {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed() || 
				!(metricValue instanceof RealMeanMetricValue))
			return;
		
		RealMeanMetricValue mean = (RealMeanMetricValue)metricValue;
		if (!isUsed()) {
			this.value = (Double)mean.value;
			this.count = 1;
		}
		else {
			double value = (Double)mean.value;
			double thisValue = (Double)this.value;
			
			this.value = (thisValue * this.count) / (this.count + 1) + 
						   value / (this.count + 1); 
			this.count ++;
		}
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		if (!isUsed())
			return new RealMeanMetricValue();
		
		RealMeanMetricValue mean = new RealMeanMetricValue((Double)value);
		mean.count = this.count;
		return mean;
	}


//	@Override
//	public int compareTo(RealMeanMetricValue o) {
//		// TODO Auto-generated method stub
//		Double v1 = ((Number)this.value).doubleValue();
//		Double v2 = ((Number)o.value).doubleValue();
//		return v1.compareTo(v2);
//	}

	
}
