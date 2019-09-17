/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.util.List;

import net.hudup.core.Util;

/**
 * This abstract class represents an array of real mean values.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RealArrayMeanMetricValue extends ArrayMeanMetricValue {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RealArrayMeanMetricValue() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with list of real values.
	 * @param valueList list of real values.
	 */
	public RealArrayMeanMetricValue(List<Double> valueList) {
		initialize(valueList);
	}

	
	@Override
	public void initialize(Object value) {
		// TODO Auto-generated method stub
		reset();
		if (value == null || !(value instanceof List<?>))
			return;
		
		List<Object> thisValueList = getValueList();
		thisValueList.clear();
		List<?> valueList = (List<?>)value;
		for (Object v : valueList)
			thisValueList.add(((Number)v).doubleValue());
		
		this.count = 1;
	}


	@Override
	public Object makeMean(Object currentMean, int currentCount, Object newValue) {
		// TODO Auto-generated method stub
		if (!(currentMean instanceof Number) || !(newValue instanceof Number))
			return null;
		
		double currentM = ((Number)currentMean).doubleValue(); 
		double newV = ((Number)newValue).doubleValue(); 
		currentM = (currentM * currentCount) / (currentCount + 1) + 
				newV / (currentCount + 1);
		
		return currentM;
	}

	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		if (!isUsed())
			return new RealArrayMeanMetricValue();
		
		List<Object> thisList = getValueList();
		List<Double> valueList = Util.newList();
		for (Object value : thisList)
			valueList.add(((Number)value).doubleValue());
			
		RealArrayMeanMetricValue mean = new RealArrayMeanMetricValue(valueList);
		mean.count = this.count;
		return mean;
	}
	

}
