package net.hudup.core.evaluate;

import java.rmi.RemoteException;

/**
 * This abstract class represents any metric having two internal arrays for calculate evaluation value such as Pearson correlation.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class TwoArrayMetric extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Default constructor.
	 */
	public TwoArrayMetric() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void setup(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(params);
		if (params == null || params.length < 1 || params[0] == null || !(params[0] instanceof TwoArrayMetricValue))
			return;
		accumValue = currentValue = (TwoArrayMetricValue)(params[0]);
	}


	@Override
	public boolean recalc(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		if (params == null || params.length < 1)
			return false;
		if (!isValid())
			setup(createMetricValue());
		
		try {
			MetricValue[] values = parseParams(params);
			if (values != null && values.length >= 2) {
				((TwoArrayMetricValue)accumValue).accum(values[0], values[1]);
				return true;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Create two-array metric value.
	 * @return two-array metric value.
	 */
	protected abstract TwoArrayMetricValue createMetricValue();
	
	
	/**
	 * Parsing the list of parameter into two metric values as elements of internal arrays.
	 * @param params specified list of parameter.
	 * @return two metric values parsed from specified list of parameter. 
	 */
	protected abstract MetricValue[] parseParams(Object... params);
	
	
}
