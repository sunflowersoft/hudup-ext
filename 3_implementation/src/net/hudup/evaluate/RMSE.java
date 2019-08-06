package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.logistic.NextUpdate;


/**
 * This class represents root mean squared error for evaluating recommendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class RMSE extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RMSE() {
		super();
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
		return "RMSE.recommend";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Root Mean Squared Error for recommendation algorithm";
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
