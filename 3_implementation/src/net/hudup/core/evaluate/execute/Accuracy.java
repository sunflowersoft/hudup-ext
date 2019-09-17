/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;

import net.hudup.core.evaluate.DefaultMetric;
import net.hudup.core.evaluate.MetricValue;

/**
 * This abstract class represents any metric to evaluate an algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class Accuracy extends DefaultMetric {

	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Accuracy() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		if (params == null || params.length < 2)
			return false;
		if ( !(params[0] instanceof Number) || !(params[1] instanceof Number))
			return false;
		
		double resultedValue = ((Number)(params[0])).doubleValue();
		double testingValue = ((Number)(params[1])).doubleValue();
		MetricValue metricValue = calc(resultedValue, testingValue);
		if (metricValue != null && metricValue.isUsed())
			return recalc0(metricValue);
		else
			return false;
	}


	/**
	 * Calculating the metric based on resulted value and testing value.
	 * @param resultedValue specified resulted value.
	 * @param testingValue specified testing value.
	 * @return metric based on resulted value and testing value.
	 */
	protected abstract MetricValue calc(double resultedValue, double testingValue);
	
	
}
