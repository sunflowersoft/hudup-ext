package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.xURI;


/**
 * Note, the abstract class of {@code Metric} interface is {code AbstractMetric}.
 * Three implemented classes of {@code Metric} which inherit from {@code AbstractMetric} are {@code DefaultMetric}, {@code MetaMetric}, and {@code MetricWrapper} as follows:
 * <ul>
 * <li>The {@code DefaultMetric} class is default partial implementation of single {@code Metric}.</li>
 * <li>The {@code MetaMetric} class is a complex {@code Metric} which contains other metrics.</li>
 * <li>In some situations, if metric requires complicated implementation, it is wrapped by this {@code MetricWrapper} class.</li>
 * </ul>
 * As a convention, this {@code MetricWrapper} is called {@code metric wrapper}.
 * It contains an internal metric referred by its internal variable {@link #metric}. Actually, such internal metric calculates measure for evaluating an algorithm.
 * Moreover, {@code metric wrapper} contains additional information as follows:
 * <ul>
 * <li>Name of the algorithm (referred by {@link #algName}) on which the internal metric {@link #metric} evaluates.</li>
 * <li>Identifier (ID) referred by {@link #datasetId} of the dataset used to evaluate the algorithm.</li>
 * <li>URI (referred by {@link #datasetUri}) of the dataset used to evaluate the algorithm.</li>
 * </ul> 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@BaseClass
public class MetricWrapper extends MetricAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The internal metric actually calculates measure for evaluating an algorithm.
	 */
	protected Metric metric = null;
	
	
	/**
	 * Name of the algorithm (referred by {@link #algName}) on which the internal metric {@link #metric} evaluates.
	 */
	protected String algName = null;
	
	
	/**
	 * Identifier (ID) referred by {@link #datasetId} of (testing) dataset used to evaluate the algorithm.
	 */
	protected int datasetId = -1;
	
	
	/**
	 * URI (referred by {@link #datasetUri}) of (testing) dataset used to evaluate the algorithm.
	 */
	protected xURI datasetUri = null;
	
	
	/**
	 * Default constructor.
	 */
	public MetricWrapper() {
		
	}
	
	
	@Override
	public void setup(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		
		if (params != null && params.length >= 3 && 
				params[0] instanceof Metric && 
				params[1] instanceof String && 
				params[2] instanceof Integer) {
			
			this.metric = (Metric) params[0];
			this.algName = (String) params[1];
			this.datasetId = (Integer) params[2];
		}
		
	}


	/**
	 * Setting up this meta metric with specified metric, algorithm name, and dataset identifier.
	 * @param metric specified metric.
	 * @param algName specified algorithm name.
	 * @param datasetId specified (testing) dataset identifier.
	 * @throws RemoteException if any error raises.
	 */
	public void setup(Metric metric, String algName, int datasetId) throws RemoteException {
		setup( new Object[] { metric, algName, new Integer(datasetId) } );
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return metric.getName();
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return metric.getDescription();
	}



	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return metric.getTypeName();
	}



	@Override
	public MetricValue getCurrentValue() throws RemoteException {
		// TODO Auto-generated method stub
		return metric.getCurrentValue();
	}



	@Override
	public MetricValue getAccumValue() throws RemoteException {
		// TODO Auto-generated method stub
		return metric.getAccumValue();
	}


	@Override
	public boolean recalc(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		return metric.recalc(params);
	}


	@Override
	public void reset() throws RemoteException {
		// TODO Auto-generated method stub
		metric.reset();
	}

	
	@Override
	public boolean isValid() throws RemoteException {
		// TODO Auto-generated method stub
		return metric.isValid();
	}


	/**
	 * Getting the internal metric.
	 * @return internal {@link Metric}.
	 */
	public Metric getMetric() {
		return metric;
	}
	
	
	/**
	 * Getting the name of algorithm on which the internal metric evaluates.
	 * @return algorithm name.
	 */
	public String getAlgName() {
		return algName;
	}
	
	
	/**
	 * Getting identifier of (testing) dataset used to evaluate the algorithm.
	 * @return dataset id.
	 */
	public int getDatasetId() {
		return datasetId;
	}
	
	
	/**
	 * Getting URI of (testing) dataset used to evaluate the algorithm.
	 * @return URI of (testing) dataset used to evaluate the algorithm.
	 */
	public xURI getDatasetUri() {
		return datasetUri;
	}
	
	
	/**
	 * Setting URI of (testing) dataset used to evaluate the algorithm.
	 * @param uri specified URI.
	 */
	public void setDatasetUri(xURI uri) {
		this.datasetUri = uri;
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MetricWrapper();
	}


	/**
	 * Creating a metric wrapper with specified internal metric, algorithm name, identifier of (testing) dataset, and URI of (testing) dataset.
	 * This method is used to construct a metric wrapper. 
	 * @param metric specified internal metric actually calculates measure for evaluating an algorithm.
	 * @param algName specified name of the algorithm on which the internal metric evaluates.
	 * @param datasetId specified identifier (ID) of (testing) dataset used to evaluate the algorithm.
	 * @param datasetUri specified URI of (testing) dataset used to evaluate the algorithm.
	 * @return new metric wrapper with specified internal metric, algorithm name, identifier of (testing) dataset, and URI of (testing) dataset.
	 * @throws RemoteException if any error raises.
	 */
	public static MetricWrapper create(Metric metric, String algName, int datasetId, xURI datasetUri) throws RemoteException {
		if (metric == null || algName == null || algName.isEmpty() || datasetId == -1)
		//if (metric == null || algName == null || algName.isEmpty() || datasetId == -1 || datasetUri == null)
			return null;
		
		MetricWrapper wrapper = new MetricWrapper();
		wrapper.setup(metric, algName, datasetId);
		wrapper.setDatasetUri(datasetUri);
		
		return wrapper;
	}
	
	
	/**
	 * 
	 * Creating a metric wrapper with specified internal metric, algorithm name, and identifier of (testing) dataset.
	 * This method is used to construct a metric wrapper. 
	 * @param metric specified internal metric actually calculates measure for evaluating an algorithm.
	 * @param algName specified name of the algorithm on which the internal metric evaluates.
	 * @param datasetId specified identifier (ID) of (testing) dataset used to evaluate the algorithm.
	 * @return new metric wrapper with specified internal metric, algorithm name, and identifier of (testing) dataset.
	 * @throws RemoteException if any error raises.
	 */
	public static MetricWrapper create(Metric metric, String algName, int datasetId) throws RemoteException {
		return create(metric, algName, datasetId, null);
	}
	
	
}
