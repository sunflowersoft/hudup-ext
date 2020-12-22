/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

/**
 * 
 * Note, the abstract class of {@code Metric} interface is {code AbstractMetric}.
 * Three implemented classes of {@code Metric} which inherit from {@code AbstractMetric} are {@code DefaultMetric}, {@code MetaMetric}, and {@code MetricWrapper} as follows:
 * <ul>
 * <li>This {@code DefaultMetric} class is default partial implementation of single {@code Metric}.</li>
 * <li>{@code MetaMetric} class is a complex {@code Metric} which contains other metrics.</li>
 * <li>In some situations, if metric requires complicated implementation, it is wrapped by {@code MetricWrapper} class.</li>
 * </ul>
 * The most important aspect of this default metric is that default metric provides the method {@link #recalc0(MetricValue)}.
 * The {@link #recalc(Object...)} method will call the {@code #recalc0(MetricValue)} method.
 * The {@code #recalc0(MetricValue)} method is to accumulate metric value at each iteration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class DefaultMetric extends MetricAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Current value of this metric.
	 */
	protected MetricValue currentValue = null;
	
	
	/**
	 * Accumulated of this metric.
	 */
	protected MetricValue accumValue = null;
	
	
	/**
	 * Default constructor.
	 */
	public DefaultMetric() {
		super();
	}


	@Override
	public MetricValue getCurrentValue() throws RemoteException {
		return currentValue;
	}

	
	@Override
	public MetricValue getAccumValue() throws RemoteException {
		return accumValue;
	}

	
	@Override
	public void reset() throws RemoteException {
		currentValue = null;
		accumValue = null;
	}


	/**
	 * The {@link #recalc(Object...)} method will call the {@link #recalc0(MetricValue)} method.
	 * This method is to accumulate metric value at each iteration.
	 *  
	 * @param metricValue specified metric value.
	 * @return whether calculating successfully.
	 * @throws RemoteException if any error raises.
	 */
	protected boolean recalc0(MetricValue metricValue) throws RemoteException {
		if (metricValue == null)
			return false;
		
		currentValue = (MetricValue) metricValue.clone();
		if (accumValue == null)
			accumValue = (MetricValue) metricValue.clone();
		else
			accumValue.accum(metricValue);
		
		return true;
	}


	@Override
	public boolean isValid() throws RemoteException {
		return currentValue != null && accumValue != null;
	}


}
