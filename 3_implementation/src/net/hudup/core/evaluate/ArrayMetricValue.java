/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;

/**
 * This abstract class represents an array of real numbers as a {@link MetricValue}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ArrayMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * An array of metric values.
	 */
	protected List<MetricValue> array = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ArrayMetricValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with list of metric values.
	 * @param array specified list of metric values.
	 */
	public ArrayMetricValue(List<MetricValue> array) {
		super();
		// TODO Auto-generated constructor stub
		
		this.array.addAll(array);
	}

	
	/**
	 * Constructor with array of metric values.
	 * @param array specified array of metric values.
	 */
	public ArrayMetricValue(MetricValue... array) {
		super();
		
		for (MetricValue value : array)
			this.array.add(value);
	}
	
	
	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		return array.size() > 0;
	}

	
	@Override
	public void accum(MetricValue metricValue) throws RemoteException {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed())
			return;
		
		array.add(metricValue);
	}


	/**
	 * Getting the metric value of the internal array of real number {@link #array} at specified index.
	 * @param index specified index.
	 * @return metric value of the internal array of real number {@link #array} at specified index.
	 */
	public MetricValue getElement(int index) {
		return array.get(index);
	}
	
	
	/**
	 * Getting the element count of the internal array of real number {@link #array}.
	 * @return element count of the internal array of real number {@link #array}.
	 */
	public int getElementCount() {
		return array.size();
	}
	
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		array.clear();
	}

	
	@Override
	public abstract Object clone();
	
	
}
