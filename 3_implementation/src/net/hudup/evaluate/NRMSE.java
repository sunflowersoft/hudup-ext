package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMetricValue;


/**
 * This class represents NRMSE metric for recommendation algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NRMSE extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NRMSE() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Setting up this metric with other metric MSE.
	 * @param nmse specified other metric MSE.
	 */
	public void setup(NMSE nmse) {
		super.setup( new Object[] { nmse } );
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "NRMSE.recommend";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Normalized Root Mean Squared Error for recommendation algorithm";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Predictive accuracy";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if ( meta == null || meta.length < 1 || meta[0] == null || 
				(!(meta[0] instanceof NMSE)) )
			return false;
		
		NMSE nmse = (NMSE)meta[0];
		if (!nmse.isValid() || !nmse.getCurrentValue().isUsed() || !nmse.getAccumValue().isUsed())
			return false;
		
		double currentValue = MetricValue.extractRealValue(nmse.getCurrentValue());
		this.currentValue = new RealMetricValue(Math.sqrt(currentValue));
		
		double accumValue = MetricValue.extractRealValue(nmse.getAccumValue());
		this.accumValue = new RealMetricValue(Math.sqrt(accumValue));
		
		return true;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new NRMSE();
	}


}
