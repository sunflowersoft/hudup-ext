package net.hudup.core.evaluate;

import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.BaseClass;


/**
 * Meta-metric with mean value.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@BaseClass
public class MeanMetaMetric extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MeanMetaMetric() {
		super();
	}
	
	
	/**
	 * Setup internal metric.
	 * @param point specified internal metric.
	 */
	public void setup(Metric point) {
		// TODO Auto-generated method stub
		super.setup(new Object[] { point });
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return meta[0].getName();
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return meta[0].getDesc();
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return meta[0].getTypeName();
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if ( params == null || params.length < 1 || !(params[0] instanceof Metric) )
			return false;
		
		Metric metric = (Metric) params[0];
		if ( !metric.getName().equals(meta[0].getName()) || !metric.isValid() )
			return false;
		
		MetricValue metricValue = metric.getAccumValue();
		if (metricValue == null || !metricValue.isUsed())
			return false;
		
		Object value = metricValue.value();
		MeanMetricValue mean = null;
		if (metric instanceof DefineMeanMetric)
			mean = ((DefineMeanMetric)metric).createMeanMetricValue();
		else if (metric instanceof MetricWrapper) {
			Metric inner = ((MetricWrapper)metric).getMetric();
			if (inner instanceof DefineMeanMetric)
				mean = ((DefineMeanMetric)inner).createMeanMetricValue();
		}
		if (mean != null) {
			mean.initialize(value);
			return recalc0(mean);
		}
		
		List<Double> list = parseRealNumberList(value);
		if (list.size() == 0)
			return false;
		else if (list.size() == 1) {
			value = list.get(0);
			mean = new RealMeanMetricValue();
		}
		else {
			value = list;
			mean = new RealArrayMeanMetricValue();
		}
		
		mean.initialize(value);
		return recalc0(mean);
	}


	/**
	 * Parsing a list of real number from specified parameter.
	 * @param param specified parameter.
	 * @return list of real number parsed from specified parameter.
	 */
	public static List<Double> parseRealNumberList(Object param) {
		List<Double> vector = Util.newList();

		if (param instanceof Collection<?>) {
			Collection<?> valueList = (Collection<?>)param;
			for (Object v : valueList) {
				if (v instanceof Number)
				vector.add(((Number)v).doubleValue());
			}
		}
		else if (param instanceof double[]) {
			double[] valueList = (double[])param;
			for (double v : valueList)
				vector.add(v);
		}
		else if (param instanceof Double[]) { // Add on August 16, 2018. Check it later.
			Double[] valueList = (Double[])param;
			for (double v : valueList)
				vector.add(v);
		}
		else if (param instanceof Object[]) { // Add on August 16, 2018. Check it later.
			Object[] valueList = (Object[])param;
			for (Object v : valueList) {
				if (v instanceof Number)
					vector.add(((Number)v).doubleValue());
			}
		}
		else if (param instanceof Number)
			vector.add(((Number)param).doubleValue());
		
		return vector;
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MeanMetaMetric();
	}

	
}
