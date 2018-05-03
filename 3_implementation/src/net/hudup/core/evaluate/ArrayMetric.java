package net.hudup.core.evaluate;

/**
 * This abstract class represents any metric having one internal array for calculate evaluation value such as error range.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ArrayMetric extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public ArrayMetric() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void setup(Object... params) {
		// TODO Auto-generated method stub
		super.setup(params);
		if (params == null || params.length < 1 || params[0] == null || !(params[0] instanceof ArrayMetricValue))
			return;
		accumValue = currentValue = (ArrayMetricValue)(params[0]);
	}


	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if (params == null || params.length < 1)
			return false;
		if (!isValid())
			setup(createMetricValue());
		
		try {
			MetricValue value = parseParams(params);
			if (value != null) {
				((ArrayMetricValue)accumValue).accum(value);
				return true;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Create one-array metric value.
	 * @return one-array metric value.
	 */
	protected abstract ArrayMetricValue createMetricValue();
	
	
	/**
	 * Parsing list of parameters into a metric value as an element of internal array.
	 * @param params list of parameters.
	 * @return metric value parsed from list of parameters. 
	 */
	protected abstract MetricValue parseParams(Object... params);

	
}
