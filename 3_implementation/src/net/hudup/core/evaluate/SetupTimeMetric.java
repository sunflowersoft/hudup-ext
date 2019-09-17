/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SetupTimeMetric extends TimeMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public SetupTimeMetric() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Setup time";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Setup time (in seconds)";
	}


	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Time";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SetupTimeMetric();
	}


}
