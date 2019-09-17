/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;

/**
 * This abstract class represents a vector of metric values.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class VectorMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal vector of metric values.
	 */
	protected MetricValue[] metricValues = null;
	
	
	/**
	 * Constructor with a specified vector of metric values.
	 * @param metricValues specified vector of metric values.
	 */
	public VectorMetricValue(MetricValue...metricValues) {
		this.metricValues = metricValues;
	}
	
	
	/**
	 * Constructor with a specified collection of metric values.
	 * @param metricValues specified collection of metric values.
	 */
	public VectorMetricValue(Collection<MetricValue> metricValues) {
		this.metricValues = metricValues.toArray(new MetricValue[] {});
	}

	
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		if (!this.isUsed())
			return null;
		
		List<Object> values = Util.newList();
		for (int i = 0; i < this.metricValues.length; i++) {
			values.add(this.metricValues[i].value());
		}
		return values;
	}

	
	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		if (this.metricValues == null || this.metricValues.length == 0)
			return false;
		
		return true;
	}

	
	@Override
	public void accum(MetricValue metricValue) throws RemoteException {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed() || 
				!(metricValue instanceof VectorMetricValue))
			return;
		
		VectorMetricValue vector = (VectorMetricValue)metricValue;
		
		if (!this.isUsed())
			this.metricValues = vector.metricValues;
		else {
			for (int i = 0; i < this.metricValues.length; i++)
				this.metricValues[i].accum(vector.metricValues[i]);
		}
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.metricValues = null;
	}

	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		VectorMetricValue newVector = new VectorMetricValue();
		if (!this.isUsed())
			return newVector;
		
		newVector.metricValues = new MetricValue[this.metricValues.length];
		for (int i = 0; i < this.metricValues.length; i++)
			newVector.metricValues[i] = (MetricValue)this.metricValues[i].clone();
			
		return newVector;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer("[");
		for (int i = 0; i < this.metricValues.length; i++) {
			if (i > 0)
				buffer.append(", ");
			buffer.append(this.metricValues[i].toString());
		}
		buffer.append("]");
		
		return buffer.toString();
	}
	
	
}
