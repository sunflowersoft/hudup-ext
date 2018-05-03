package net.hudup.core.evaluate;

import java.util.List;

import net.hudup.core.logistic.Vector;


/**
 * This class represents a metric value of one array of real numbers.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class RealArrayMetricValue extends ArrayMetricValue {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public RealArrayMetricValue() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with an array of real numbers.
	 * @param array an array of real numbers.
	 */
	public RealArrayMetricValue(List<Double> array) {
		super();
		for (Double value : array)
			this.array.add(new RealMetricValue(value));
	}


	/**
	 * Update this one-array metric by a list of metric values.
	 * @param array list of metric values.
	 */
	public void update(List<MetricValue> array) {
		this.array.clear();
		
		for (MetricValue value : array)
			this.array.add(new RealMetricValue(MetricValue.extractRealValue(value)));
	}
	
	
	/**
	 * Converting the internal array into a vector.
	 * @return a vector converted from the internal array.
	 */
	public Vector toVector() {
		Vector v = new Vector(array.size(), 0);
		for (int i = 0; i < array.size(); i++) {
			RealMetricValue value = (RealMetricValue) array.get(i);
			v.set(i, MetricValue.extractRealValue(value));
		}
		return v;
	}
	
	
}
