package net.hudup.core.evaluate;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.MathUtil;

/**
 * This abstract class represents an array of mean values.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ArrayMeanMetricValue extends MeanMetricValue {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ArrayMeanMetricValue() {
		// TODO Auto-generated constructor stub
		reset();
	}

	
	/**
	 * Constructor with specified list of values.
	 * @param valueList specified list of values.
	 */
	public ArrayMeanMetricValue(List<Object> valueList) {
		// TODO Auto-generated constructor stub
		initialize(valueList);
	}

	
	@Override
	public void initialize(Object value) {
		// TODO Auto-generated method stub
		reset();
		if (value == null || !(value instanceof List))
			return;
		List<Object> thisValueList = getValueList();
		thisValueList.clear();
		
		List<?> valueList = (List<?>)value;
		for (Object v : valueList)
			thisValueList.add(v);
		this.count = 1;
	}


	/**
	 * Getting list of values.
	 * @return list of values.
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getValueList() {
		return ((List<Object>)this.value);
	}
	
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.value = (List<Object>) Util.newList();
		this.count = 0;
	}


	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		if (this.value == null || this.count == 0)
			return false;
		else
			return getValueList().size() > 0;
	}


	@Override
	public void accum(MetricValue metricValue) throws RemoteException {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed() || 
				!(metricValue instanceof ArrayMeanMetricValue))
			return;
		
		List<Object> otherList = ((ArrayMeanMetricValue)metricValue).getValueList();
		if (!isUsed()) {
			this.value = otherList;
			this.count = 1;
		}
		else {
			List<Object> thisList = this.getValueList();
			int n = Math.min(thisList.size(), otherList.size());
			if (n == 0)
				return;
			
			List<Object> list = Util.newList();
			for (int i = 0; i < n; i++) {
				Object thisMean = thisList.get(i);
				Object otherMean = otherList.get(i);
				Object mean = makeMean(thisMean, this.count, otherMean);
				if (mean != null)
					list.add(mean);
			}
			
			if (list.size() > 0) {
				this.value = list;
				this.count ++;
			}
		}

	}


	/**
	 * Computing a mean based on current mean and new value.
	 * @param currentMean current mean.
	 * @param currentCount current count.
	 * @param newValue new value.
	 * @return a mean based on current mean and new value.
	 */
	public abstract Object makeMean(Object currentMean, int currentCount, Object newValue);


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (!isUsed())
			return "";
		
		List<Object> valueList = getValueList();
		return toString(valueList);
	}
	
	
	/**
	 * Convert a list of values to string.
	 * @param valueList specified list of values.
	 * @return string converted from a list of values.
	 */
	public static String toString(List<Object> valueList) {
		if (valueList == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < valueList.size(); i++) {
			if (i == 0)
				buffer.append("[");
			else
				buffer.append(", ");
			Object value = valueList.get(i);
			if (value instanceof Number)
				buffer.append(MathUtil.format(((Number)value).doubleValue()));
			else
				buffer.append(value.toString());
			if (i == valueList.size() - 1)
				buffer.append("]");
		}
		
		return buffer.toString();
	}
	
	
}
