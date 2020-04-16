/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
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
