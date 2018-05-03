package net.hudup.em.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.Accuracy;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;

/**
 * This class represents mean absolute error for evaluating dual regression EM algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MAE extends Accuracy {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public MAE() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Mean Absolute Error for Dual Regression EM";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Accuracy";
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MAE.drem";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MAE();
	}

	
	@Override
	protected MetricValue calc(double resultedValue, double testingValue) {
		// TODO Auto-generated method stub
		double d = resultedValue - testingValue;
		return new FractionMetricValue(Math.abs(d), 1);		
	}

	
}
