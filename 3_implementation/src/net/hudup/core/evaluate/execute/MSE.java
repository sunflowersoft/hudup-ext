package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;

/**
 * This class represents mean squared error for evaluating dual regression EM algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MSE extends Accuracy {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public MSE() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MSE.exe";
	}
	
	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Accuracy";
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Mean Squared Error for executable algorithms";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MSE();
	}

	
	@Override
	protected MetricValue calc(double resultedValue, double testingValue) {
		// TODO Auto-generated method stub
		double d = resultedValue - testingValue;
		return new FractionMetricValue(d * d, 1);		
	}

	
}
