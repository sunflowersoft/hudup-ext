/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;

/**
 * This class represents ratio mean absolute error for evaluating testing algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MAERatio extends Accuracy {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Ratio Mean Absolute Error for executable algorithms";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Accuracy";
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MAERatio.exe";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MAERatio();
	}

	
	@Override
	protected MetricValue calc(double resultedValue, double testingValue) {
		// TODO Auto-generated method stub
		double d = resultedValue - testingValue;
		if (testingValue == 0)
			testingValue = Double.MIN_VALUE;
		return new FractionMetricValue(Math.abs(d) / Math.abs(testingValue), 1);		
	}

}
