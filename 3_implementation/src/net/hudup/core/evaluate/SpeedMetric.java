/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
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
public class SpeedMetric extends TimeMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SpeedMetric() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Speed";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Speed (in seconds)";
	}


	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Time";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SpeedMetric();
	}


}
