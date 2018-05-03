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
		
		if (value instanceof Collection) {
			List<Double> list = Util.newList();
			Collection<?> valueList = (Collection<?>)value;
			if (valueList.size() == 0)
				return false;
			for (Object v : valueList) {
				if (!(v instanceof Number))
					return false;
				else
					list.add(((Number)v).doubleValue());
			}
			value = list;
			mean = new RealArrayMeanMetricValue();
		}
		else if (value instanceof double[]) {
			List<Double> list = Util.newList();
			double[] valueList = (double[])value;
			if (valueList.length == 0)
				return false;
			for (double v : valueList) {
				list.add(v);
			}
			value = list;
			mean = new RealArrayMeanMetricValue();
		}
		else if (value instanceof Number)
			mean = new RealMeanMetricValue();
		
		if (mean != null) {
			mean.initialize(value);
			return recalc0(mean);
		}
		else
			return false;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MeanMetaMetric();
	}

	
}
