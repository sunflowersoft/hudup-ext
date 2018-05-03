package net.hudup.core.evaluate;

import java.util.List;

import net.hudup.core.logistic.Vector;


/**
 * This class represents a metric value of two arrays of real numbers such as Pearson coefficient and cosine coefficient.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * Because this class extends directly {@link TwoArrayMetricValue}, it also contains two arrays of real numbers (two real vectors).
 * Users will implement {@link #value()} method to define Pearson coefficient or cosine coefficient, for example.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class RealTwoArrayMetricValue extends TwoArrayMetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RealTwoArrayMetricValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with two arrays of real numbers.
	 * @param array0 first array of of real numbers.
	 * @param array1 second array of of real numbers.
	 */
	public RealTwoArrayMetricValue(List<Double> array0, List<Double> array1) {
		super();
		for (Double value : array0)
			this.array0.add(new RealMetricValue(value));
		
		for (Double value : array1)
			this.array1.add(new RealMetricValue(value));
	}


	/**
	 * Update this two-array metric by two lists of metric values.
	 * @param array0 first list of metric values.
	 * @param array1 second list of metric values.
	 */
	public void update(List<MetricValue> array0, List<MetricValue> array1) {
		this.array0.clear();
		this.array1.clear();
		
		for (MetricValue value : array0)
			this.array0.add(new RealMetricValue(MetricValue.extractRealValue(value)));
		
		for (MetricValue value : array1)
			this.array1.add(new RealMetricValue(MetricValue.extractRealValue(value)));
	}
	
	
	/**
	 * Converting the first array into a vector.
	 * @return a vector converted from the first array.
	 */
	public Vector toVector0() {
		Vector v0 = new Vector(array0.size(), 0);
		for (int i = 0; i < array0.size(); i++) {
			RealMetricValue value = (RealMetricValue) array0.get(i);
			v0.set(i, MetricValue.extractRealValue(value));
		}
		return v0;
	}

	
	/**
	 * Converting the first array into a vector.
	 * @return a vector converted from the first array.
	 */
	public Vector toVector1() {
		Vector v1 = new Vector(array1.size(), 0);
		for (int i = 0; i < array1.size(); i++) {
			RealMetricValue value = (RealMetricValue) array1.get(i);
			v1.set(i, MetricValue.extractRealValue(value));
		}
		return v1;
	}
	
	
}
