package net.hudup.core.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestingAlg;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.xURI;

/**
 * Metric wrapper with algorithm information.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass
public class MetricWrapper2 extends MetricWrapper {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Description of algorithm.
	 */
	protected String algDesc = "";
	
	
	/**
	 * Default constructor.
	 */
	public MetricWrapper2() {
		
	}
	
	
	@Override
	public void setup(Object... params) {
		// TODO Auto-generated method stub
		super.setup(params);
		if (params != null && params.length >= 4) 
			this.algDesc = (params[3] != null) ? params[3].toString() : "";
	}


	/**
	 * Setting up this meta metric with specified metric, algorithm name, and dataset identifier.
	 * @param metric specified metric.
	 * @param algName specified algorithm name.
	 * @param datasetId specified dataset identifier.
	 * @param algDesc description of algorithm.
	 */
	public void setup(Metric metric, String algName, int datasetId, String algDesc) {
		setup( new Object[] { metric, algName, new Integer(datasetId), algDesc } );
	}


	/**
	 * Getting algorithm description.
	 * @return algorithm description.
	 */
	public String getAlgDesc() {
		return algDesc;
	}
	
	
	/**
	 * Setting algorithm description.
	 * 
	 * @param algDesc specified algorithm description.
	 */
	public void setAlgDesc(String algDesc) {
		this.algDesc = algDesc;
	}
	
	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MetricWrapper2();
	}
	
	
	/**
	 * Creating a metric wrapper with specified internal metric, algorithm name, identifier of dataset, URI of dataset, and algorithm description.
	 * This method is used to construct a metric wrapper. 
	 * @param metric specified internal metric actually calculates measure for evaluating an algorithm.
	 * @param alg specified algorithm on which the internal metric evaluates.
	 * @param datasetId specified identifier (ID) of the dataset used to evaluate the algorithm.
	 * @param datasetUri specified URI of the dataset used to evaluate the algorithm.
	 * @return new metric wrapper with specified internal metric, algorithm name, identifier of dataset, and URI of dataset.
	 */
	public static MetricWrapper2 create(Metric metric, Alg alg, int datasetId, xURI datasetUri) {
		if (metric == null || alg == null || datasetId == -1)
			return null;
		
		MetricWrapper2 wrapper = new MetricWrapper2();
		String algDesc = (alg instanceof TestingAlg) ? ((TestingAlg)alg).getDescription() : "";
		wrapper.setup(metric, alg.getName(), datasetId, algDesc);
		wrapper.setDatasetUri(datasetUri);
		
		return wrapper;
	}
	
	
}
