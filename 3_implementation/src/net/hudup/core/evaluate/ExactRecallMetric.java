/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

/**
 * This class implements recall metric for estimation on query ID.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExactRecallMetric extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExactRecallMetric() {

	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Exact recall";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Hudup";
	}

	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
		if (params == null || params.length != 1 || !(params[0] instanceof FractionMetricValue))
			return false;

		FractionMetricValue fraction = (FractionMetricValue)params[0];
		if (!fraction.isUsed())
			return false;
		
		return recalc0(fraction);
	}

	
	@Override
	public String getName() {
		return "Exact recall";
	}


}
