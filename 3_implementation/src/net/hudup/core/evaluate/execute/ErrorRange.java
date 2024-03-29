/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;
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

	}

	
	@Override
	public String getName() {
		return "ErrorRange.exe";
	}
	
	
	@Override
	public String getTypeName() throws RemoteException {
		return "Accuracy";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Error range for executable algorithms";
	}
	

	@Override
	protected ArrayMetricValue createMetricValue() {
		return new ErrorRangeMetricValue();
	}

	
	@Override
	protected MetricValue parseParams(Object... params) {
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
			ErrorRangeMetricValue errorRangeValue = new ErrorRangeMetricValue();
			errorRangeValue.update(this.array);
			return errorRangeValue;
		}

		@Override
		public String toString() {
			@SuppressWarnings("unchecked")
			List<Object> valueList = (List<Object>)value();
			return ArrayMeanMetricValue.toString(valueList);
		}
		
	}

	
}
