package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;


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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Exact recall";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Hudup";
	}

	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		if (params == null || params.length != 1 || !(params[0] instanceof FractionMetricValue))
			return false;

		FractionMetricValue fraction = (FractionMetricValue)params[0];
		if (!fraction.isUsed())
			return false;
		
		return recalc0(fraction);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Exact recall";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ExactRecallMetric();
	}


}
