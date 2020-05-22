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
public class SpeedMetric extends TimeMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SpeedMetric() {

	}

	
	@Override
	public String getName() {
		return "Speed";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Speed (in seconds)";
	}


	@Override
	public String getTypeName() throws RemoteException {
		return "Time";
	}


}
