/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.gfall;

import java.rmi.RemoteException;

import net.hudup.core.logistic.NextUpdate;


/**
 * This class implements Green Fall algorithm with maximum improvement.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public class GreenFallMaxiCF extends GreenFallCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public GreenFallMaxiCF() {
		super();
	}


	@Override
	public String getName() {
		return "gfallmaxi";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Maximal Green Fall algorithm";
	}


	@Override
	protected FreqItemsetFinder createFreqItemsetFinder() {
		YRollerMaxi yrollermaxi = new YRollerMaxi();
		
		return yrollermaxi;
	}


}
