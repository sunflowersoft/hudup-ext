package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.OperatorFractionMetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class represents root mean squared error for evaluating dual regression expectation maximization (EM) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RMSE extends MSE {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RMSE() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RMSE.test";
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Root Mean Squared Error for testing algorithms";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Accuracy";
	}
	
	
	@Override
	protected MetricValue calc(double resultedValue, double testingValue) {
		// TODO Auto-generated method stub
		FractionMetricValue fraction = (FractionMetricValue)super.calc(resultedValue, testingValue);
		
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
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RMSE();
	}

	
}



/**
 * This class represents root mean squared error for evaluating dual regression expectation maximization (EM) algorithm.
 * It is now deprecated.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
@Deprecated
class RMSEDeprecated extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public RMSEDeprecated() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Setting up this RMSE by internal MSE.
	 * @param mse internal MSE.
	 * @throws RemoteException if any error raises.
	 */
	public void setup(MSE mse) throws RemoteException {
		super.setup( new Object[] { mse } );
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RMSE.test.deprecated";
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Root Mean Squared Error (deprecated) for testing algorithms";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Accuracy";
	}
	
	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
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
		return new RMSEDeprecated();
	}

	
}
