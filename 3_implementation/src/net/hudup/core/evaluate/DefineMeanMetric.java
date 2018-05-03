package net.hudup.core.evaluate;


/**
 * This interface represents enhanced metric which defines owned mean value.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface DefineMeanMetric extends Metric {

	
	/**
	 * This method define mean metric value.
	 * @return defined mean metric value.
	 */
	MeanMetricValue createMeanMetricValue();
	
	
}
