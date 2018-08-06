package net.hudup.core.evaluate.testing;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMetricValue;

/**
 * This class represents root mean squared error for evaluating dual regression expectation maximization (EM) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RMSE extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public RMSE() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Setting up this RMSE by internal MSE.
	 * @param mse internal MSE.
	 */
	public void setup(MSE mse) {
		super.setup( new Object[] { mse } );
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RMSE.test";
	}
	
	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Root Mean Squared Error for testing algorithms";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Accuracy";
	}
	
	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if ( meta == null || meta.length < 1 || meta[0] == null ||
				(!(meta[0] instanceof MSE)) )
			return false;
		
		MSE mse = (MSE)meta[0];
		if (!mse.isValid() || !mse.getCurrentValue().isUsed() || !mse.getAccumValue().isUsed())
			return false;
		
		double currentValue = MetricValue.extractRealValue(mse.getCurrentValue());
		this.currentValue = new RealMetricValue(Math.sqrt(currentValue));
		
		double accumValue = MetricValue.extractRealValue(mse.getAccumValue());
		this.accumValue = new RealMetricValue(Math.sqrt(accumValue));
		
		return true;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RMSE();
	}

	
}
