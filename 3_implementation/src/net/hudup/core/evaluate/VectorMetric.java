package net.hudup.core.evaluate;

import java.rmi.RemoteException;

/**
 * This abstract class represents any vector of metrics.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class VectorMetric extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public VectorMetric() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void setup(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(params);
		if (params == null || params.length < 1 || params[0] == null || !(params[0] instanceof VectorMetricValue))
			return;
		accumValue = currentValue = (VectorMetricValue)(params[0]);
	}


	@Override
	public boolean recalc(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		if (params == null || params.length < 1)
			return false;
		if (!isValid())
			setup(new VectorMetricValue());
		
		try {
			MetricValue value = parseParams(params);
			if (value != null) {
				((VectorMetricValue)accumValue).accum(value);
				return true;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return false;

	}

	
	/**
	 * Parsing list of parameters into a metric value as an element of internal vector.
	 * @param params list of parameters.
	 * @return metric value parsed from list of parameters. 
	 */
	protected abstract MetricValue parseParams(Object... params);


}
