package net.hudup.core.evaluate.testing;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MeanMetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.VectorMetric;
import net.hudup.core.evaluate.VectorMetricValue;

/**
 * This class represents vector of mean absolute errors for evaluating testing algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MAEVector extends VectorMetric {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public MAEVector() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Vector of Mean Absolute Errors for testing algorithms";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Accuracy";
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MAEVector.test";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MAEVector();
	}

	
	@Override
	protected MetricValue parseParams(Object... params) {
		// TODO Auto-generated method stub
		if (params == null || params.length < 2)
			return null;
		
		List<Double> vector0 = MeanMetaMetric.parseRealNumberList(params[0]);
		if (vector0.size() == 0)
			return null;
		
		List<Double> vector1 = MeanMetaMetric.parseRealNumberList(params[1]);
		if (vector1.size() == 0)
			return null;
		
		int n = vector0.size();
		List<MetricValue> metricValues = Util.newList();
		for (int i = 0; i < n; i++) {
			double d = Math.abs(vector0.get(i) - vector1.get(i));
			FractionMetricValue value = new FractionMetricValue(d, 1);
			metricValues.add(value);
		}
		
		return new VectorMetricValue(metricValues);
	}

	
}
