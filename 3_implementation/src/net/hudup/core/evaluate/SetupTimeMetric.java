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
		super();
	}

	
	@Override
	public String getName() {
		return "Setup time";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Setup time (in seconds)";
	}


	@Override
	public String getTypeName() throws RemoteException {
		return "Time";
	}


}
