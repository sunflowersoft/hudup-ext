/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;

import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;

/**
 * This class represents mean absolute error for evaluating testing algorithms.
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

	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Mean Absolute Error for executable algorithms";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Accuracy";
	}

	
	@Override
	public String getName() {
		return "MAE.exe";
	}

	
	@Override
	protected MetricValue calc(double resultedValue, double testingValue) {
		double d = resultedValue - testingValue;
		return new FractionMetricValue(Math.abs(d), 1);		
	}

	
}
