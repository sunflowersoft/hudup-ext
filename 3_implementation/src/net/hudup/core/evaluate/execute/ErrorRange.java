package net.hudup.core.evaluate.execute;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.ArrayMeanMetricValue;
import net.hudup.core.evaluate.ArrayMetric;
import net.hudup.core.evaluate.ArrayMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealArrayMetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.logistic.Vector;

/**
 * Error range metric to evaluate regression model.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ErrorRange extends ArrayMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ErrorRange() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ErrorRange.test";
	}
	
	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Accuracy";
	}


	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Error range for testing algorithms";
	}
	

	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ErrorRange();
	}

	
	@Override
	protected ArrayMetricValue createMetricValue() {
		// TODO Auto-generated method stub
		return new ErrorRangeMetricValue();
	}

	
	@Override
	protected MetricValue parseParams(Object... params) {
		// TODO Auto-generated method stub
		if (params == null || params.length < 2 || !(params[0] instanceof Number) || !(params[1] instanceof Number))
			return null;
		
		double value1 = ((Number)params[0]).doubleValue();
		double value2 = ((Number)params[1]).doubleValue();
		
		return new RealMetricValue(value1 - value2);
	}
	
	
	/**
	 * Error range metric value.
	 * @author Loc Nguyen
	 * @version 1.0
	 *
	 */
	protected class ErrorRangeMetricValue extends RealArrayMetricValue {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object value() {
			// TODO Auto-generated method stub
			Vector v = toVector();
			double mean = v.mean();
			double sd = Math.sqrt(v.mleVar());
			
			List<Double> valueList = Util.newList();
			if (Util.isUsed(sd)) {
				valueList.add(mean);
				valueList.add(sd);
			}
			return valueList;
		}

		@Override
		public Object clone() {
			// TODO Auto-generated method stub
			ErrorRangeMetricValue errorRangeValue = new ErrorRangeMetricValue();
			errorRangeValue.update(this.array);
			return errorRangeValue;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			@SuppressWarnings("unchecked")
			List<Object> valueList = (List<Object>)value();
			return ArrayMeanMetricValue.toString(valueList);
		}
		
	}

	
}
