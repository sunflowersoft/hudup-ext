/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.DSUtil;

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
	 * @throws RemoteException if any error raises.
	 */
	public void setup(Metric point) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(new Object[] { point });
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return meta[0].getName();
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return meta[0].getDescription();
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return meta[0].getTypeName();
	}

	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
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
		
		List<Double> list = DSUtil.toDoubleList(value, true);
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


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MeanMetaMetric();
	}

	
}
