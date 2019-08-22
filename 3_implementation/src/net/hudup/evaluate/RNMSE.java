package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.OperatorFractionMetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class represents NRMSE metric for recommendation algorithms. It is now deprecated.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RNMSE extends NMSE {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RNMSE() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub
		FractionMetricValue fraction = (FractionMetricValue)super.calc(recommended, vTesting, testing);
		
		if (fraction == null)
			return null;
		else
			return new OperatorFractionMetricValue(fraction) {
				
				/**
				 * Default serial version UID.
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected double operator(double value) {
					// TODO Auto-generated method stub
					return Math.sqrt(value);
				}
			};
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RNMSE.recommend";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Root Normalized Mean Squared Error for recommendation algorithm";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Predictive accuracy";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RNMSE();
	}


}



/**
 * This class represents NRMSE metric for recommendation algorithms. It is now deprecated.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
class NRMSEDeprecated extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NRMSEDeprecated() {
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
		return "RNMSE.recommend.deprecated";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Root Normalized Mean Squared Error (deprecated) for recommendation algorithm";
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
		return new NRMSEDeprecated();
	}


}