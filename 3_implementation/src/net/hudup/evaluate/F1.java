package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.logistic.NextUpdate;


/**
 * This class represents F1 metric.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class F1 extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public F1() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Setting up F1 metric with precision metric and recall metric.
	 * @param precision precision metric.
	 * @param recall recall metric.
	 */
	public void setup(Precision precision, Recall recall) {
		// TODO Auto-generated method stub
		super.setup(new Object[] { precision, recall });
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "F1.recommend";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Classification accuracy";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "F1 for recommendation algorithm";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		
		if ( meta == null || meta.length < 2 || meta[0] == null || meta[1] == null || 
				(!(meta[0] instanceof Precision)) || 
				(!(meta[1] instanceof Recall)) )
			return false;
		
		Precision p = (Precision) meta[0];
		Recall r = (Recall) meta[1];
		if (!p.isValid() || !r.isValid() ||
				!p.getCurrentValue().isUsed() || !r.getCurrentValue().isUsed() ||
				!p.getAccumValue().isUsed() || !r.getAccumValue().isUsed()
			)
			return false;
		
		double pCurrentValue = MetricValue.extractRealValue(p.getCurrentValue());
		double rCurrentValue = MetricValue.extractRealValue(r.getCurrentValue());
		this.currentValue = new RealMetricValue(
				(2 * pCurrentValue * rCurrentValue) / 
				(pCurrentValue + rCurrentValue));
		
		double pAccumValue = MetricValue.extractRealValue(p.getAccumValue());
		double rAccumValue = MetricValue.extractRealValue(r.getAccumValue());
		this.accumValue = new RealMetricValue(
				(2 * pAccumValue * rAccumValue) / 
				(pAccumValue + rAccumValue));
		
		return true;
	}


	@Override
	protected boolean recalc0(MetricValue metricValue) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("Not implement this method");
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new F1();
	}


}
