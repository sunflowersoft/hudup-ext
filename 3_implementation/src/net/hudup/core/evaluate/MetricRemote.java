package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemote;

/**
 * This interface represents remote metri.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface MetricRemote extends AlgRemote {

	
	/**
	 * Getting the type of this metric, for example, &quot;Time&quot;, &quot;Predictive Accuracy&quot;, and &quot;Classification Accuracy&quot; 
	 * @return metric type name
	 * @throws RemoteException if any error raises.
	 */
	String getTypeName() throws RemoteException;
	
	
	/**
	 * Setting up (Initializing) this metric based on an array of objects known as parameters
	 * @param params specified array of objects known as parameters. The parameters should be serializable.
	 * @throws RemoteException if any error raises.
	 */
	void setup(Object... params) throws RemoteException;
	
	
	/**
	 * Methods {@link #getAccumValue()} and {@link #getAccumValue()} return accumulative value and current value of metric.
	 * After each time {@code metric} is re-calculated by {@link #recalc(Object...)} method, accumulative value and current value can be changed.
	 * For example, a sample {@code metric} receives values 3, 1, 2 at the first, second, and third calculations.
	 * At the fourth calculation if {@link #recalc(Object...)} produces value 2, the {@link #getCurrentValue()} will return 2 and the {@link #getAccumValue()} will return 3 + 1 + 2 + 2 = 8.
	 * Methods {@link #getAccumValue()} and {@link #getCurrentValue()} can return any thing and so their returned value is represented by {@link MetricValue} interface.
	 * How to implement {@code MetricValue} is dependent on concrete application.
	 * @return current {@link MetricValue}
	 * @throws RemoteException if any error raises.
	 */
	MetricValue getCurrentValue() throws RemoteException;
	
	
	/**
	 * Methods {@link #getAccumValue()} and {@link #getAccumValue()} return accumulative value and current value of metric.
	 * After each time {@code metric} is re-calculated by {@link #recalc(Object...)} method, accumulative value and current value can be changed.
	 * For example, a sample {@code metric} receives values 3, 1, 2 at the first, second, and third calculations.
	 * At the fourth calculation if {@link #recalc(Object...)} produces value 2, the {@link #getCurrentValue()} will return 2 and the {@link #getAccumValue()} will return 3 + 1 + 2 + 2 = 8.
	 * Methods {@link #getAccumValue()} and {@link #getCurrentValue()} can return any thing and so their returned value is represented by {@link MetricValue} interface.
	 * How to implement {@code MetricValue} is dependent on concrete application.
	 * @return accumulated {@link MetricValue}
	 * @throws RemoteException if any error raises.
	 */
	MetricValue getAccumValue() throws RemoteException;

	
	/**
	 * This is the most important method expressing how to re-calculate a concrete {@code metric} according to specified parameters.
	 * @param params specified array of objects known as parameters. Such parameters varied according to concrete metric.
	 * The parameters should be serializable.
	 * @return whether calculating successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean recalc(Object... params) throws RemoteException;
	
	
	/**
	 * Reseting this metric, which makes this metric in original status.
	 * @throws RemoteException if any error raises.
	 */
	void reset() throws RemoteException;
	
	
	/**
	 * Testing whether or not this metric is valid.
	 * @return whether or not this metric is valid.
	 * @throws RemoteException if any error raises.
	 */
	boolean isValid() throws RemoteException;
	
	
}
