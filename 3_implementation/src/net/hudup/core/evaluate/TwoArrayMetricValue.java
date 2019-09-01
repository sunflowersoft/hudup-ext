package net.hudup.core.evaluate;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;


/**
 * This abstract class represents two arrays of real numbers as a {@link MetricValue}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * How to calculate the real number of these two arrays depends on particular application, which means that
 * any class completes this abstract class must complete the method {@link #value()} in order to calculate the real number from such two arrays.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class TwoArrayMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * First array of metric values.
	 */
	protected List<MetricValue> array0 = Util.newList();
	
	
	/**
	 * Second array of metric values.
	 */
	protected List<MetricValue> array1 = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public TwoArrayMetricValue() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with two arrays of metric values.
	 * @param array0 first array of of metric values.
	 * @param array1 second array of of metric values.
	 */
	public TwoArrayMetricValue(List<MetricValue> array0, List<MetricValue> array1) {
		// TODO Auto-generated constructor stub
		
		int n = Math.min(array0.size(), array1.size());
		if (n == 0)
			return;
		
		this.array0.addAll(array0.subList(0, n));
		this.array1.addAll(array1.subList(0, n));
	}
	
	
	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		return array0.size() > 0 && array1.size() > 0;
	}

	
	@Override
	public void accum(MetricValue metricValue) throws RemoteException {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed() || !(metricValue instanceof ArrayMetricValue))
			return;
		
		ArrayMetricValue array = (ArrayMetricValue)metricValue;
		if (array.getElementCount() < 2)
			return;
		
		MetricValue value0 = array.getElement(0);
		MetricValue value1 = array.getElement(1);
		accum(value0, value1);
	}

	
	/**
	 * Adding the first specified metric value {@code value0} and the second specified metric value {@code value1} into the first internal array {@link #array0} and the second internal array {@link #array1}, respectively.
	 * @param value0 the first specified metric value.
	 * @param value1 the second specified metric value.
	 */
	public void accum(MetricValue value0, MetricValue value1) {
		if (value0.isUsed() && value1.isUsed()) {
			array0.add(value0);
			array1.add(value1);
		}
	}
	
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		array0.clear();
		array1.clear();
	}

	
	@Override
	public abstract Object clone();
	
}
